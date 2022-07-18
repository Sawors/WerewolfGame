package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.discord.DiscordManager;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GameType;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DiscordCommandListener extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        String[] args = content.split(" ");
        if(args.length >= 2 && args[0].equals("!ww")){
            
            switch(args[1]){
                case"test":
                    new TestCommand().execute(event.getMessage());
                    break;
                case"register":
                case"reg":
                    if(event.isFromGuild()){
                        new RegisterGuildCommand().execute(event);
                    } else {
                        event.getChannel().sendMessage("This command does not work in private messages").queue();
                    }
                    break;
                case"set":
                    if(args.length >= 3 && event.isFromGuild()){
                        switch (args[2]){
                            case"admin":
                                if(event.getMessage().getMentions().getChannels().size() > 0){
                                    DatabaseManager.setGuildAdminChannel((TextChannel) event.getMessage().getMentions().getChannels().get(0));
                                    event.getChannel().sendMessage("**Admins Channel** successfully set to text channel "+event.getMessage().getMentions().getChannels().get(0).getAsMention()).queue();
                                } else {
                                    DatabaseManager.setGuildAdminChannel(event.getTextChannel());
                                    event.getChannel().sendMessage("**Admins Channel** successfully set to text channel "+event.getTextChannel().getAsMention()).queue();
                                }
                                return;
                            case"invites":
                                if(event.getMessage().getMentions().getChannels().size() > 0){
                                    DatabaseManager.setGuildInvitesChannel((TextChannel) event.getMessage().getMentions().getChannels().get(0));
                                    event.getChannel().sendMessage("**Invitation's Channel** successfully set to text channel "+event.getMessage().getMentions().getChannels().get(0).getAsMention()).queue();
                                } else {
                                    DatabaseManager.setGuildInvitesChannel(event.getTextChannel());
                                    event.getChannel().sendMessage("**Invitation's Channel** successfully set to text channel "+event.getTextChannel().getAsMention()).queue();
                                }
                                return;
                            case"waiting":
                                if(args.length >= 4){
                                    StringBuilder voicename = new StringBuilder();
                                    for(int i = 3; i< args.length; i++){
                                        voicename.append(args[i]);
                                        if(i < args.length-1){
                                            voicename.append(" ");
                                        }
                                    }
                                    for(VoiceChannel chan : event.getMessage().getGuild().getVoiceChannels()){
                                        if(chan.getName().equals(voicename.toString())){
                                            DatabaseManager.setGuildWaitingChannel(chan);
                                            event.getChannel().sendMessage("**Waiting Room** successfully set to existing voice channel ***"+chan.getName()+"***").queue();
                                            return;
                                        }
                                    }
                                    try{
                                        ChannelAction<VoiceChannel> act = event.getGuild().createVoiceChannel(voicename.toString());
                                        Consumer<VoiceChannel> setchan = DatabaseManager::setGuildWaitingChannel;
                                        act.queue(setchan);
                                        event.getChannel().sendMessage("**Waiting Room** successfully set to new voice channel *"+voicename+"*").queue();
                                        return;
                                    } catch (InsufficientPermissionException e){
                                        event.getChannel().sendMessage("Can't create voice channel *"+voicename+"*"+", permission **Manage Channels** not granted").queue();
                                        return;
                                    }
                                }
                        }
                    }
                    break;
                case"invites":
                case"invitations":
                case"invs":
                    if(args.length >= 3){
                        Main.logAdmin(args[2]);
                        GameManager gm = GameManager.fromId(args[2]);
                        if(gm != null){
                            gm.sendInvites();
                        }
                    }
                    break;
                case"create":
                    GameManager gm = new GameManager(event.getGuild(), GameType.DISCORD);
                    gm.sendInvites();
                    event.getChannel().sendMessage("Game created ! ID : "+gm.getGameID()).queue();
                case"clean":
                case"clear":
                    if(args.length >= 3){
                        Main.logAdmin(args[2]);
                        GameManager gmclear = GameManager.fromId(args[2]);
                        if(gmclear != null){
                            gmclear.clean();
                            
                        } else {
                            for(Category cat : event.getGuild().getCategories()){
                                if(cat.getName().contains("WEREWOLF : "+args[2])){
                                    DiscordManager.cleanCategory(cat);
                                    break;
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }
}
