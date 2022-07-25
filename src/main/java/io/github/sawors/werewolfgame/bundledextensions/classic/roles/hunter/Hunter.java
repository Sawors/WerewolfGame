package io.github.sawors.werewolfgame.bundledextensions.classic.roles.hunter;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import io.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.Set;

public class Hunter extends PrimaryRole  {
    public Hunter(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String toString() {
        return DefaultRoleType.HUNTER.toString();
    }
    
    @Override
    public Integer priority() {
        return null;
    }
    
    @Override
    public void onDeathAction() {
        //TODO : Hunter action
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
}
