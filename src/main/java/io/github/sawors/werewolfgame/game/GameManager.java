package io.github.sawors.werewolfgame.game;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.DiscordBot;
import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.discord.ChannelType;
import io.github.sawors.werewolfgame.discord.DiscordManager;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.events.*;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.PrimaryRole;
import io.github.sawors.werewolfgame.game.roles.TextRole;
import io.github.sawors.werewolfgame.game.roles.base.Villager;
import io.github.sawors.werewolfgame.game.roles.base.Wolf;
import io.github.sawors.werewolfgame.localization.LoadedLocale;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GameManager {

    private GameType gametype;
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
    private TextChannel waitingchannel;

    // GAME OPTIONS
    private GameOptions options = new GameOptions();

    // LOGGING OPTIONS
    private TextChannel logchannel = null;
    private boolean logtofile = false;
    private boolean logtoconsole = true;
    private StringBuilder logholder = new StringBuilder();
    
    // GAME DATA
    private Set<UserId> readyplayers = new HashSet<>();
    private Set<UserId> playerset = new HashSet<>();
    private Map<PlayerRole, Integer> rolepool = new HashMap<>();
    private Map<UserId, WerewolfPlayer> playerlink = new HashMap<>();
    private List<GameEvent> addedevents = new ArrayList<>();
    private Queue<GameEvent> eventqueue = new LinkedList<>();
    private int round = 0;
    private Set<PlayerRole> usedroles = new HashSet<>();
    private GameEvent currentevent;
    private GamePhase gamephase;
    private HashMap<UserId, User> discordlink = new HashMap<>();
    private HashMap<UUID, UserId> minecraftlink = new HashMap<>();
    private HashMap<TextChannel, TextRole> rolechannels = new HashMap<>();
    private List<BackgroundEvent> backgroundevents = new ArrayList<>();
    private TextChannel wolfchannel;
    private Set<WerewolfExtension> loadedextensions = new HashSet<>();
    private Role deadrole;
    private final float gamehue = (280+(int)(Math.random()*20))/360f;
    private UserId mayor;
    private Set<UserId> pendingdeath = new HashSet<>();
    private Map<String, List<UserId>> teams = new HashMap<>();
    private TextChannel deadchannel;
    private final Narrator narrator;

    
    

    public GameManager(Guild guild, GameType type, JoinType accessibility){
        this.id = Main.generateRandomGameId();
        this.gametype = type;
        this.guild = guild;
        this.jointype = accessibility;
        this.joinkey = RandomStringUtils.randomNumeric(5);
        this.language = DatabaseManager.getGuildLanguage(guild);
        this.gamephase = GamePhase.BEFORE_GAME;
        this.narrator = new Narrator(this);
        
        createRoles(
                a0 -> guild.createCategory("[\uD83D\uDC3A WEREWOLF : "+id+"]")
                        .addRolePermissionOverride(playerrole.getIdLong(), List.of(Permission.VIEW_CHANNEL), List.of(Permission.MANAGE_CHANNEL))
                        .addRolePermissionOverride(adminrole.getIdLong(),List.of(Permission.VIEW_CHANNEL), List.of())
                        .addRolePermissionOverride(deadrole.getIdLong(),List.of(Permission.VIEW_CHANNEL), List.of(Permission.MESSAGE_SEND, Permission.MESSAGE_SEND_IN_THREADS,Permission.CREATE_PRIVATE_THREADS,Permission.CREATE_PUBLIC_THREADS,Permission.MESSAGE_ADD_REACTION))
                        .addRolePermissionOverride(guild.getPublicRole().getIdLong(),List.of(), List.of(Permission.VIEW_CHANNEL, Permission.CREATE_PUBLIC_THREADS, Permission.CREATE_PRIVATE_THREADS))
                        .queue(a1 -> {
                    category = a1;
                    this.createChannels(category);
                    }
                )
        );
        
        
        // Load extensions (all by default before the extension loader is completed)
        for(WerewolfExtension extension : Main.getLoadedExtensions()){
            for(PlayerRole role : extension.getRoles()){
                this.rolepool.put(role,role.priority());
            }
            this.backgroundevents.addAll(extension.getBackgroundEvents());
            for(BackgroundEvent event : backgroundevents){
                event.initialize(this);
            }
            this.loadedextensions.add(extension);
        }
    
        Main.logAdmin("Loaded events", addedevents);
    
        Main.registerNewGame(this);
    }

    public void setReady(UserId id, boolean ready){
        if(ready){
            readyplayers.add(id);
        } else {
            readyplayers.remove(id);
        }
    }

    public Map<UserId, WerewolfPlayer> getPlayerRoles(){
        return Map.copyOf(playerlink);
    }

    public void confirmDeath(UserId id){
        if(playerlink.containsKey(id)){
            playerlink.get(id).kill();

            for(BackgroundEvent event : backgroundevents){
                event.onDeathConfirmed(id);
            }
            try{
                guild.addRoleToMember(UserSnowflake.fromId(DatabaseManager.getDiscordId(id)), deadrole).queue();
                getGuild().mute(UserSnowflake.fromId(DatabaseManager.getDiscordId(id)), true).queue();
            } catch (IllegalArgumentException | IllegalStateException ignored){}
        }
        pendingdeath.remove(id);
    }

    public TextChannel getWaitingChannel(){
        return waitingchannel;
    }
    
    public void addRoleEvent(Collection<GameEvent> events){
        for(GameEvent event : events){
            if(event instanceof RoleEvent){
                addedevents.add(event);
            }
        }
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
                +"\n\n `use` : "+texts.get("commands.admin.use.description")
        ;
    }
    
    private String buildInvite(){
        return new TranslatableText(Main.getTranslator(), language).get("invites.invite-body-customizable");
    }

    public LoadedLocale getLanguage(){
        return language;
    }
    
    public GamePhase getGamePhase(){
        return  gamephase;
    }
    
    public UserId getMayor(){
        return mayor;
    }
    
    public void setMayor(UserId user){
        this.mayor = user;
    }

    private void createRoles(Consumer<?> chainedaction){
        guild.createRole().setName("WW:"+id+":ADMIN").setMentionable(false).setHoisted(false).setColor(Color.HSBtoRGB(gamehue,.8f,.6f)).queue(a -> {
                    this.adminrole = a;
                    guild.createRole().setName("WW:" + id).setMentionable(false).queue(b -> {
                        this.playerrole = b;
                        if(owner != null){
                            setAdmin(owner);
                        }
                        guild.createRole().setName("WW:"+id+":DEAD").setMentionable(false).setHoisted(false).setColor(0x333333).queue(c -> {
                            this.deadrole = c;
                            chainedaction.accept(null);
                        });
                    });
                }
        )
        ;
    }
    
    public TextChannel getWolfChannel(){
        return wolfchannel;
    }
    
    public float getGameHue(){
        return gamehue;
    }
    
    public Map<TextChannel, TextRole> getRoleChannels(){
        return Map.copyOf(rolechannels);
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
        DiscordBot.addPendingAction(adminchannel.getManager().setName(texts.get("channels.text.admin")));
        DiscordBot.addPendingAction(maintextchannel.getManager().setName(texts.get("channels.text.main")));
        DiscordBot.addPendingAction(mainvoicechannel.getManager().setName(texts.get("channels.voice.main")));
        DiscordBot.addPendingAction(waitingchannel.getManager().setName(texts.get("channels.text.waiting")));
        DiscordBot.triggerActionQueue();
        adminchannel.sendMessage(texts.get("commands.admin.lang.success")).queue(m -> DiscordBot.addPendingAction(adminchannel.sendMessageEmbeds(new EmbedBuilder().setDescription(buildTutorial()).setColor(0x89CFF0).build())));
        Main.logAdmin(rolechannels);
        for(Map.Entry<TextChannel, TextRole> entry : rolechannels.entrySet()){
            TextChannel channel = entry.getKey();
            Main.logAdmin(channel.getName());
            DiscordBot.addPendingAction(entry.getKey().getManager().setName(entry.getValue().getChannelName(language)));
            MessageEmbed helpmsg = entry.getValue().getHelpMessageEmbed(language);
            String welcomemsg = entry.getValue().getIntroMessage(language);
            if(helpmsg != null){
                DiscordBot.addPendingAction(channel.sendMessageEmbeds(helpmsg));
            }
            if(welcomemsg != null && welcomemsg.length() > 0){
                DiscordBot.addPendingAction(channel.sendMessage(welcomemsg));
            }
        }
        DiscordBot.triggerActionQueue();
    }
    
    private void createChannels(Category category){
        if(this.category == null){
            this.category = category;
        }
        if(this.category != null){
            TranslatableText texts = new TranslatableText(Main.getTranslator(), language);
            // create admin text channel
            setupTextChannel(texts.get("channels.text.admin"), ChannelType.ADMIN);
            // create waiting channel
            setupTextChannel(texts.get("channels.text.waiting"), ChannelType.WAITING);
            // create main text channel
            setupTextChannel(texts.get("channels.text.main"), ChannelType.ANNOUNCEMENTS);
            // create dead text channel
            setupTextChannel(texts.get("channels.text.dead"), ChannelType.DEAD);
            
            // create main voice channel
            category.createVoiceChannel(texts.get("channels.voice.main")).queue(channel -> this.mainvoicechannel = channel);
        }
    }
    
    public GameOptions getOptions(){
        return options;
    }
    
    private RestAction<?> setChannelVisible(GuildChannel channel, boolean visible){
        List<Permission> allow = new ArrayList<>();
        List<Permission> deny = new ArrayList<>();
        PermissionOverride base = channel.getPermissionContainer().getPermissionOverride(playerrole);
        if(base != null){
            allow.addAll(base.getAllowed());
            deny.addAll(base.getDenied());
        }

        // I could use array inversion to switch these 2 values, but I don't think it deserves much goal other than flexing
        if(visible){
            allow.add(Permission.VIEW_CHANNEL);
            deny.remove(Permission.VIEW_CHANNEL);
        } else {
            deny.add(Permission.VIEW_CHANNEL);
            allow.remove(Permission.VIEW_CHANNEL);
        }

        return channel.getPermissionContainer().getManager().putRolePermissionOverride(playerrole.getIdLong(), allow,deny);
    }

    private RestAction<?> setChannelLocked(TextChannel channel, boolean locked){
        List<Permission> allow = new ArrayList<>();
        List<Permission> deny = new ArrayList<>();
        PermissionOverride base = channel.getPermissionContainer().getPermissionOverride(playerrole);
        if(base != null){
            allow.addAll(base.getAllowed());
            deny.addAll(base.getDenied());
        }

        // I could use array inversion to switch these 2 values, but I don't think it deserves much goal other than flexing
        if(locked){
            deny.add(Permission.MESSAGE_SEND);
            deny.add(Permission.MESSAGE_ADD_REACTION);
            allow.remove(Permission.MESSAGE_SEND);
            allow.remove(Permission.MESSAGE_ADD_REACTION);
        } else {
            allow.add(Permission.MESSAGE_SEND);
            allow.add(Permission.MESSAGE_ADD_REACTION);
            deny.remove(Permission.MESSAGE_SEND);
            deny.remove(Permission.MESSAGE_ADD_REACTION);
        }

        return channel.getPermissionContainer().getManager().putRolePermissionOverride(playerrole.getIdLong(), allow,deny);
    }
    
    private void setupTextChannel(String name, ChannelType type){
        this.category.createTextChannel(name).queue(channel -> cacheChannel(channel, type));
    }
    
    private void cacheChannel(GuildChannel channel, ChannelType type){
        switch(type){
            case DEAD:
                deadchannel = (TextChannel) channel;
                deadchannel.getPermissionContainer().getManager()
                        .putRolePermissionOverride(adminrole.getIdLong(), List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND),List.of(Permission.MANAGE_CHANNEL))
                        .putRolePermissionOverride(deadrole.getIdLong(), List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND),List.of(Permission.MANAGE_CHANNEL))
                        .putRolePermissionOverride(playerrole.getIdLong(), List.of(Permission.UNKNOWN),List.of(Permission.VIEW_CHANNEL,Permission.MESSAGE_SEND))
                        .queue();
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
                        .putRolePermissionOverride(adminrole.getIdLong(), List.of(Permission.VIEW_CHANNEL),List.of(Permission.MANAGE_CHANNEL))
                        .putRolePermissionOverride(playerrole.getIdLong(), List.of(Permission.UNKNOWN),List.of(Permission.VIEW_CHANNEL))
                        .putRolePermissionOverride(deadrole.getIdLong(), List.of(Permission.UNKNOWN),List.of(Permission.VIEW_CHANNEL))
                        .queue();
                
                break;
            case ANNOUNCEMENTS:
                this.maintextchannel = (TextChannel) channel;
                setChannelVisible(channel, false).queue();
                break;
            case WAITING:
                this.waitingchannel = (TextChannel) channel;
                TranslatableText textpool = new TranslatableText(Main.getTranslator(), language);
                waitingchannel.sendMessageEmbeds(new EmbedBuilder().setDescription(
                        textpool.get("events.intro-block.description")
                                .replaceAll("%admin%",owner.getAsMention())
                        +"\n\n"+textpool.get("events.intro-block.ready-instructions")
                                .replaceAll("%emoji%",":white_check_mark:")).setFooter(
                        textpool.get("events.intro-block.leave-button-indication")
                                .replaceAll("%button%", textpool.get("buttons.leave-game")))
                        .build()).setActionRow(Button.danger("leave:"+id,textpool.get("buttons.leave-game"))).queue(msg -> {
                    leavemessage = msg;
                    msg.addReaction("U+2705").queue();
                });
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
        eventqueue.clear();
        currentevent = null;
        for(UserId u : discordlink.keySet()){
            try{
                getGuild().mute(UserSnowflake.fromId(DatabaseManager.getDiscordId(u)), false).queue();
            } catch (IllegalArgumentException | IllegalStateException ignored){}
        }
        // here to safely unlink we could loop through the channel link and remove every channel linked to this GameManager's id, however if we can keep all channels in memory
        // (like we are doing for the moment) I don't see the point in looping through potentially thousands of channels when we ALREADY know their key and therefore where they are
        Main.unlinkChannel(adminchannel.getIdLong());
        Main.unlinkChannel(maintextchannel.getIdLong());
        Main.unlinkChannel(mainvoicechannel.getIdLong());
        Main.unlinkChannel(waitingchannel.getIdLong());
        for(TextChannel channel : rolechannels.keySet()){
            Main.unlinkChannel(channel.getIdLong());
        }
        DiscordManager.cleanCategory(category);
        playerrole.delete().queue();
        adminrole.delete().queue();
        deadrole.delete().queue();
        Main.removeGame(id);
    }

    public @Nullable User getDiscordUser(UserId id){
        return discordlink.get(id);
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
            if(discord != null && !discordlink.containsKey(playerid)){
                Main.getJDA().retrieveUserById(DatabaseManager.getDiscordId(playerid)).queue(u -> discordlink.put(playerid, u));
                try{
                    Main.getJDA().retrieveUserById(discord).queue(user ->{
                        guild.addRoleToMember(user, playerrole).queue();
                        waitingchannel.sendMessage(new TranslatableText(Main.getTranslator(), language).get("events.player-join-message").replaceAll("%user%",user.getAsMention())).queue();
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
    
    public void addPlayer(UserId playerid){
        addPlayer(playerid, null);
    }
    
    public void removePlayer(UserId player){
            if(player != null && playerset.contains(player)){
                // remove discord link
                if(discordlink.containsKey(player)){
                    for(Map.Entry<UserId, User> entry : new HashSet<>(discordlink.entrySet())){
                        if(entry.getKey().equals(player)){
                            discordlink.remove(entry.getKey());
                        }
                    }
                }
                // remove minecraft link
                if(minecraftlink.containsValue(player)){
                    for(Map.Entry<UUID, UserId> entry : new HashSet<>(minecraftlink.entrySet())){
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
                        for(VoiceChannel chan : new HashSet<>(category.getVoiceChannels())){
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
                    waitingchannel.sendMessage(new TranslatableText(Main.getTranslator(), language).get("events.player-leave-message").replaceAll("%user%",member.getAsMention())).queue();
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
                        DiscordBot.addPendingAction(channels.get(i).delete());
                        if(i == channels.size()-1){
                            DiscordBot.addPendingAction(cat.delete());
                        }
                    }
                    DiscordBot.triggerActionQueue();
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

    public TextChannel getRoleChannel(PlayerRole role){
        if(role instanceof TextRole){
            for(Map.Entry<TextChannel, TextRole> entry : rolechannels.entrySet()){
                if(entry.getValue().equals(role)){
                    return entry.getKey();
                }
            }
        }

        return maintextchannel;
    }
    
    public void killUser(UserId id){
        pendingdeath.add(id);
        for(BackgroundEvent event : backgroundevents){
            event.onPlayerKilled(id);
        }
    }
    
    public void resurrectUser(UserId id){
        pendingdeath.remove(id);
        for(BackgroundEvent event : backgroundevents){
            event.onPlayerSaved(id);
        }
    }
    
    public Set<UserId> getPendingDeath(){
        return Set.copyOf(pendingdeath);
    }
    
    public Set<PrimaryRole> getUsedRoles(){
        Set<PrimaryRole> allroles = new HashSet<>();
        for(Map.Entry<UserId, WerewolfPlayer> entry : playerlink.entrySet()){
            // Here we are not using WerewolfPlayer.getMainRole() to ensure we correctly get the role event if WerewolfPlayer.mainrole is not correctly set
            List<PlayerRole> usroles = entry.getValue().getRoles();
            for(PlayerRole role : usroles){
                if(role instanceof PrimaryRole prole){
                    allroles.add(prole);
                    break;
                }
            }
        }
        
        return allroles;
    }
    
    public @NotNull List<UserId> getUsersWithRole(PlayerRole role){
        List<UserId> users = new ArrayList<>();
        if(role != null){
            for(Map.Entry<UserId, WerewolfPlayer> entry : playerlink.entrySet()){
                for(PlayerRole prole : entry.getValue().getRoles()){
                    if(prole.equals(role)){
                        users.add(entry.getKey());
                    }
                }
            }
        }
        return users;
    }
/*
    |------------------------------------------------|
    |================[TEAMS OPTIONS]=================|
    |------------------------------------------------|
*/
    public void registerNewTeam(String team){
        if(team != null){
            teams.putIfAbsent(team.toUpperCase(Locale.ROOT), new ArrayList<>());
        }
    }
    public void registerNewTeam(DefaultTeam team){
        registerNewTeam(team.toString());
    }
    public List<String> getRegisteredTeams(){
        return List.copyOf(teams.keySet());
    }
    public @Nullable List<UserId> getTeamPlayers(String team){
        return team != null && teams.containsKey(team) ? List.copyOf(teams.get(team)) : null;
    }
    public @Nullable List<UserId> getTeamPlayers(DefaultTeam team){
        return getTeamPlayers(team.toString());
    }
    public void addPlayerToTeam(String team, UserId playerid){
        List<UserId> list = teams.get(team);
        if(list != null && !list.contains(playerid)){
            list.add(playerid);
        }
    }
    public void addPlayerToTeam(DefaultTeam team, UserId playerid){
        addPlayerToTeam(team.toString(),playerid);
    }
    public void removePlayerFromTeam(String team, UserId playerid){
        List<UserId> list = teams.get(team);
        if(list != null){
            list.remove(playerid);
        }
    }
    public void removePlayerFromTeam(DefaultTeam team, UserId playerid){
        removePlayerFromTeam(team.toString(), playerid);
    }
    public @Nullable String getPlayerTeam(UserId playerid){
        if(playerid != null){
            for(Map.Entry<String, List<UserId>> entry : teams.entrySet()){
                if(entry.getValue().contains(playerid)){
                    return entry.getKey();
                }
            }
        }
        return null;
    }
    
    public boolean doTeamExists(String team){
        if(team == null){
            return false;
        }
        return teams.containsKey(team);
    }
    
    public boolean doTeamExists(DefaultTeam team){
        return doTeamExists(team.toString());
    }
    
    public List<String> getAllTeams(Iterable<UserId> userlist){
        List<String> allteams = new ArrayList<>();
        for(Map.Entry<String, List<UserId>> entry : teams.entrySet()){
            for(UserId user : userlist){
                String checkteam = entry.getKey();
                if(entry.getValue().contains(user) && !allteams.contains(checkteam)){
                    allteams.add(checkteam);
                }
            }
        }
        
        return allteams;
    }

    public boolean checkSameTeam(List<UserId> userlist){
        if(userlist.size() > 0){
            String team = getPlayerTeam(userlist.get(0));
            List<UserId> players = getTeamPlayers(team);
            if(team != null && players != null){
                return new HashSet<>(players).containsAll(userlist);
            }
            
        }
        return false;
    }
/*
    |------------------------------------------------|
    |=================[EVENT QUEUE]==================|
    |------------------------------------------------|
*/
    
    public boolean checkForWinCondition(){
        List<UserId> aliveplayers = new ArrayList<>(playerlink.keySet());
        aliveplayers.removeIf(u -> !playerlink.get(u).isAlive() || pendingdeath.contains(u));
        if(checkSameTeam(aliveplayers)){
            String teamname = getPlayerTeam(aliveplayers.get(0));
            Main.logAdmin("Teams Victory",teamname);
            eventqueue.clear();
            currentevent = null;
            for(UserId u : discordlink.keySet()){
                try{
                    DiscordBot.addPendingAction(getGuild().mute(UserSnowflake.fromId(DatabaseManager.getDiscordId(u)), false));
                } catch (IllegalArgumentException | IllegalStateException ignored){}
            }
            
            if(teamname != null && teamname.length() > 0){
                // Capitalize the first letter of the team's name
                teamname = teamname.substring(0,1).toUpperCase(Locale.ROOT) + teamname.substring(1).toLowerCase(Locale.ROOT);
                maintextchannel.sendMessage(guild.getPublicRole().getAsMention()+" \n"+new TranslatableText(Main.getTranslator(),language).get("teams.generic.win-message").replaceAll("%team%",teamname)).queue(m ->
                        maintextchannel.sendMessage("https://c.tenor.com/bHGXw7bf04QAAAAd/carla-fortnite.gif").queueAfter(1,TimeUnit.SECONDS));
            }
            DiscordBot.triggerActionQueue();
            return true;
        } else {
            return false;
        }
    }
    
    private Set<PlayerRole> getCompleteRolePool(){
        Set<PlayerRole> completeroles = new HashSet<>(Set.copyOf(rolepool.keySet()));
        completeroles.add(new Wolf(Main.getRootExtensionn()));
        completeroles.add(new Villager(Main.getRootExtensionn()));
        return completeroles;
    }
    
    public void startGame() {
        Main.logAdmin("Let's gooooooooooooooooooo");
        for (int i = 0; i < 5; i++) {
            UserId userid = new UserId();
            logEvent("adding fake player " + userid, LogDestination.CONSOLE);
            playerset.add(userid);
        }
        lock();
        waitingchannel.sendMessage(new TranslatableText(Main.getTranslator(), language).get("channels.game-started").replaceAll("%channel%", maintextchannel.getAsMention())).queue();
        setChannelVisible(maintextchannel, true).queue(c -> setChannelLocked(maintextchannel, true).queue());
        setChannelLocked(waitingchannel, true).queue();
        maintextchannel.sendMessage(guild.getPublicRole().getAsMention()).queue();
        
        registerNewTeam(DefaultTeam.VILLAGE);
        registerNewTeam(DefaultTeam.WOLVES);
        
        assignRoles();
    
        //create role channels
        // TODO : OPTIMISE THE SEARCH
        for (PlayerRole role : getCompleteRolePool()) {
            if (role instanceof TextRole chanrole && ((TextRole) role).getChannelName(language) != null && ((TextRole) role).getChannelName(language).length() > 0) {
                ChannelAction<TextChannel> createaction = category.createTextChannel(((TextRole) role).getChannelName(language));
                List<UserId> playerswithrole = new ArrayList<>();
                for (Map.Entry<UserId, WerewolfPlayer> entry : playerlink.entrySet()) {
                    if (entry.getValue().getRoles().contains(chanrole)) {
                        playerswithrole.add(entry.getKey());
                    }
                }
                createaction = createaction.addRolePermissionOverride(playerrole.getIdLong(), List.of(Permission.UNKNOWN), List.of(Permission.VIEW_CHANNEL));
                createaction = createaction.addRolePermissionOverride(adminrole.getIdLong(), List.of(Permission.VIEW_CHANNEL), List.of(Permission.MESSAGE_SEND, Permission.MANAGE_CHANNEL, Permission.MESSAGE_ADD_REACTION));
                for (UserId id : playerswithrole) {
                    String discordid = DatabaseManager.getDiscordId(id);
                    if (discordid != null) {
                        createaction = createaction.addMemberPermissionOverride(UserSnowflake.fromId(discordid).getIdLong(), chanrole.getChannelAllow(), chanrole.getChannelDeny());

                    }
                }
                createaction.queue(chan -> {
                    this.rolechannels.put(chan, chanrole);
                    Main.linkChannel(chan.getIdLong(), this.getId());
                    if (role.getClass().equals(Wolf.class)) {
                        this.wolfchannel = chan;
                    }
                    if (chanrole.getHelpMessageEmbed(language) != null) {
                        chan.sendMessageEmbeds(chanrole.getHelpMessageEmbed(language)).queue();
                        for(UserId id : playerswithrole){
                            if(id.toString().equals("sawors01")){
                                chan.sendMessage(UserSnowflake.fromId(DatabaseManager.getDiscordId(id)).getAsMention()).queue();
                            }
                        }
                    }
                    if (chanrole.getIntroMessage(language) != null && chanrole.getIntroMessage(language).length() > 0) {
                        chan.sendMessage(chanrole.getIntroMessage(language)).queue();
                    }
                });
    
            }
        }
        gamephase = GamePhase.SUNRISE;
        buildFirstDayQueue();
        nextEvent();
    }
    
    public void nextEvent(){
        if(eventqueue.isEmpty()){
            Main.logAdmin("no more events, THIS IS AN ERROR");
        } else {
            GameEvent next = eventqueue.poll();
            currentevent = next;
            Main.logAdmin("Current event",next);
            next.start(this);
        }
    }

    public Set<LinkedUser> defaultVotePool(){
        Set<LinkedUser> votepool = new HashSet<>();
        //TODO : Remove this
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
        Queue<String> fakenames = new LinkedList<>(fakenameslist);
        for(int i = 1; fakenames.size()*i <= playerset.size(); i++){
            fakenames.addAll(fakenameslist);
        }
        // is used to put name in the newly created LinkedUser
        //DatabaseManager.getUserData(uid, UserDataType.NAME) != null ? DatabaseManager.getUserData(uid, UserDataType.NAME) : fakenames.poll()
        playerset.forEach(uid -> votepool.add(new LinkedUser(uid, uid.toString(),UUID.randomUUID(),"",null,null)));
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
    
    public void setGamePhase(GamePhase phase){
        Main.logAdmin("Changing game phase",gamephase+" -> "+phase);
        this.gamephase = phase;
    }
    
    private Map<GameEvent, GamePhase> getUsedEvents(){
        Map<GameEvent, GamePhase> events = new HashMap<>();
        Main.logAdmin("GetUsedRoles",getUsedRoles());
        for(PrimaryRole role : getUsedRoles()){
            Main.logAdmin("Role Check", role);
            boolean isholderalive = false;
            for(WerewolfPlayer player : playerlink.values()){
                if(player != null && player.getMainRole().equals(role) && player.isAlive()){
                    isholderalive = true;
                    break;
                }
                Main.logAdmin("player != null",player != null);
                try{
                    Main.logAdmin("player.getMainRole().equals(role)",player.getMainRole().equals(role));
                } catch (NullPointerException e){
                    Main.logAdmin("player is null, NullPointerException");
                }
                Main.logAdmin("player.isAlive()",player.isAlive());
            }
            if(isholderalive){
                events.putAll(role.getEvents());
                events.putAll(role.getRoundEvents(round));
            }
            Main.logAdmin("getevents",role.getEvents());
            Main.logAdmin("holder alive",isholderalive);

        }
        Main.logAdmin("Used Events (debug)",events);
        return events;
    }

    private void buildNightQueue(){
        round++;
        Map<GameEvent, GamePhase> events = getUsedEvents();
        for(Map.Entry<GameEvent, GamePhase> entry : events.entrySet()){
            if(entry.getValue().equals(GamePhase.NIGHT_PREWOLVES)){
                eventqueue.add(entry.getKey());
            }
        }
        eventqueue.add(new WolfKillEvent(Main.getRootExtensionn()));
        for(Map.Entry<GameEvent, GamePhase> entry : events.entrySet()){
            if(entry.getValue().equals(GamePhase.NIGHT_WOLVES)){
                eventqueue.add(entry.getKey());
            }
        }
        for(Map.Entry<GameEvent, GamePhase> entry : events.entrySet()){
            if(entry.getValue().equals(GamePhase.NIGHT_POSTWOLVES)){
                eventqueue.add(entry.getKey());
            }
        }
        eventqueue.add(new SunriseEvent(Main.getRootExtensionn()));
        for(GameEvent event : eventqueue){
            Main.logAdmin("Night Queue",event);
        }
    }

    private void buildDayQueue(){
        Map<GameEvent, GamePhase> events = getUsedEvents();
        for(Map.Entry<GameEvent, GamePhase> entry : events.entrySet()){
            if(entry.getValue().equals(GamePhase.SUNRISE)){
                eventqueue.add(entry.getKey());
            }
        }
        eventqueue.add(new DeathValidateEvent(Main.getRootExtensionn(), true));
        eventqueue.add(new VillageVoteEvent(Main.getRootExtensionn()));
        for(Map.Entry<GameEvent, GamePhase> entry : events.entrySet()){
            if(entry.getValue().equals(GamePhase.VILLAGE_VOTE)){
                eventqueue.add(entry.getKey());
            }
        }
        eventqueue.add(new DeathValidateEvent(Main.getRootExtensionn(), false));
        for(Map.Entry<GameEvent, GamePhase> entry : events.entrySet()){
            if(entry.getValue().equals(GamePhase.NIGHTFALL)){
                eventqueue.add(entry.getKey());
            }
        }
        eventqueue.add(new NightfallEvent(Main.getRootExtensionn()));
        for(GameEvent event : eventqueue){
            Main.logAdmin("Day Queue",event);
        }
    }

    private void buildFirstDayQueue(){
        eventqueue.add(new IntroEvent(Main.getRootExtensionn()));
        eventqueue.add(new MayorVoteEvent(Main.getRootExtensionn()));
        eventqueue.add(new NightfallEvent(Main.getRootExtensionn()));
        for(GameEvent event : eventqueue){
            Main.logAdmin("First Day Queue",event);
        }
    }
    
    public Set<UserId> getRealPlayers(){
        Set<UserId> output = new HashSet<>();
        for(UserId usid: playerset){
            if(DatabaseManager.getDiscordId(usid) != null && DatabaseManager.getDiscordId(usid).length() >= 6){
                output.add(usid);
            }
        }
        return output;
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
        logEvent("Complete role pool : "+getCompleteRolePool(), LogDestination.CONSOLE);

        List<PlayerRole> villageuniqueroles = new ArrayList<>(getCompleteRolePool());
        villageuniqueroles.removeIf(role -> role instanceof Villager);
        villageuniqueroles.removeIf(role -> role instanceof Wolf);
        villageuniqueroles.removeIf(role -> !(role instanceof PrimaryRole));
        Collections.shuffle(villageuniqueroles);
        Queue<PlayerRole> pendingroles = new LinkedList<>(villageuniqueroles);
        logEvent("Pending village roles : "+pendingroles, LogDestination.CONSOLE);

        List<PlayerRole> wolfroles = new ArrayList<>(getCompleteRolePool());
        wolfroles.removeIf(role -> !(role instanceof Wolf));
        wolfroles.removeIf(role -> !(role instanceof PrimaryRole));
        Collections.shuffle(wolfroles);
        Queue<PlayerRole> pendingwolves = new LinkedList<>(wolfroles);
        logEvent("Pending wolf roles : "+pendingwolves, LogDestination.CONSOLE);

        options.computeWolfAmount(playercount);

        if(playercount < 4){
            throw new IndexOutOfBoundsException("too few players to start the game (must be > 4, got "+playercount);
        }

        // assigning roles
        if(options.autoWolf()){
            logEvent("Using autowolf with wolves percentage set to "+(int)(options.autowolfPercentage()*100)+"%", LogDestination.CONSOLE);
        }
        logEvent("Wolves amount : "+options.wolfAmount(), LogDestination.CONSOLE);
        for(int i = 0; i < options.wolfAmount(); i++){
            UserId user = pendingusers.poll();
            if(user == null){
                throw new IndexOutOfBoundsException("too few players to start the game, could not give wolf roles, all players are wolves (???? Serious issue, please report it to https://github.com/Sawors/WerewolfGame/issues/new");
            }
            PlayerRole role = pendingwolves.poll();
            if(role == null){
                role = new Wolf(Main.getRootExtensionn());
            }
            logEvent("Giving role "+role+" to player "+user+" (Wolf Phase)", LogDestination.CONSOLE);
            usedroles.add(role);
            addPlayerToTeam(DefaultTeam.WOLVES,user);
            playerlink.put(user, new WerewolfPlayer(user, this, (PrimaryRole) role));
        }
        for(UserId user : pendingusers){
            PlayerRole role = pendingroles.poll();
            if(role == null){
                role = new Villager(Main.getRootExtensionn());
            }
            logEvent("Giving role "+role+" to player "+user+" (Village Phase)", LogDestination.CONSOLE);
            usedroles.add(role);
            addPlayerToTeam(DefaultTeam.VILLAGE,user);
            playerlink.put(user, new WerewolfPlayer(user, this, (PrimaryRole) role));
        }
        logEvent(playerlink, LogDestination.CONSOLE);
    }

    public void setupTimedAction(int seconds, Runnable action){
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(action,seconds,TimeUnit.SECONDS);
    }
    
    public List<BackgroundEvent> getBackgroundEvents() {
        return this.backgroundevents;
    }
    
    public void overwriteCurrentEvent(GameEvent event){
        this.currentevent = event;
    }
}
