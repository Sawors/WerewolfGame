package com.github.sawors.werewolfgame;

import net.dv8tion.jda.api.JDA;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Main {
    static HashMap<String, GameManager> activegames;
    static boolean standalone;
    static boolean discordenabled = false;
    static boolean minecraftenabled = false;
    static JDA jda;


    public static void init(boolean standalone, String token){
        Main.standalone = standalone;
        minecraftenabled = !Main.standalone && PluginLauncher.getPlugin().isEnabled();
        jda = DiscordBot.initJDA(token, Main.standalone);
        if(jda != null){
            discordenabled = true;
        }



        // shut down if no execution context is found
        if(!minecraftenabled && !discordenabled){
            throw new RuntimeException("No execution context found, " +
                    "please enable at least one : " +
                    "Minecraft by successfully starting the program as a plugin and/or Discord by providing an API token." +
                    "If launched in standalone mode, provide a Discord API token as the first argument when launching the Jar file (no interaction with Minecraft possible in this mode)");
        }
    }

    public static void registerNewGame(GameManager manager){
        String id = generateRandomGameId();
        activegames.put(generateRandomGameId(), manager);
    }

    public static String generateRandomGameId(){
        return RandomStringUtils.randomNumeric(8);
    }

    public static String generateRandomPlayerId(){
        return "WW-"+RandomStringUtils.randomAlphanumeric(4).toUpperCase(Locale.ENGLISH);
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

    protected static HashMap<String, GameManager> getGamesList(){
        return activegames;
    }
}
