package com.github.sawors.werewolfgame.game;

public enum GamePhase {
    FIRST_DAY, NIGHT_PREWOLVES, NIGHT_WOLVES, NIGHT_POSTWOLVES, SUNRISE, VILLAGE_VOTE, NIGHTFALL;
    
    public boolean isDay(GamePhase phase){
        return switch (phase) {
            case FIRST_DAY, NIGHTFALL, VILLAGE_VOTE -> true;
            case NIGHT_WOLVES, SUNRISE, NIGHT_PREWOLVES, NIGHT_POSTWOLVES -> false;
        };
    }
}
