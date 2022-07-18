package com.github.sawors.werewolfgame.game;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.discord.ChannelType;
import com.github.sawors.werewolfgame.discord.DiscordManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
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


    public GameManager(Guild guild, GameType type){
        
        this.id = Main.generateRandomGameId();
        this.gametype = type;
        this.guild = guild;
        
        guild.createCategory("WEREWOLF : "+id).queue(this::createChannels);
        
        
        
        
        
        Main.registerNewGame(this);
    }
    
    private void createChannels(Category category){
        if(this.category == null){
            this.category = category;
        }
        if(this.category != null){
            this.category.createTextChannel("Admin").queue(admin -> cacheChannel(admin, ChannelType.ADMIN));
            
        }
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
                break;
            case ANNOUNCEMENTS:
                break;
        }
    }
    
    public void finish(){
    
    }
    
    public void clean(){
        Main.logAdmin("Cleaning game "+id);
        clearInvites();
        DiscordManager.cleanCategory(category);
        
    }
    
    private void deleteCategory(){
       if(category != null){
           Main.logAdmin("Deleted category "+category.getName());
           this.category.delete().queue();
       }
    }

    public void addPlayer(UserId playerid){
        // instead of using a set
        if(playerid != null){
            if(!playerlist.containsKey(playerid)){
                playerlist.put(playerid, new WerewolfPlayer());
            }
            String discord = DatabaseManager.getDiscordId(playerid);
            if(discord != null && !discordlink.containsKey(discord)){
                discordlink.put(discord, playerid);
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

    public String getGameID(){
        return id;
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

    public static GameManager restoreFromFile(File backup) {
        //TODO : Restoration process (priority : not important)
        return new GameManager(null, GameType.MIXED);
    }
    
    // TODO : create a method to send these invites to anybody
    public void sendInvites(){
        TextChannel chan = DatabaseManager.getGuildInvitesChannel(guild);
        if(chan != null){
            sendInvites(chan);
        } else {
            throw new NullPointerException("no channel found for guild "+guild+":"+guild.getId());
        }
    }
    
    protected void sendInvites(TextChannel channel){
        MessageAction msg =channel.sendMessage("Click **HERE** to join the game").setActionRow(Button.primary("join:"+id, "Join Game"));
        Consumer<Message> loginvite = this::logInvite;
        msg.queue(loginvite);
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
