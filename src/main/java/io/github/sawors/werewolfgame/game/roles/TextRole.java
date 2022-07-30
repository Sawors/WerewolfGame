package io.github.sawors.werewolfgame.game.roles;

import io.github.sawors.werewolfgame.localization.LoadedLocale;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface TextRole {
    String getChannelName(@Nullable LoadedLocale language);
    Collection<Permission> getChannelAllow();
    Collection<Permission> getChannelDeny();
    
    MessageEmbed getHelpMessageEmbed(LoadedLocale language);
    String getIntroMessage(LoadedLocale language);
    
    void onMessageSent(GenericMessageEvent event);
    void onReactionAdded(GenericMessageEvent event);
    void onReactionRemoved(GenericMessageEvent event);
    
}
