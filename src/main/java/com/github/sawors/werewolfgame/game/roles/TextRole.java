package com.github.sawors.werewolfgame.game.roles;

import com.github.sawors.werewolfgame.localization.LoadedLocale;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface TextRole {
    String getChannelName(@Nullable LoadedLocale language);
    Collection<Permission> getChannelAllow();
    Collection<Permission> getChannelDeny();
    
    MessageEmbed getHelpMessageEmbed();
    String getIntroMessage();
    
}
