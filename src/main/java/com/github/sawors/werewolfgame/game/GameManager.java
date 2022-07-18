package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.discord.ChannelType;
import com.github.sawors.werewolfgame.discord.DiscordManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

public class GameManager {

    private GameType gametype;
    private GamePhase gamephase;
    private HashMap<UserId, WerewolfPlayer> playerlist = new HashMap<>();
    private HashMap<String, UserId> discordlink = new HashMap<>();
    private HashMap<UUID, UserId> minecraftlink = new HashMap<>();
    private HashMap<GuildChannel, PlayerRole> rolechannels = new HashMap<>();
    private GuildChannel admin;
    private final String id;
    private final Guild guild;
    //private Server mcserver;
    private ArrayList<Message> invites = new ArrayList<>();
    private Category category;
    private final JoinType jointype;
    private final String joinkey;
    private VoiceChannel mainvoice;
    private Role gamerole;

    private String tutorial =
            "**Command List :**" +
            "\n"+
            "\n - `clean` : removes all channels created for this game (including this one) and deletes the category"
            ;
    
    private String invitetemplate =
                    "Join Game "+"**?ID?**"+
                    "\n" +
                    "\n*created on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm:ss"))+"*"+
                    ""
            ;
    

    public GameManager(Guild guild, GameType type, JoinType accessibility){
        this.id = Main.generateRandomGameId();
        this.gametype = type;
        this.guild = guild;
        this.jointype = accessibility;
        this.joinkey = RandomStringUtils.randomNumeric(5);
        
        createRole(
                a0 -> guild.createCategory("WEREWOLF : "+id)
                        .addRolePermissionOverride(gamerole.getIdLong(), List.of(Permission.VIEW_CHANNEL), List.of(Permission.MANAGE_CHANNEL))
                        .addRolePermissionOverride(guild.getPublicRole().getIdLong(),List.of(), List.of(Permission.VIEW_CHANNEL))
                        .queue(a1 -> {
                    category = a1;
                    this.createChannels(category);
                    }
                )
        );
        
        Main.registerNewGame(this);
    }
    
    private void createRole(Consumer<?> chainedaction){
        guild.createRole().setColor(0xffffff).setName(id).setMentionable(false).queue(a -> {
            this.gamerole = a;
            chainedaction.accept(null);
        });
    }
    
    private void createChannels(Category category){
        if(this.category == null){
            this.category = category;
        }
        if(this.category != null){
            // create admin
            createTextChannel("admin", ChannelType.ADMIN);
    
            // create main voice channel
            category.createVoiceChannel("Village Place").queue(channel -> this.mainvoice = channel);
        }
    }
    
    private void createTextChannel(String name, ChannelType type){
        this.category.createTextChannel(name).queue(channel -> cacheChannel(channel, type));
    }
    
    private void cacheChannel(GuildChannel channel, ChannelType type){
        switch(type){
            case DEAD:
                break;
            case ROLE:
                break;
            case ADMIN:
                admin = channel;
                Main.linkChannel(channel.getIdLong(), id);
                // send command tutorial in @admin
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(0x89CFF0);
                embed.setDescription(tutorial);
                ((TextChannel) admin).sendMessageEmbeds(embed.build()).queue();
                break;
            case ANNOUNCEMENTS:
                break;
        }
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
        // instead of using a set
        if(playerid != null){
            if(!playerlist.containsKey(playerid)){
                playerlist.put(playerid, new WerewolfPlayer());
            }
            String discord = DatabaseManager.getDiscordId(playerid);
            if(discord != null && !discordlink.containsKey(discord)){
                discordlink.put(discord, playerid);
                try{
                    guild.addRoleToMember(UserSnowflake.fromId(discord), gamerole).queue();
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
        Main.logAdmin(playerlist);
        Main.logAdmin(discordlink);
        Main.logAdmin(minecraftlink);
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
        return admin;
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
        Button joinbutton = Button.primary("join:"+id, "Join Game");
        if(jointype == JoinType.PRIVATE){
            joinbutton = Button.secondary("joinprivate:"+id, "Join Private Game");
        }
        EmbedBuilder builder = new EmbedBuilder();
            builder
                .setDescription(invitetemplate.replace("?ID?", id))
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

}
