package io.github.sawors.werewolfgame.game;

public enum GamePhase {
    BEFORE_GAME, NIGHT_PREWOLVES, NIGHT_WOLVES, NIGHT_POSTWOLVES, SUNRISE, VILLAGE_VOTE, NIGHTFALL;
    
    public boolean isDay(GamePhase phase){
        return switch (phase) {
            case SUNRISE, VILLAGE_VOTE -> true;
            case BEFORE_GAME, NIGHT_WOLVES, NIGHTFALL, NIGHT_PREWOLVES, NIGHT_POSTWOLVES -> false;
        };
    }
}
