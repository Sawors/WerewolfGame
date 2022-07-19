package com.github.sawors.werewolfgame.extensions;

import com.github.sawors.werewolfgame.game.PlayerRole;

import java.util.Set;

public abstract class WerewolfExtension {
    public abstract void onLoad();
    public abstract Set<PlayerRole> getRoles();
    public abstract ExtensionMetadata getMeta();
}
