package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.commands.DiscordCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class DiscordBot {
    private static GameManager manager;


    protected static JDA initJDA(String token, boolean standalone){
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new DiscordListeners());
        builder.addEventListeners(new DiscordCommandListener());
        try{
            JDA jda = builder.build();
            Main.logAdmin("Successfully started Discord Bot !");
            return jda;
        }catch (LoginException | IllegalArgumentException e){
            Main.logAdmin("Discord token not found, disabling Discord bot features");
            return null;
        }
    }
}
