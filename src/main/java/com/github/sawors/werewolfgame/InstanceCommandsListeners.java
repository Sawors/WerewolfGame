package com.github.sawors.werewolfgame;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class InstanceCommandsListeners extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        // instance commands
        if(!event.isFromGuild() && args.length >= 1){
            PrivateChannel channel = event.getPrivateChannel();
            if(Main.isInstanceAdmin(event.getAuthor().getId())){
                switch(args[0].toLowerCase(Locale.ROOT)){
                    case"amiop":
                    case"op":
                    case"operator":
                        channel.sendMessage("You **are** an instance administrator").queue();
                    case"lang":
                        if(args.length >= 2){
                            Main.setInstanceLanguage(LoadedLocale.fromReference(args[1]));
                        } else {
                            channel.sendMessage("Instance language set to **" + Main.getLanguage()+"**").queue();
                        }
                }
            } else {
                if(args[0].length() >= 5 && args[0].substring(0,5).equalsIgnoreCase("amiop")){
                    channel.sendMessage("You **are not** an instance administrator").queue();
                }
            }
        
        
        }
    }
}
