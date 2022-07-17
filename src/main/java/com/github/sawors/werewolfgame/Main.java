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
    private static HashMap<String, GameManager> activegames;
    private static boolean standalone;
    private static boolean discordenabled = false;
    private static boolean minecraftenabled = false;
    private static JDA jda;
    private static File datalocation;
    private static File dbfile;
    private static boolean usecaching = true;
    private static HashMap<UserId, LinkedUser> cachedusers;


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
    
    protected static File getDbFile(){
        return dbfile;
    }

    public static void registerNewGame(GameManager manager){
        String id = generateRandomGameId();
        activegames.put(generateRandomGameId(), manager);
    }

    public static String generateRandomGameId(){
        return RandomStringUtils.randomNumeric(8);
    }

    protected JDA getJDA(){
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
