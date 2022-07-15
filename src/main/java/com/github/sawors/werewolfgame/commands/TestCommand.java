package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.PlayerId;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TestCommand implements GameCommand{
    @Override
    public void execute() {
        Main.logAdmin("Player IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(new PlayerId());
        }
        Main.logAdmin("\nGame IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(Main.generateRandomGameId());
        }
    }
    
    public void execute(Message msg) {
        MessageChannel chan = msg.getChannel();
        StringBuilder output = new StringBuilder();
        output.append("**Player IDs**");
        for(int i = 0; i<=8; i++){
            output.append("\n   ").append(new PlayerId());
        }
        output.append("\n**Game IDs**");
        for(int i = 0; i<=8; i++){
            output.append("\n   ").append(Main.generateRandomGameId());
        }
        chan.sendMessage(output.toString()).queue();
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
