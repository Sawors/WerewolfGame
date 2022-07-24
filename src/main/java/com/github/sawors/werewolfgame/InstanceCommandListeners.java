package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.commands.RegisterUserCommand;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class InstanceCommandListeners extends ListenerAdapter {
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
                        break;
                    case"lang":
                        if(args.length >= 2){
                            Main.setInstanceLanguage(LoadedLocale.fromReference(args[1]));
                            channel.sendMessage("Instance language set to **" + Main.getLanguage()+"**").queue();
                        } else {
                            channel.sendMessage("Instance language set to **" + Main.getLanguage()+"**").queue();
                        }
                        break;
                    case"reload":
                        if(args.length >= 2){
                            switch (args[1]) {
                                case "lang", "language" -> Main.reloadLanguages();
                                case "config" -> Main.reloadConfig();
                                default -> channel.sendMessage("Reload type not found, available :\n`lang` / `language` to reload language files\n`config` to reload config file").queue();
                            }
                        } else {
                            channel.sendMessage("Reload type available :\n`lang` / `language` to reload language files\n`config` to reload config file").queue();
                        }
                        break;
                    case"loghere":
                        if(!Main.getLogChannels().contains(event.getPrivateChannel().getId())){
                            Main.addLogChannel(event.getPrivateChannel());
                            Main.logAdmin("New log channel",event.getPrivateChannel().getName());
                        } else {
                            Main.removeLogChannel(event.getPrivateChannel());
                            Main.logAdmin("Removed log channel",event.getPrivateChannel().getName());
                        }
                    case"log":
                        if(args.length >=2){
                            String message = event.getMessage().getContentDisplay();
                            Main.logAdmin(event.getAuthor().getName()+":"+event.getAuthor().getId(), message.substring(message.indexOf("log")+4));
                        }
                }
            } else {
                if(args[0].length() >= 5 && args[0].substring(0,5).equalsIgnoreCase("amiop")){
                    channel.sendMessage("You **are not** an instance administrator").queue();
                }
            }
            switch(args[0]){
                case"instance":
                    channel.sendMessage("Instance : `"+ Main.getInstanceName()+"`").queue();
                case"regme":
                    if(args.length >= 3){
                        new RegisterUserCommand(event.getAuthor(), args[2]).execute(event.getChannel());
                    } else {
                        new RegisterUserCommand(event.getAuthor()).execute(event.getChannel());
                    }
            }
        
        
        }
    }
}
