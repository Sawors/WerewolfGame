package com.github.sawors.werewolfgame.commands;

public interface GameCommand {
    void execute();
    boolean isAdminOnly();
}
