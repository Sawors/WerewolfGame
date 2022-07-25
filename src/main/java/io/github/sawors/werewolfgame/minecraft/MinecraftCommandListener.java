package io.github.sawors.werewolfgame.minecraft;

import io.github.sawors.werewolfgame.commands.TestCommand;
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
                    new TestCommand().execute();
                    break;
            }
            return true;
        }

        return false;
    }
}
