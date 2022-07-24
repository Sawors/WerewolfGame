package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserDataType;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.discord.ChannelType;
import com.github.sawors.werewolfgame.discord.DiscordManager;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.events.PhaseType;
import com.github.sawors.werewolfgame.game.events.day.MayorVoteEvent;
import com.github.sawors.werewolfgame.game.events.day.NightfallEvent;
import com.github.sawors.werewolfgame.game.events.night.SunriseEvent;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.base.Villager;
import com.github.sawors.werewolfgame.game.roles.base.Wolf;
import com.github.sawors.werewolfgame.localization.LoadedLocale;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameManager {

    private GameType gametype;
    private GamePhase gamephase;
    private HashMap<String, UserId> discordlink = new HashMap<>();
    private HashMap<UUID, UserId> minecraftlink = new HashMap<>();
    private HashMap<GuildChannel, PlayerRole> rolechannels = new HashMap<>();
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
    private Role playerrole;
    private Role adminrole;
    private User owner;
    private int paramstringlength = 0;
    private LoadedLocale language;
    private boolean locked = false;

    // GAME DATA
    private Set<UserId> playerset = new HashSet<>();
    private Map<PlayerRole, Integer> rolepool = new HashMap<>();
    private Map<UserId, WerewolfPlayer> playerlink = new HashMap<>();
    List<GameEvent> addedevents = new ArrayList<>();
    private Queue<GameEvent> eventqueue = new LinkedList<>();
    private int round = 0;
    private GameEvent currentevent;

    // GAME OPTIONS
    private boolean instantvote = true;
    private boolean autowolf = true;
    private boolean uselogchannel = false;
    private double autowolfpercentage = 0.25;
    private int wolfamount = 1;

    // LOGGING OPTIONS
    private TextChannel logchannel = null;
    private boolean logtofile = false;
    private boolean logtoconsole = true;
    private StringBuilder logholder = new StringBuilder();

    
    

    public GameManager(Guild guild, GameType type, JoinType accessibility){
        this.id = Main.generateRandomGameId();
        this.gametype = type;
        this.guild = guild;
        this.jointype = accessibility;
        this.joinkey = RandomStringUtils.randomNumeric(5);
        this.language = DatabaseManager.getGuildLanguage(guild);
        
        createRoles(
                a0 -> guild.createCategory("[\uD83D\uDC3A WEREWOLF : "+id+"]")
                        .addRolePermissionOverride(playerrole.getIdLong(), List.of(Permission.VIEW_CHANNEL), List.of(Permission.MANAGE_CHANNEL))
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
            rolepool.put(role, prio);
        }
        
        Main.registerNewGame(this);
    }
    
    public void addRoleEvent(GameEvent... events){
    
    }
    
    private String buildTutorial(){
        TranslatableText texts = new TranslatableText(Main.getTranslator(), language);
        return  texts.get("commands.admin.title")
                +"\n\n `clean` : "+texts.get("commands.admin.clean.description")
                +"\n\n `start` : "+texts.get("commands.admin.start.description")
                +"\n\n `lang` : "+texts.get("commands.admin.lang.description")
                +"\n\n `lock` : "+texts.get("commands.admin.lock.description")
                +"\n\n `unlock` : "+texts.get("commands.admin.unlock.description")
                +"\n\n `admin` : "+texts.get("commands.admin.admin.description")
        ;
    }
    
    private String buildInvite(){
        return new TranslatableText(Main.getTranslator(), language).get("invites.invite-body-customizable");
    }

    public LoadedLocale getLanguage(){
        return language;
    }

    private void createRoles(Consumer<?> chainedaction){
        guild.createRole().setName("WW:"+id+":ADMIN").setMentionable(false).queue(a -> {
                    this.adminrole = a;
                    guild.createRole().setName("WW:" + id).setMentionable(false).queue(b -> {
                        this.playerrole = b;
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

    public Guild getGuild(){
        return guild;
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
    
    public void setLanguage(LoadedLocale locale){
        this.language = locale;
        TranslatableText texts = new TranslatableText(Main.getTranslator(), language);
        adminchannel.getManager().setName(texts.get("channels.text.admin")).queue();
        maintextchannel.getManager().setName(texts.get("channels.text.main")).queue();
        mainvoicechannel.getManager().setName(texts.get("channels.voice.main")).queue();
        adminchannel.sendMessage(texts.get("commands.admin.lang.success")).queue(m -> adminchannel.sendMessageEmbeds(new EmbedBuilder().setDescription(buildTutorial()).setColor(0x89CFF0).build()).queue());
    }
    
    private void createChannels(Category category){
        if(this.category == null){
            this.category = category;
        }
        if(this.category != null){
            TranslatableText texts = new TranslatableText(Main.getTranslator(), language);
            // create admin text channel
            setupTextChannel(texts.get("channels.text.admin"), ChannelType.ADMIN);
            // create main text channel
            setupTextChannel(texts.get("channels.text.main"), ChannelType.ANNOUNCEMENTS);
            
            // create main voice channel
            category.createVoiceChannel(texts.get("channels.voice.main")).queue(channel -> this.mainvoicechannel = channel);
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
                adminchannel.sendMessageEmbeds(embed.build()).queue(a -> adminchannel.pinMessageById(a.getId()).queue(m -> adminchannel.getHistory().getRetrievedHistory().forEach(msg -> {
                    if(msg.getAuthor().isSystem()){
                        msg.delete().queue();
                    }
                })));
                //adminchannel.sendMessage("Start Game").setActionRow(Button.success("start"+id, "Start Game")).queue();
                adminchannel.getPermissionContainer().getManager()
                        .putRolePermissionOverride(adminrole.getIdLong(), Permission.VIEW_CHANNEL.getRawValue(),Permission.MANAGE_CHANNEL.getRawValue())
                        .putRolePermissionOverride(playerrole.getIdLong(), Permission.UNKNOWN.getRawValue(),Permission.VIEW_CHANNEL.getRawValue())
                        .queue();
                
                break;
            case ANNOUNCEMENTS:
                this.maintextchannel = (TextChannel) channel;
                TranslatableText textpool = new TranslatableText(Main.getTranslator(), language);
                maintextchannel.sendMessage(textpool.get("buttons.leave-game-message").replaceAll("%button%",textpool.get("buttons.leave-game"))).setActionRow(Button.danger("leave:"+id,textpool.get("buttons.leave-game"))).queue(msg -> leavemessage = msg);
                break;
        }
        Main.linkChannel(channel.getIdLong(), id);
    }
    
    public Set<UserId> getPlayerSet(){
        return Set.copyOf(playerset);
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
        playerrole.delete().queue();
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
                if(mng.getPlayerSet().contains(playerid)){
                    if(mng.isLocked()){
                        Main.logAdmin("player could not have been moved from game "+mng.getId()+" to "+id+" because the source game is locked");
                        return;
                    } else {
                        mng.removePlayer(playerid);
                    }
                }
            }
            if(!playerset.contains(playerid)){
                playerset.add(playerid);
            }
            String discord = DatabaseManager.getDiscordId(playerid);
            if(discord != null && !discordlink.containsKey(discord)){
                discordlink.put(discord, playerid);
                try{
                    Main.getJDA().retrieveUserById(discord).queue(user ->{
                        guild.addRoleToMember(user, playerrole).queue();
                        maintextchannel.sendMessage(new TranslatableText(Main.getTranslator(), language).get("events.player-join-message").replaceAll("%user%",user.getAsMention())).queue();
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
            if(player != null && playerset.contains(player)){
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
                    maintextchannel.sendMessage(new TranslatableText(Main.getTranslator(), language).get("events.player-leave-message").replaceAll("%user%",member.getAsMention())).queue();
                });
                playerset.remove(player);
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
            LoadedLocale guildlang = DatabaseManager.getGuildLanguage(guild);
            TranslatableText texts = new TranslatableText(Main.getTranslator(), guildlang);
            String buttontitle = jointype == JoinType.PUBLIC ? texts.get("buttons.join-game") : texts.get("buttons.join-private-game");
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
            setInviteExpired(msg);
        }
    }
    
    public static void setInviteExpired(Message invite) {
        TranslatableText texts = new TranslatableText(Main.getTranslator(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(invite.getGuild())));
        Main.logAdmin("marking invite " + invite.getId() + " as expired");
        setInviteState(invite, texts.get("invites.expired-title"), texts.get("buttons.expired-game"), ButtonStyle.SECONDARY, 0x40454c, false);
    }
    
    public static void setInviteLocked(Message invite) {
        TranslatableText texts = new TranslatableText(Main.getTranslator(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(invite.getGuild())));
        Main.logAdmin("marking invite " + invite.getId() + " as locked");
        setInviteState(invite, texts.get("invites.locked-title"), texts.get("buttons.locked-game"), ButtonStyle.PRIMARY, 0x553369, false);
    }
    
    public static void setInviteOpen(Message invite) {
        Main.logAdmin("marking invite " + invite.getId() + " as open");
        setInviteState(invite, "", new TranslatableText(Main.getTranslator(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(invite.getGuild()))).get("buttons.join-game"), ButtonStyle.PRIMARY, 0xb491c8, true);
    }
    
    public void lock(){
        lock(true);
    }
    
    public void unlock(){
        lock(false);
        
    }
    
    public void lock(boolean lock){
        locked = lock;
        TranslatableText texts = new TranslatableText(Main.getTranslator(), language);
        if(lock){
            if(adminchannel != null){
                adminchannel.sendMessage(texts.get("commands.admin.lock.success")).queue();
            }
            for(Message msg : invites){
                setInviteLocked(msg);
            }
        } else {
            if(adminchannel != null){
                adminchannel.sendMessage(texts.get("commands.admin.unlock.success")).queue();
            }
            for(Message msg : invites){
                setInviteOpen(msg);
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

    public static void forceClean(Guild guild, String id){
        for(Role role : guild.getRoles()){
            if(role.getName().contains(id)){
                role.delete().queue();
            }
        }
        for(Category cat : guild.getCategories()){
            if(cat.getName().contains(id)){
                try{
                    List<GuildChannel> channels = cat.getChannels();
                    for(int i = 0; i<channels.size(); i++){
                        if(i == channels.size()-1){
                            channels.get(i).delete().queue(c -> cat.delete().queue());
                        } else {
                            channels.get(i).delete().queue();
                        }
                    }
                } catch (InsufficientPermissionException e){
                    Main.logAdmin("Permission Error in guild : "+guild.getId(),e.getMessage());
                }
            }
        }
    }

    private void logEvent(Object log){
        if(logtofile){
            logEvent(log, LogDestination.FILE);
        }
        if(logchannel != null && logchannel.getParentCategory() != null && logchannel.getParentCategory().equals(category)){
            logEvent(log, LogDestination.CHANNEL);
        }
        if(logtoconsole){
            logEvent(log, LogDestination.CONSOLE);
        }
    }

    private void logEvent(Object log, LogDestination destination){
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        // log.toString() is a bit overkill, normally log is automatically converted to String type when summed with other Strings
        switch (destination){
            case CONSOLE:
                System.out.println("Game "+id+" : ["+timestamp+"] "+log.toString());
                break;
            case CHANNEL:
                logchannel.sendMessage("["+timestamp+"] "+log.toString()).queue();
                break;
            case DATABASE:
                break;
            case FILE:
                break;
        }
    }
    
    
/*
    |------------------------------------------------|
    |=================[EVENT QUEUE]==================|
    |------------------------------------------------|
*/
    
    
    public void startGame(){
        Main.logAdmin("Let's gooooooooooooooooooo");
        for(int i = 0; i<8; i++){
            UserId userid = new UserId();
            logEvent("adding fake player "+userid, LogDestination.CONSOLE);
            playerset.add(userid);
        }
        assignRoles();
        buildFirstDayQueue();
        nextEvent();
    }

    private Set<LinkedUser> defaultVotePool(){
        Set<LinkedUser> votepool = new HashSet<>();
        List<String> fakenameslist = new ArrayList<>();
        fakenameslist.add("Pasawors");
        fakenameslist.add("esprit-absent");
        fakenameslist.add("MOLE1383");
        fakenameslist.add("Dimitri Sussmaibouls");
        fakenameslist.add("Arthurlute");
        fakenameslist.add("Marvanne");
        fakenameslist.add("7o");
        fakenameslist.add("BelloBito");
        fakenameslist.add("GuyDon");
        fakenameslist.add("Richard GoldenTrash");
        fakenameslist.add("Gabriel >");
        fakenameslist.add("UwU E-Girl#547");
        fakenameslist.add("Xx_D4rkSl4y3r_xX");
        fakenameslist.add("AlphacatCoolCat");
        fakenameslist.add("l'Idiot");
        fakenameslist.add("JimmyBois");
        fakenameslist.add("aïecaillou");
        Collections.shuffle(fakenameslist);
        Queue<String> fakenames = new LinkedList<>(fakenameslist);
        for(int i = 1; fakenames.size()*i <= playerset.size(); i++){
            Collections.shuffle(fakenameslist);
            fakenames.addAll(fakenameslist);
        }
        Main.logAdmin("Name Pool", fakenames);
        Main.logAdmin(playerset);
        Main.logAdmin(playerset.contains(UserId.fromDiscordId("315237447065927691")));
        playerset.forEach(uid -> votepool.add(new LinkedUser(uid, DatabaseManager.getUserData(uid, UserDataType.NAME) != null ? DatabaseManager.getUserData(uid, UserDataType.NAME) : fakenames.poll(),UUID.randomUUID(),"",null,null)));
        return votepool;
    }
    
    public void buildQueue(PhaseType type){
        if(type == PhaseType.DAY){
            buildDayQueue();
        } else {
            buildNightQueue();
        }
    }

    public GameEvent getCurrentEvent(){
        return currentevent;
    }
    
    public void nextEvent(){
        if(eventqueue.isEmpty()){
            // TODO ???
        } else {
            GameEvent next = eventqueue.poll();
            currentevent = next;
            next.start();
        }
    }

    private void buildNightQueue(){



        eventqueue.add(new SunriseEvent(this));
    }

    private void buildDayQueue(){
        round++;



        eventqueue.add(new NightfallEvent(this));
    }

    private void buildFirstDayQueue(){
        //eventqueue.add(new Intro(this));
        eventqueue.add(new MayorVoteEvent(this, defaultVotePool(),Set.of(UserId.fromDiscordId("315237447065927691"), UserId.fromString("oxtyaevi")), maintextchannel));
        eventqueue.add(new NightfallEvent(this));
    }

    private void assignRoles(){

        logEvent("Assigning Roles", LogDestination.CONSOLE);
        // using playerset
        // using rolepool

        List<UserId> shuffled = new ArrayList<>(List.copyOf(playerset));
        Collections.shuffle(shuffled);
        Queue<UserId> pendingusers = new LinkedList<>(shuffled);

        int playercount = pendingusers.size();

        logEvent("Player count : "+playercount, LogDestination.CONSOLE);
        logEvent("Complete role pool : "+rolepool.keySet(), LogDestination.CONSOLE);

        List<PlayerRole> villageuniqueroles = new ArrayList<>(rolepool.keySet());
        villageuniqueroles.removeIf(role -> role instanceof Villager);
        villageuniqueroles.removeIf(role -> role instanceof Wolf);
        Collections.shuffle(villageuniqueroles);
        Queue<PlayerRole> pendingroles = new LinkedList<>(villageuniqueroles);
        logEvent("Pending village roles : "+pendingroles, LogDestination.CONSOLE);

        List<PlayerRole> wolfroles = new ArrayList<>(rolepool.keySet());
        wolfroles.removeIf(role -> !(role instanceof Wolf));
        Collections.shuffle(wolfroles);
        Queue<PlayerRole> pendingwolves = new LinkedList<>(wolfroles);
        logEvent("Pending wolf roles : "+pendingwolves, LogDestination.CONSOLE);

        if(autowolf){
            autowolfpercentage = Math.max(Math.min(autowolfpercentage, 0.75), 0.25);
            // casting to int here does floor the amount as it is a positive number (if roof just do +1)
            wolfamount = (int) (playercount*autowolfpercentage);
        }

        if(playercount < 4){
            throw new IndexOutOfBoundsException("too few players to start the game (must be > 4, got "+playercount);
        }

        // assigning roles
        if(autowolf){
            logEvent("Using autowolf with wolves percentage set to "+(int)(autowolfpercentage*100)+"%", LogDestination.CONSOLE);
        }
        logEvent("Wolves amount : "+wolfamount, LogDestination.CONSOLE);
        for(int i = 0; i < wolfamount; i++){
            UserId user = pendingusers.poll();
            if(user == null){
                throw new IndexOutOfBoundsException("too few players to start the game, could not give wolf roles, all players are wolves (???? Serious issue, please report it to https://github.com/Sawors/WerewolfGame/issues/new");
            }
            PlayerRole role = pendingwolves.poll();
            if(role == null){
                role = new Wolf();
            }
            logEvent("Giving role "+role+" to player "+user+" (Wolf Phase)", LogDestination.CONSOLE);
            playerlink.put(user, new WerewolfPlayer(user, this, role));
        }
        for(UserId user : pendingusers){
            PlayerRole role = pendingroles.poll();
            if(role == null){
                role = new Villager();
            }
            logEvent("Giving role "+role+" to player "+user+" (Village Phase)", LogDestination.CONSOLE);
            playerlink.put(user, new WerewolfPlayer(user, this, role));
        }
        logEvent(playerlink, LogDestination.CONSOLE);
    }

    public void setupTimedAction(int seconds, Runnable action){
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(action,seconds,TimeUnit.SECONDS);
    }
}
