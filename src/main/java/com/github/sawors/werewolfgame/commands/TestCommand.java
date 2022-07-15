package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.Main;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

public class TestCommand implements GameCommand{
    @Override
    public void execute() {
        Main.logAdmin("Player IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(Main.generateRandomPlayerId());
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
            output.append("\n   ").append(Main.generateRandomPlayerId());
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
