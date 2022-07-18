package com.github.sawors.werewolfgame.discord;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DiscordListeners extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Main.logAdmin("pass 1");
        if(Main.isLinked(event.getChannel().getIdLong()) && event.isFromGuild() && !event.getAuthor().isSystem() && !event.getAuthor().isBot()){
            Main.logAdmin("pass 2");
            GameManager manager = Main.getManager(event.getChannel().getIdLong());
            if(manager != null && manager.getAdminChannel().getId().equals(event.getChannel().getId())){
                Main.logAdmin("pass 3");
                String[] commands = event.getMessage().getContentDisplay().split(" ");
                if(commands.length > 0){
                    Main.logAdmin("pass 4");
                    switch(commands[0]){
                        case"clean":
                            Main.logAdmin("pass 5");
                            manager.clean();
                            break;
                    }
                }
            }
        }
    }
}
