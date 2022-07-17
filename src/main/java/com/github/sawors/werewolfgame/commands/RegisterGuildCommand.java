package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.DatabaseManager;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

import java.util.List;

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
    
            TextChannel admins = null;
            TextChannel invites = null;
            VoiceChannel waiting = null;
            
            List<GuildChannel> channels = source.getGuild().getChannels();
            for(GuildChannel chan : channels){
                String name = chan.getName();
                switch(name){
                    case"lg-admins":
                    case"lg-admin":
                        if(chan.getType().isMessage()){
                            admins = (TextChannel) chan;
                        }
                        break;
                    case"lg-invites":
                    case"lg-parties":
                        if(chan.getType().isMessage()){
                            invites = (TextChannel) chan;
                        }
                        break;
                    case"LG Waiting Room":
                    case"lg waiting room":
                        if(chan.getType().isAudio()){
                            waiting = (VoiceChannel) chan;
                        }
                        break;
                }
            }
    
            DatabaseManager.registerGuild(source.getGuild(),admins,invites,waiting);
            
            source.getChannel().sendMessage("Server *"+source.getGuild().getName()+":"+source.getGuild().getId()+"* successfully registered").queue();
        } else {
            source.getChannel().sendMessage("This command does not work in private messages").queue();
        }
        
    }
}
