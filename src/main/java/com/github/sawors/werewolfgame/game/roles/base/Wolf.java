package com.github.sawors.werewolfgame.game.roles.base;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.TextRole;
import com.github.sawors.werewolfgame.game.roles.WolfLike;
import com.github.sawors.werewolfgame.localization.LoadedLocale;
import com.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Wolf extends WolfLike implements TextRole {
    public Wolf(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String toString() {
        return DefaultRoleType.WOLF.toString();
    }

    @Override
    public void wolfAction() {

    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }
    
    @Override
    public String getChannelName(@Nullable LoadedLocale language) {
        return new TranslatableText(getExtension().getTranslator(), language).get("roles.wolf.channel");
    }
    @Override
    public Collection<Permission> getChannelAllow(){
        return List.of(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_ADD_REACTION
        );
    }
    @Override
    public Collection<Permission> getChannelDeny(){
        return List.of(
                Permission.MANAGE_CHANNEL
        );
    }
    
    @Override
    public String getIntroMessage() {
        return "Welcome to the Wolves text channel";
    }
    
    @Override
    public MessageEmbed getHelpMessageEmbed() {
        return null;
    }
}
