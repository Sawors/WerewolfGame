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
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Consumer;

public class GameManager {

    private GameType gametype;
    private GamePhase gamephase;
    private HashMap<UserId, WerewolfPlayer> playerlist = new HashMap<>();
    private HashMap<String, UserId> discordlink = new HashMap<>();
    private HashMap<UUID, UserId> minecraftlink = new HashMap<>();
    private HashMap<GuildChannel, PlayerRole> rolechannels = new HashMap<>();
    private ArrayList<PlayerRole> rolelist = new ArrayList<>();
    //private GuildChannel admin;
    private final String id;
    private final Guild guild;
    //private Server mcserver;
    private ArrayList<Message> invites = new ArrayList<>();
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
    private String language = Main.getLocale();

    
    private final String tutorial =
            TranslatableText.get("commands.admin.title",language)
            +"\n\n - `clean` : "+TranslatableText.get("commands.admin.clean-description",language)
            +"\n\n - `start` : "+TranslatableText.get("commands.admin.start-description",language)
            ;
    
    private final String invitetemplate = TranslatableText.get("invites.invite-body-customizable",language);
    

    public GameManager(Guild guild, GameType type, JoinType accessibility){
        this.id = Main.generateRandomGameId();
        this.gametype = type;
        this.guild = guild;
        this.jointype = accessibility;
        this.joinkey = RandomStringUtils.randomNumeric(5);
        
        createRoles(
                a0 -> guild.createCategory("WEREWOLF : "+id)
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
                embed.setDescription(tutorial);
                adminchannel.sendMessageEmbeds(embed.build()).queue(a -> adminchannel.pinMessageById(a.getId()).queue());
                adminchannel.getPermissionContainer().getManager()
                        .putRolePermissionOverride(adminrole.getIdLong(), Permission.VIEW_CHANNEL.getRawValue(),Permission.MANAGE_CHANNEL.getRawValue())
                        .putRolePermissionOverride(gamerole.getIdLong(), Permission.UNKNOWN.getRawValue(),Permission.VIEW_CHANNEL.getRawValue())
                        .queue();
                
                break;
            case ANNOUNCEMENTS:
                this.maintextchannel = (TextChannel) channel;
                break;
        }
        Main.linkChannel(channel.getIdLong(), id);
    }
    
    public Set<UserId> getPlayerList(){
        return playerlist.keySet();
    }
    
    public void finish(){
    
    }
    
    public void clean(){
        Main.logAdmin("Cleaning game "+id);
        clearInvites();
        DiscordManager.cleanCategory(category);
        gamerole.delete().queue();
        adminrole.delete().queue();
    }
    
    private void deleteCategory(){
       if(category != null){
           Main.logAdmin("Deleted category "+category.getName());
           this.category.delete().queue();
       }
    }

    public void addPlayer(UserId playerid, String privatekey){
        if(jointype == JoinType.PRIVATE && !Objects.equals(privatekey,joinkey)){
            Main.logAdmin(playerid+" could not join game "+id+" reason : (private game) wrong key -> "+privatekey+"!="+joinkey);
            return;
        }
        if(playerid != null){
            if(!playerlist.containsKey(playerid)){
                playerlist.put(playerid, new WerewolfPlayer(playerid, this));
            }
            String discord = DatabaseManager.getDiscordId(playerid);
            if(discord != null && !discordlink.containsKey(discord)){
                discordlink.put(discord, playerid);
                try{
                    Main.getJDA().retrieveUserById(discord).queue(user ->{
                        guild.addRoleToMember(user, gamerole).queue();
                        Main.logAdmin(discord);
                        maintextchannel.sendMessage(TranslatableText.get("events.player-join-message",language).replaceAll("%user%",user.getAsMention())).queue();
                    });
                    //user successfully added to the game
                    guild.retrieveMember(UserSnowflake.fromId(discord)).queue(m ->{
                        if(m != null && maintextchannel != null){
                            try{
                                guild.moveVoiceMember(m, mainvoicechannel).queue();
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

    public String getId(){
        return id;
    }
    
    public String getJoinKey(){
        return joinkey;
    }

    public GameType getGameType() {
        return gametype;
    }
    
    public GuildChannel getAdminChannel(){
        return adminchannel;
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
        String buttontitle = jointype == JoinType.PUBLIC ? TranslatableText.get("buttons.join-game", language) : TranslatableText.get("buttons.join-private-game", language);
        Button joinbutton = Button.primary("join:"+id, buttontitle);
        if(jointype == JoinType.PRIVATE){
            joinbutton = Button.secondary("joinprivate:"+id, buttontitle);
        }
        EmbedBuilder builder = new EmbedBuilder();
            builder
                    // TODO : support for multiple predefined time display
                .setDescription(
                        invitetemplate
                        .replaceAll("%id%",getId())
                        .replaceAll("%type%",jointype.toString().toLowerCase(Locale.ROOT))
                        .replaceAll("%join%",TranslatableText.get(buttontitle, language)))
                .setColor(0x8510d8);
        MessageAction msg =channel.sendMessageEmbeds(builder.build()).setActionRow(joinbutton);
        msg.queue(this::logInvite);
    }
    
    private void logInvite(Message msg){
        invites.add(msg);
        Main.logAdmin("MSG ID : "+msg.getId());
        Main.logAdmin("MSG CONTENT : "+msg.getContentDisplay());
        Main.logAdmin(invites);
    }
    
    private void clearInvites(){
        for(Message msg : invites){
            msg.delete().queue();
        }
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
