package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MinecraftCommandListener implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length >= 1){
            switch (args[0]){
                case"start":
                    break;
                case"test":
                    Main.logAdmin("Player IDs");
                    for(int i = 0; i<=8; i++){
                        Main.logAdmin(Main.generateRandomPlayerId());
                    }
                    Main.logAdmin("\nGame IDs");
                    for(int i = 0; i<=8; i++){
                        Main.logAdmin(Main.generateRandomGameId());
                    }
                    break;
            }
            return true;
        }

        return false;
    }
}
