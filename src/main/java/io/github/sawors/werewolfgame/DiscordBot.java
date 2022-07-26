package io.github.sawors.werewolfgame;

import io.github.sawors.werewolfgame.discord.DiscordCommandListener;
import io.github.sawors.werewolfgame.discord.DiscordInteractionsListener;
import io.github.sawors.werewolfgame.discord.DiscordListeners;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.RestAction;

import javax.security.auth.login.LoginException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordBot {
    
    private static Queue<RestAction<?>> pending = new LinkedList<>();
    
    protected static JDA initJDA(String token, boolean standalone){
        JDABuilder builder = JDABuilder.createDefault(token);
        builder
            .addEventListeners(new DiscordListeners())
            .addEventListeners(new DiscordCommandListener())
            .addEventListeners(new InstanceCommandListeners())
            .addEventListeners(new DiscordInteractionsListener());
        
        try{
            Main.logAdmin("Successfully started Discord Bot !");
            return builder.build();
        }catch (LoginException | IllegalArgumentException e){
            Main.logAdmin("Discord token not found, disabling Discord bot features");
            return null;
        }
    }
    
    public static void addPendingAction(RestAction<?> action){
        pending.add(action);
    }
    
    //TODO : Implement that
    public static void triggerActionQueue(){
        final Queue<RestAction<?>> localpending = new LinkedList<>(List.copyOf(pending));
        pending.clear();
        Main.logAdmin("Pending",localpending);
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            if(localpending.size() > 0){
                localpending.poll().queue();
            }
        }, 100, 200, TimeUnit.MILLISECONDS);
    }
}
