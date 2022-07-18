package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TestCommand implements GameCommand{
    @Override
    public void execute() {
        Main.logAdmin("Player IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(new UserId());
        }
        Main.logAdmin("\nGame IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(Main.generateRandomGameId());
        }
    }
    
    public void execute(Message msg) {
        MessageChannel chan = msg.getChannel();
        chan.sendMessage("Click **HERE** to join the game").queue();
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
