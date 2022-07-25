package io.github.sawors.werewolfgame.commands;

public class PrintGameStatus implements GameCommand{
    @Override
    public void execute() {
        //TODO : Print all game info
        // |=======================[GameInfo]=======================|
        // |GAME_NAME                                               |
        // |- Player Count : PLAYER_COUNT                           |
        // |- On Discord : ON_DISCORD_USER_AMOUNT                   |
        // |- On Minecraft : ON_MINECRAFT_USER_AMOUNT               |
        // |- Game Phase : GAME_PHASE                               |
        // |- Night : NIGHT_INDEX                                   |
        // |Players :                                               |
        // |    - PLAYER_NAME : PLAYER_ROLE(S), STATUS, PLAYER_TYPE |
        // |    - PLAYER_NAME : PLAYER_ROLE(S), STATUS, PLAYER_TYPE |
        // |    - [...]                                             |
        // |________________________________________________________|
        //
        // <end of print game description>
        // STATUS : ALIVE, DEAD
        // PLAYER_TYPE : Discord, Minecraft, BOTH
        //
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
