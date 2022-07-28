package io.github.sawors.werewolfgame.game.roles.base;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import io.github.sawors.werewolfgame.game.roles.TextRole;
import io.github.sawors.werewolfgame.game.roles.VillagerLike;
import io.github.sawors.werewolfgame.localization.LoadedLocale;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Villager extends VillagerLike implements TextRole {
    public Villager(WerewolfExtension extension) {
        super(extension);
    }

    @Override
    public Set<GameEvent> getEvents() {
        return Set.of();
    }

    @Override
    public String toString() {
        return DefaultRoleType.VILLAGER.toString();
    }
    
    @Override
    public String getChannelName(@Nullable LoadedLocale language) {
        return new TranslatableText(getExtension().getTranslator(), language).get("roles.villager.channel");
    }
    
    @Override
    public Collection<Permission> getChannelAllow() {
        return List.of(Permission.VIEW_CHANNEL);
    }
    
    @Override
    public Collection<Permission> getChannelDeny() {
        return List.of(Permission.MESSAGE_SEND, Permission.CREATE_PUBLIC_THREADS);
    }
    
    @Override
    public MessageEmbed getHelpMessageEmbed() {
        return null;
    }
    
    @Override
    public String getIntroMessage() {
        return null;
    }
    
    @Override
    public void onMessageSent(GenericMessageEvent event) {
    
    }
    
    @Override
    public void onReactionAdded(GenericMessageEvent event) {
    
    }
    
    @Override
    public void onReactionRemoved(GenericMessageEvent event) {
    
    }
}
