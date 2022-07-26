package io.github.sawors.werewolfgame.discord;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.events.BackgroundEvent;
import io.github.sawors.werewolfgame.game.events.RoleEvent;
import io.github.sawors.werewolfgame.game.roles.TextRole;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiscordListeners extends ListenerAdapter {
    
    //|=================================================================================|\\
    //|                                                                                 |\\
    //|      THIS CLASS IS MAINLY USED TO TRIGGER SUB-EVENTS IN GAMEMANAGER EVENTS      |\\
    //|                                                                                 |\\
    //|=================================================================================|\\
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        GameManager manager = Main.getManager(event.getChannel().getIdLong());
        TextRole role = getEventRole(event);
        if(role != null){
            role.onMessageSent(event);
        }
        if(manager != null){
            for(BackgroundEvent bgevent : manager.getBackgroundEvents()){
                bgevent.onMessageSent(event);
            }
        }
    }
    
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        GameManager manager = Main.getManager(event.getChannel().getIdLong());
        TextRole role = getEventRole(event);
        if(role != null){
            role.onReactionAdded(event);
        }
        if(manager != null){
            for(BackgroundEvent bgevent : manager.getBackgroundEvents()){
                bgevent.onReactionAdded(event);
            }
        }
    }
    
    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        GameManager manager = Main.getManager(event.getChannel().getIdLong());
        TextRole role = getEventRole(event);
        if(role != null){
            role.onReactionRemoved(event);
        }
        if(manager != null){
            for(BackgroundEvent bgevent : manager.getBackgroundEvents()){
                bgevent.onReactionRemoved(event);
            }
        }
    }
    
    
    private @Nullable TextRole getEventRole(GenericMessageEvent event){
        GameManager manager = Main.getManager(event.getChannel().getIdLong());
        if(event.isFromGuild() && manager != null && manager.getCurrentEvent() instanceof RoleEvent revent && revent instanceof TextRole role && manager.getRoleChannels().containsKey((TextChannel) event.getChannel())){
            return role;
        }
        return null;
    }
}
