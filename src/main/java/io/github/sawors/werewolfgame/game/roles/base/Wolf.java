package io.github.sawors.werewolfgame.game.roles.base;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.roles.TextRole;
import io.github.sawors.werewolfgame.game.roles.WolfLike;
import io.github.sawors.werewolfgame.localization.LoadedLocale;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class Wolf extends WolfLike implements TextRole {
    public Wolf(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public String getChannelName(@Nullable LoadedLocale language) {
        return new TranslatableText(getExtension().getTranslator(), language).get("roles."+getRoleName()+".channel");
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
    public MessageEmbed getHelpMessageEmbed(LoadedLocale language) {
        TranslatableText textpool = new TranslatableText(getExtension().getTranslator(), language);
        return
                new EmbedBuilder()
                        .setTitle(textpool.get("roles.wolf.name",true))
                        .setDescription(textpool.get("roles.wolf.text",true))
                        .addField(textpool.get("roles.generic.role-description"), textpool.get("roles.wolf.role-description"),false)
                        .addField(textpool.get("roles.generic.win-condition"), textpool.get("roles.wolf.win-condition"),false)
                        .setThumbnail(textpool.get("roles.wolf.thumbnail", true))
                        .build();
    }
    
    @Override
    public String getIntroMessage(LoadedLocale language) {
        return new TranslatableText(Main.getTranslator(), language).get("roles.wolf.intro",true);
    }
    
    @Override
    public String getAnnouncementMessage(LoadedLocale locale) {
        return new TranslatableText(getExtension().getTranslator(), locale).get("roles."+getRoleName()+".announcement");
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
