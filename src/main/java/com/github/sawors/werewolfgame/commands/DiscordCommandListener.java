package com.github.sawors.werewolfgame.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordCommandListener extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        String[] args = content.split(" ");
        if(args.length > 1 && args[0].equals("!ww")){
            switch(args[1]){
                case"test":
                    new TestCommand().execute(event.getMessage());
                    break;
                case"register":
                case"reg":
                    if(event.isFromGuild()){
                        new RegisterGuildCommand().execute(event);
                    } else {
                        event.getChannel().sendMessage("This command does not work in private messages").queue();
                    }
                    break;
            }
        }
    }
}
