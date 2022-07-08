package com.github.sawors.werewolfgame;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class DiscordBot {
    private static JDA jda;
    private static String token = "token";
    private static boolean discordenabled = false;


    protected static JDA initJDA(){
        token = WerewolfGame.getMainConfig().getString("discord-token");
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new DiscordListeners());



        try{
            jda = builder.build();
            discordenabled = true;
            WerewolfGame.logAdmin("Successfully started Discord Bot !");
            return jda;
        }catch (LoginException e){
            WerewolfGame.logAdmin("Discord token not found, disabling Discord bot features");
            //Bukkit.getLogger().log(Level.WARNING, "Discord token not found, disabling Discord bot features");
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
