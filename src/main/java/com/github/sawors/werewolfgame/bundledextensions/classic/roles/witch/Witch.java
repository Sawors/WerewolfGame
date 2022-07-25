package com.github.sawors.werewolfgame.bundledextensions.classic.roles.witch;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.events.GameEvent;
import com.github.sawors.werewolfgame.game.roles.DefaultRoleType;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;
import com.github.sawors.werewolfgame.game.roles.TextRole;
import com.github.sawors.werewolfgame.localization.LoadedLocale;
import com.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Witch extends PrimaryRole implements TextRole {
    public Witch(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String toString() {
        return DefaultRoleType.WITCH.toString();
    }

    @Override
    public Integer priority() {
        return 10;
    }

    @Override
    public void nightAction() {
        //TODO : Witch action
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public Set<GameEvent> getEvents() {
        return Set.of(
                new WitchPotionEvent(getExtension())
        );
    }
    
    @Override
    public String getChannelName(@Nullable LoadedLocale language) {
        return new TranslatableText(getExtension().getTranslator(), language).get("roles.witch.channel");
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
    public MessageEmbed getHelpMessageEmbed() {
        return null;
    }
    
    @Override
    public String getIntroMessage() {
        return "Welcome here Witch !";
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
