package com.github.sawors.werewolfgame.extensionsloader;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.localization.Translator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class WerewolfExtension {
    public Set<PlayerRole> roles = new HashSet<>();
    public Main loader;
    public Translator translator;
    
    public WerewolfExtension(Main loader){
        this.loader = loader;
        this.translator = new Translator();
    }
    public abstract void onLoad();
    public Set<PlayerRole> getRoles() {
        return roles;
    }
    public abstract ExtensionMetadata getMeta();
    
    public void registerNewRoles(PlayerRole... role){
        roles.addAll(List.of(role));
    };
}
