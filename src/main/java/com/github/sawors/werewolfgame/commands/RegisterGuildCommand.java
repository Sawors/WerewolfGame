package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

import java.util.Objects;

public class RegisterGuildCommand implements GameCommand{
    @Override
    public void execute() {
    
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
    
    public void execute(GenericMessageEvent source) {
        if(source.isFromGuild()){
            DatabaseManager.registerGuildAuto(source.getGuild());
            source.getChannel().sendMessage("Server *"+source.getGuild().getName()+":"+source.getGuild().getId()+"* successfully registered").queue();
        } else {
            source.getChannel().sendMessage(new TranslatableText(Main.getTranslator(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(source.getGuild()))).get("commands.error-messages.private-message-error")).queue();
        }
    }
    
    
}
