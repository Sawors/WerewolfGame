package com.github.sawors.werewolfgame;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public class DiscordBot {
    private static JDA jda;
    private static String token = "token";
    private static boolean discordenabled = false;


    protected static JDA initJDA(){
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new DiscordListeners());



        try{
            jda = builder.build();
            discordenabled = true;
            return jda;
        }catch (LoginException e){
            e.printStackTrace();
            Bukkit.getLogger().log(Level.WARNING, "Discord token not found, disabling Discord bot features");
            return null;
        }
    }

    public static boolean isDiscordEnabled(){
        return discordenabled;
    }

    protected JDA getJDA(){
        return this.jda;
    }
}
