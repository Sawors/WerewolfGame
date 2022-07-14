package com.github.sawors.werewolfgame.commands;

public class StartGame implements GameCommand{
    @Override
    public void execute() {
        //TODO : Start game sequence
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
