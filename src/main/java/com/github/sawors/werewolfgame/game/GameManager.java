package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.discord.ChannelType;
import com.github.sawors.werewolfgame.discord.DiscordManager;
import com.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

public class GameManager {

    private GameType gametype;
    private GamePhase gamephase;
    // playerlist's String -> UserId.toString()
    private HashMap<UserId, WerewolfPlayer> playerlist = new HashMap<>();
    private HashMap<String, UserId> discordlink = new HashMap<>();
    private HashMap<UUID, UserId> minecraftlink = new HashMap<>();
    private HashMap<GuildChannel, PlayerRole> rolechannels = new HashMap<>();
    private ArrayList<PlayerRole> rolelist = new ArrayList<>();
    private final String id;
    private final Guild guild;

    private ArrayList<Message> invites = new ArrayList<>();
    private Message leavemessage;

    private Category category;
    private JoinType jointype;
    private final String joinkey;
    private VoiceChannel mainvoicechannel;
    private TextChannel maintextchannel;
    private TextChannel adminchannel;
    private Role gamerole;
    private Role adminrole;
    private User owner;
    private int paramstringlength = 0;
    private Map<PlayerRole, Integer> rolepool = new HashMap<>();
    private List<PlayerRole> roleset;
    private Queue<GamePhase> eventqueue = new SynchronousQueue<>();
    private int round = 0;
    private String language;
    private boolean locked = false;
    
    

    public GameManager(Guild guild, GameType type, JoinType accessibility){
        this.id = Main.generateRandomGameId();
        this.gametype = type;
        this.guild = guild;
        this.jointype = accessibility;
        this.joinkey = RandomStringUtils.randomNumeric(5);
        this.language = DatabaseManager.getGuildLanguage(guild);
        
        createRoles(
                a0 -> guild.createCategory("[\uD83D\uDC3A WEREWOLF : "+id+"]")
                        .addRolePermissionOverride(gamerole.getIdLong(), List.of(Permission.VIEW_CHANNEL), List.of(Permission.MANAGE_CHANNEL))
                        .addRolePermissionOverride(adminrole.getIdLong(),List.of(Permission.VIEW_CHANNEL), List.of())
                        .addRolePermissionOverride(guild.getPublicRole().getIdLong(),List.of(), List.of(Permission.VIEW_CHANNEL))
                        .queue(a1 -> {
                    category = a1;
                    this.createChannels(category);
                    }
                )
        );
        
        for(PlayerRole role : Main.getRolePool()){
            Integer prio = role.priority();
            if(prio != null){
                rolepool.put(role, prio);
            }
        }
        
        Main.registerNewGame(this);
    }
    
    private String buildTutorial(){
        return TranslatableText.get("commands.admin.title",language)
                +"\n\n `clean` : "+TranslatableText.get("commands.admin.clean.description",language)
                +"\n\n `start` : "+TranslatableText.get("commands.admin.start.description",language)
                +"\n\n `lang` : "+TranslatableText.get("commands.admin.lang.description",language)
                +"\n\n `lock` : "+TranslatableText.get("commands.admin.lock.description",language)
                +"\n\n `unlock` : "+TranslatableText.get("commands.admin.unlock.description",language)
                +"\n\n `admin` : "+TranslatableText.get("commands.admin.admin.description",language)
        ;
    }
    
    private String buildInvite(){
        return TranslatableText.get("invites.invite-body-customizable",language);
    }

    public String getLanguage(){
        return language;
    }

    private void createRoles(Consumer<?> chainedaction){
        guild.createRole().setName("WW:"+id+":ADMIN").setMentionable(false).queue(a -> {
                    this.adminrole = a;
                    guild.createRole().setName("WW:" + id).setMentionable(false).queue(b -> {
                        this.gamerole = b;
                        if(owner != null){
                            setAdmin(owner);
                        }
                        chainedaction.accept(null);
                    });
                }
        )
        ;
    }
    
    //TODO : Find a better way to implement that
    // BE CAREFUL WHEN MODIFYING THE ORDER !
    public String getPreferencesString(){
        StringBuilder prefs = new StringBuilder();
        // pos 0
        prefs.append(jointype == JoinType.PUBLIC ? '0' : '1');
        paramstringlength++;
    
        /*// pos 1
        prefs.append(jointype == JoinType.PUBLIC ? '0' : '1');
        paramstringlength++;
    
        // pos 2
        prefs.append(jointype == JoinType.PUBLIC ? '0' : '1');
        paramstringlength++;
    
        // pos 3
        prefs.append(jointype == JoinType.PUBLIC ? '0' : '1');
        paramstringlength++;*/
        
        
        
        return prefs.toString();
    }
    
    // BE CAREFUL WHEN MODIFYING THE ORDER !
    public void setPreferences(String prefscode){
        for(int i = 0; i<=paramstringlength-1; i++){
            char c = prefscode.toCharArray()[i];
            switch(i){
                case 0:
                    if(c == '0'){
                        jointype = JoinType.PUBLIC;
                    } else if(c == '1'){
                        jointype = JoinType.PRIVATE;
                    }
                    break;
                case 1:
                case 2:
                case 3:
            }
        }
    }
    
    public void setOwner(User owner){
        this.owner = owner;
    }
    
    public User getOwner(){
        return this.owner;
    }
    
    public void setAdmin(User user){
        if(adminrole != null){
            guild.addRoleToMember(UserSnowflake.fromId(user.getId()), adminrole).queue();
        }
    }
    
    public void removeAdmin(User user){
        if(adminrole != null){
            guild.removeRoleFromMember(UserSnowflake.fromId(user.getId()), adminrole).queue();
        }
    }
    
    public void setLanguage(String locale){
        this.language = locale;
        adminchannel.getManager().setName(TranslatableText.get("channels.text.admin", language)).queue();
        maintextchannel.getManager().setName(TranslatableText.get("channels.text.main", language)).queue();
        mainvoicechannel.getManager().setName(TranslatableText.get("channels.voice.main", language)).queue();
        adminchannel.sendMessage(TranslatableText.get("commands.admin.lang.success", language)).queue(m -> adminchannel.sendMessageEmbeds(new EmbedBuilder().setDescription(buildTutorial()).setColor(0x89CFF0).build()).queue());
    }
    
    private void createChannels(Category category){
        if(this.category == null){
            this.category = category;
        }
        if(this.category != null){
            // create admin text channel
            setupTextChannel(TranslatableText.get("channels.text.admin",language), ChannelType.ADMIN);
            // create main text channel
            setupTextChannel(TranslatableText.get("channels.text.main",language), ChannelType.ANNOUNCEMENTS);
            
            // create main voice channel
            category.createVoiceChannel(TranslatableText.get("channels.voice.main",language)).queue(channel -> this.mainvoicechannel = channel);
        }
    }
    
    private void setupTextChannel(String name, ChannelType type){
        this.category.createTextChannel(name).queue(channel -> cacheChannel(channel, type));
    }
    
    private void cacheChannel(GuildChannel channel, ChannelType type){
        switch(type){
            case DEAD:
                break;
            case ROLE:
                break;
            case ADMIN:
                adminchannel = (TextChannel) channel;
                // send command tutorial in @admin
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(0x89CFF0);
                embed.setDescription(buildTutorial());
                adminchannel.sendMessageEmbeds(embed.build()).queue(a -> adminchannel.pinMessageById(a.getId()).queue());
                adminchannel.getPermissionContainer().getManager()
                        .putRolePermissionOverride(adminrole.getIdLong(), Permission.VIEW_CHANNEL.getRawValue(),Permission.MANAGE_CHANNEL.getRawValue())
                        .putRolePermissionOverride(gamerole.getIdLong(), Permission.UNKNOWN.getRawValue(),Permission.VIEW_CHANNEL.getRawValue())
                        .queue();
                
                break;
            case ANNOUNCEMENTS:
                this.maintextchannel = (TextChannel) channel;
                maintextchannel.sendMessage(TranslatableText.get("buttons.leave-game-message", language).replaceAll("%button%",TranslatableText.get("buttons.leave-game", language))).setActionRow(Button.danger("leave:"+id,TranslatableText.get("buttons.leave-game", language))).queue();
                break;
        }
        Main.linkChannel(channel.getIdLong(), id);
    }
    
    public Set<UserId> getPlayerList(){
        return Set.copyOf(playerlist.keySet());
    }
    
    public void finish(){
    
    }
    
    public void clean(){
        Main.logAdmin("Cleaning game ["+id+"]");
        clearInvites();
        Main.unlinkChannel(adminchannel.getIdLong());
        Main.unlinkChannel(maintextchannel.getIdLong());
        Main.unlinkChannel(mainvoicechannel.getIdLong());
        DiscordManager.cleanCategory(category);
        gamerole.delete().queue();
        adminrole.delete().queue();
        Main.removeGame(id);
    }

    public void addPlayer(UserId playerid, String privatekey){
        if(jointype == JoinType.PRIVATE && !Objects.equals(privatekey,joinkey)){
            Main.logAdmin(playerid+" could not join game "+id+" reason : (private game) wrong key -> "+privatekey+"!="+joinkey);
            return;
        }
        if(playerid != null && !locked){
            // remove the player from its ancient game if it is unlocked, otherwise prevent it to join this game
            for(GameManager mng : Main.getGamesList().values()){
                if(mng.getPlayerList().contains(playerid)){
                    if(mng.isLocked()){
                        Main.logAdmin("player could not have been moved from game "+mng.getId()+" to "+id+" because the source game is locked");
                        return;
                    } else {
                        mng.removePlayer(playerid);
                    }
                }
            }
            if(!playerlist.containsKey(playerid)){
                playerlist.put(playerid, new WerewolfPlayer(playerid, this));
            }
            String discord = DatabaseManager.getDiscordId(playerid);
            if(discord != null && !discordlink.containsKey(discord)){
                discordlink.put(discord, playerid);
                try{
                    Main.getJDA().retrieveUserById(discord).queue(user ->{
                        guild.addRoleToMember(user, gamerole).queue();
                        maintextchannel.sendMessage(TranslatableText.get("events.player-join-message",language).replaceAll("%user%",user.getAsMention())).queue();
                    });
                    //user successfully added to the game
                    guild.retrieveMember(UserSnowflake.fromId(discord)).queue(m ->{
                        if(m != null && maintextchannel != null){
                            try{
                                if(m.getVoiceState() != null && m.getVoiceState().inAudioChannel()){
                                    guild.moveVoiceMember(m, mainvoicechannel).queue();
                                }
                            } catch (InsufficientPermissionException noperm){
                                Main.logAdmin("Bot does not have enough permissions to move user "+discord+" in guild "+guild.getId());
                            } catch (IllegalStateException ignore){
                                //Main.logAdmin("Bot does not have enough permissions to move user "+discord+" in guild "+guild.getId());
                            }
                        }
                    });
                    
                }catch (IllegalArgumentException e1){
                    Main.logAdmin("User "+discord+" unknown, could not give the role");
                }catch (InsufficientPermissionException e2){
                    Main.logAdmin("Not enough permission to give user "+discord+" the game role");
                }
            }
            UUID mc = DatabaseManager.getMinecraftUUID(playerid);
            if(mc != null && !minecraftlink.containsKey(mc)){
                minecraftlink.put(mc, playerid);
            }
            
        }
    }
    
    public void addplayer(UserId playerid){
        addPlayer(playerid, null);
    }
    
    public void removePlayer(UserId player){
            if(player != null && playerlist.containsKey(player)){
                // remove discord link
                if(discordlink.containsValue(player)){
                    for(Map.Entry<String, UserId> entry : discordlink.entrySet()){
                        if(entry.getValue().equals(player)){
                            discordlink.remove(entry.getKey());
                        }
                    }
                }
                // remove minecraft link
                if(minecraftlink.containsValue(player)){
                    for(Map.Entry<UUID, UserId> entry : minecraftlink.entrySet()){
                        if(entry.getValue().equals(player)){
                            minecraftlink.remove(entry.getKey());
                        }
                    }
                }
                UserSnowflake member = UserSnowflake.fromId(DatabaseManager.getDiscordId(player));
                guild.retrieveMember(member).queue(mb ->{
                    // remove its roles
                    for(Role role : mb.getRoles()){
                        if(role.getName().contains(id)){
                            try{
                                guild.removeRoleFromMember(member, role).queue();
                            } catch (InsufficientPermissionException e){
                                Main.logAdmin("Not enough permissions to do", "remove role from member");
                            }
                        }
                    }
                    // kick it out of game voice channel
                    if(mb.getVoiceState() != null && mb.getVoiceState().inAudioChannel()){
                        for(VoiceChannel chan : category.getVoiceChannels()){
                            if(chan.getMembers().contains(mb)){
                                try{
                                    guild.kickVoiceMember(mb).queue();
                                } catch (InsufficientPermissionException e){
                                    Main.logAdmin("Not enough permissions to do", "kick member from voice");
                                }
                            }
                        }
                    }
                    // remove channel perms, seems heavy doing nested for loops but in reality the second loop contains
                    // only one or 2 elements, it is just to avoid missing one permission
                    for(GuildChannel channel : category.getChannels()){
                        for(PermissionOverride perm : channel.getPermissionContainer().getMemberPermissionOverrides()){
                            if(perm.getMember() != null && perm.getMember().getId().equals(mb.getId())){
                                channel.getPermissionContainer().getManager().removePermissionOverride(perm.getIdLong()).queue();
                            }
                        }
                    }
                    maintextchannel.sendMessage(TranslatableText.get("events.player-leave-message", language).replaceAll("%user%",member.getAsMention())).queue();
                });
                playerlist.remove(player);
            }
    }

    public String getId(){
        return id;
    }
    
    public String getJoinKey(){
        return joinkey;
    }

    public GameType getGameType() {
        return gametype;
    }
    
    public TextChannel getAdminChannel(){
        return adminchannel;
    }
    public TextChannel getMainTextChannel(){
        return maintextchannel;
    }
    public VoiceChannel getMainVoiceChannel(){
        return mainvoicechannel;
    }
    public void setGameType(GameType gametype) {
        this.gametype = gametype;
    }

    public static GameManager fromId(String id){
        return Main.getGamesList().getOrDefault(id, null);
    }
    
    // TODO : create a method to send these invites to anybody
    public void sendInvite(){
        TextChannel chan = DatabaseManager.getGuildInvitesChannel(guild);
        if(chan != null){
            sendInvite(chan);
        } else {
            throw new NullPointerException("no channel found for guild "+guild+":"+guild.getId());
        }
    }
    
    protected void sendInvite(TextChannel channel){
        if(!locked){
            String guildlang = DatabaseManager.getGuildLanguage(guild);
            String buttontitle = jointype == JoinType.PUBLIC ? TranslatableText.get("buttons.join-game", guildlang) : TranslatableText.get("buttons.join-private-game", guildlang);
            Button joinbutton = Button.primary("join:"+id, buttontitle);
            if(jointype == JoinType.PRIVATE){
                joinbutton = Button.secondary("joinprivate:"+id, buttontitle);
            }
            EmbedBuilder builder = new EmbedBuilder();
            builder
                    // TODO : support for multiple predefined time display
                    .setDescription(
                            buildInvite()
                                    .replaceAll("%id%",getId())
                                    .replaceAll("%type%",jointype.toString().toLowerCase(Locale.ROOT))
                                    .replaceAll("%button%",buttontitle))
                    .setColor(0xb491c8)
                    .setFooter("ID: "+id)
                    .setTimestamp(LocalDateTime.now())
            ;
            MessageAction msg =channel.sendMessageEmbeds(builder.build()).setActionRow(joinbutton);
            msg.queue(m -> invites.add(m));
        }
    }
    
    private void logInvite(Message msg){
        invites.add(msg);
        Main.logAdmin("MSG ID : "+msg.getId());
        Main.logAdmin("MSG CONTENT : "+msg.getContentDisplay());
        Main.logAdmin(invites);
    }
    
    private void clearInvites(){
        for(Message msg : invites){
            setInviteExpired(msg, language);
        }
    }
    
    public static void setInviteExpired(Message invite, String language) {
        Main.logAdmin("marking invite " + invite.getId() + " as expired");
        setInviteState(invite, TranslatableText.get("invites.expired-title", DatabaseManager.getGuildLanguage(Objects.requireNonNull(invite.getGuild()))), TranslatableText.get("buttons.expired-game", language), ButtonStyle.SECONDARY, 0x40454c, false);
    }
    
    public static void setInviteLocked(Message invite, String language) {
        Main.logAdmin("marking invite " + invite.getId() + " as locked");
        setInviteState(invite, TranslatableText.get("invites.locked-title", DatabaseManager.getGuildLanguage(Objects.requireNonNull(invite.getGuild()))), TranslatableText.get("buttons.locked-game", language), ButtonStyle.PRIMARY, 0x553369, false);
    }
    
    public static void setInviteOpen(Message invite, String language) {
        Main.logAdmin("marking invite " + invite.getId() + " as open");
        setInviteState(invite, "", TranslatableText.get("buttons.join-game", language), ButtonStyle.PRIMARY, 0xb491c8, true);
    }
    
    public void lock(){
        lock(true);
    }
    
    public void unlock(){
        lock(false);
        
    }
    
    public void lock(boolean lock){
        locked = lock;
        if(lock){
            if(adminchannel != null){
                adminchannel.sendMessage(TranslatableText.get("commands.admin.lock.success",language)).queue();
            }
            for(Message msg : invites){
                setInviteLocked(msg, language);
            }
        } else {
            if(adminchannel != null){
                adminchannel.sendMessage(TranslatableText.get("commands.admin.unlock.success",language)).queue();
            }
            for(Message msg : invites){
                setInviteOpen(msg, language);
            }
        }
    }
    
    public boolean isLocked(){
        return locked;
    }
    
    
    private static void setInviteState(Message invite, String title, String buttonlabel, ButtonStyle style, int color, boolean open){
        List<ActionRow> rows = invite.getActionRows();
        List<Button> modified = new ArrayList<>();
        if(open){
            for(ActionRow act : rows){
                act.getButtons().forEach(bt -> modified.add(bt.withLabel(buttonlabel).withStyle(style).asEnabled()));
            }
        } else {
            for(ActionRow act : rows){
                act.getButtons().forEach(bt -> modified.add(bt.withLabel(buttonlabel).withStyle(style).asDisabled()));
            }
        }
        List<MessageEmbed> newembeds = new ArrayList<>();
        invite.getEmbeds().forEach(em -> newembeds.add(new EmbedBuilder(em).setColor(color).setAuthor(title).build()));
    
        invite.editMessageEmbeds(invite.getEmbeds()).setActionRow(modified).queue();
        invite.editMessageEmbeds(invite.getEmbeds()).setEmbeds(newembeds).queue();
    }
    
    
/*
    |------------------------------------------------|
    |=================[EVENT QUEUE]==================|
    |------------------------------------------------|
*/
    
    
    public void startGame(){
        Main.logAdmin("Let's gooooooooooooooooooo");
    }
    
    private void buildQueue(){
    
    }
    
    protected void nextPhase(){
    
    }
}
