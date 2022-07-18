package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.JDA;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Main {
    private static HashMap<String, GameManager> activegames = new HashMap<>();
    private static boolean standalone;
    private static boolean discordenabled = false;
    private static boolean minecraftenabled = false;
    private static JDA jda;
    private static File datalocation;
    private static File dbfile;
    private static boolean usecaching = true;
    private static HashMap<UserId, LinkedUser> cachedusers = new HashMap<>();
    // I use this linking map to avoid creating a new JDA Event Listener each time a new GameManager is created
    private static HashMap<Long, String> channellink = new HashMap<>();


    public static void init(boolean standalone, String token, File datastorage){
        Main.standalone = standalone;
        minecraftenabled = !Main.standalone && PluginLauncher.getPlugin().isEnabled();
        
        datastorage.mkdirs();
        datalocation = datastorage;
        dbfile = new File(datalocation+File.separator+"database.db");
        try{
            Main.logAdmin(dbfile);
            dbfile.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
            Main.logAdmin("database creation failed, could not access the file");
        }
        
        jda = DiscordBot.initJDA(token, Main.standalone);
        if(jda != null){
            discordenabled = true;
        }
        
        DatabaseManager.connectInit();


        // shut down if no execution context is found
        if(!minecraftenabled && !discordenabled){
            throw new RuntimeException("No execution context found, " +
                    "please enable at least one : " +
                    "Minecraft by successfully starting the program as a plugin and/or Discord by providing an API token." +
                    "If launched in standalone mode, provide a Discord API token as the first argument when launching the Jar file (no interaction with Minecraft possible in this mode)");
        }
    }
    
    public static void linkChannel(Long channelid, String gamemanagerid){
        channellink.put(channelid, gamemanagerid);
        Main.logAdmin(channellink);
    }
    
    public static boolean isLinked(Long channelid){
        Main.logAdmin(channellink);
        return channellink.containsKey(channelid);
    }
    
    public static void unlinkChannel(Long channelid){
        channellink.remove(channelid);
        Main.logAdmin(channellink);
    }
    
    public static GameManager getManager(Long channelid){
        return GameManager.fromId(getManagerId(channelid));
    }
    
    public static String getManagerId(Long channelid){
        return channellink.get(channelid);
    }
    
    protected static File getDbFile(){
        return dbfile;
    }

    public static void registerNewGame(GameManager manager){
        activegames.put(manager.getId(), manager);
    }

    public static String generateRandomGameId(){
        return RandomStringUtils.randomNumeric(8);
    }

    protected static JDA getJDA(){
        return jda;
    }

    public static void logAdmin(Object text){
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        time = "[WerewolfGame : "+time+"] ";

        if(standalone){
            System.out.println(time+text);
        } else {
            PluginLauncher.logToOp(time+text);
        }
    }

    public static HashMap<String, GameManager> getGamesList(){
        return activegames;
    }
    
    public static void cacheUser(UserId id, LinkedUser user){
        cachedusers.put(id,user);
    }
    
    public static void cacheUser(LinkedUser user){
        cachedusers.put(user.getId(),user);
    }
    
    public static boolean useUserCache(){
        return usecaching;
    }
    
    protected static LinkedUser getCachedUser(UserId id){
        return cachedusers.get(id);
    }
}
