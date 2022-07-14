package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.commands.DiscordCommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class DiscordBot {
    private static JDA jda;
    private static boolean discordenabled = false;
    private static boolean isstandalone = false;


    protected static JDA initJDA(String token, boolean standalone){
        isstandalone = standalone;
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.addEventListeners(new DiscordListeners());
        builder.addEventListeners(new DiscordCommandListener());
        
        try{
            jda = builder.build();
            discordenabled = true;
            String succesmsg = "Successfully started Discord Bot !";
            if(!standalone){
                WerewolfGame.logAdmin(succesmsg);
            } else {
                System.out.println(succesmsg);
            }
            return jda;
        }catch (LoginException e){
            String errormsg = "Discord token not found, disabling Discord bot features";
            if(!standalone) {
                WerewolfGame.logAdmin(errormsg);
            } else {
                System.out.println(errormsg);
            }
            return null;
        }
    }

    public static boolean isStandalone(){
        return isstandalone;
    }
    
    public static boolean isDiscordEnabled(){
        return discordenabled;
    }

    protected JDA getJDA(){
        return jda;
    }
}
