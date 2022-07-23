package com.github.sawors.werewolfgame.discord;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.LoadedLocale;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.commands.RegisterGuildCommand;
import com.github.sawors.werewolfgame.commands.TestCommand;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GameType;
import com.github.sawors.werewolfgame.game.JoinType;
import com.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class DiscordCommandListener extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        String[] args = content.split(" ");
        if(event.isFromGuild() && args.length >= 2 && args[0].equals("!ww") && !Main.isLinked(event.getChannel().getIdLong())){
            // guild commands
            if(!DatabaseManager.getGuildLanguage(event.getGuild()).toString().substring(0,2).equalsIgnoreCase("en")){
                String alias = TranslatableText.get("commands.aliases."+args[1].toLowerCase(Locale.ROOT), DatabaseManager.getGuildLanguage(event.getGuild()), true);
                args[1] = alias != null ? alias : args[1];
            }
            switch(args[1].toLowerCase(Locale.ROOT)){
                case"test":
                    new TestCommand().execute(event.getMessage());
                    break;
                case"register":
                case"reg":
                    if(event.isFromGuild()){
                        new RegisterGuildCommand().execute(event);
                    } else {
                        event.getChannel().sendMessage(TranslatableText.get("commands.error-messages.private-message-error", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild())))).queue();
                    }
                    break;
                case"set":
                    if(args.length >= 3 && event.isFromGuild()){
                        switch (args[2]){
                            case"admin":
                                String adminsuccess = TranslatableText.get("commands.ww.set.admin-text-success", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild())));
                                if(event.getMessage().getMentions().getChannels().size() > 0){
                                    DatabaseManager.setGuildAdminChannel((TextChannel) event.getMessage().getMentions().getChannels().get(0));
                                    event.getChannel().sendMessage(adminsuccess.replaceAll("%channel%", event.getMessage().getMentions().getChannels().get(0).getAsMention())).queue();
                                } else {
                                    DatabaseManager.setGuildAdminChannel(event.getTextChannel());
                                    event.getChannel().sendMessage(adminsuccess.replaceAll("%channel%", event.getTextChannel().getAsMention())).queue();
                                }
                                return;
                            case"invites":
                                String succesmsg = TranslatableText.get("commands.ww.set.invites-text-success", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild())));
                                if(event.getMessage().getMentions().getChannels().size() > 0){
                                    DatabaseManager.setGuildInvitesChannel((TextChannel) event.getMessage().getMentions().getChannels().get(0));
                                    event.getChannel().sendMessage(succesmsg.replaceAll("%channel%", event.getMessage().getMentions().getChannels().get(0).getAsMention())).queue();
                                } else {
                                    DatabaseManager.setGuildInvitesChannel(event.getTextChannel());
                                    event.getChannel().sendMessage(succesmsg.replaceAll("%channel%", event.getChannel().getAsMention())).queue();
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
                                            event.getChannel().sendMessage(TranslatableText.get("commands.ww.set.waiting-vocal-existing-success", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).replaceAll("%name%", voicename.toString())).queue();
                                            return;
                                        }
                                    }
                                    try{
                                        ChannelAction<VoiceChannel> act = event.getGuild().createVoiceChannel(voicename.toString());
                                        Consumer<VoiceChannel> setchan = DatabaseManager::setGuildWaitingChannel;
                                        act.queue(setchan);
                                        event.getChannel().sendMessage(TranslatableText.get("commands.ww.set.waiting-vocal-created-success", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).replaceAll("%name%", voicename.toString())).queue();
                                        return;
                                    } catch (InsufficientPermissionException e){
                                        event.getChannel().sendMessage(TranslatableText.get("commands.ww.set.waiting-vocal-permission-error", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).replaceAll("%name%", voicename.toString())).queue();
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
                        GameManager gm = GameManager.fromId(args[2]);
                        if(gm != null){
                            gm.sendInvite();
                        }
                    }
                    break;
                case"create":
                case"game":
                    JoinType jointype = JoinType.PUBLIC;
                    if(args.length >= 3 && args[2].equalsIgnoreCase("private")){
                        jointype = JoinType.PRIVATE;
                    }
                    GameManager gm = new GameManager(event.getGuild(), GameType.DISCORD, jointype);
                    try{
                        gm.sendInvite();
                    }catch (NullPointerException e){
                        DatabaseManager.registerGuildAuto(event.getGuild());
                        try {
                            gm.sendInvite();
                        }catch (NullPointerException ignored){}
                    }
                    gm.setOwner(event.getAuthor());
                    event.getChannel().sendMessage(TranslatableText.get("commands.ww.create.success", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).replaceAll("%id%", gm.getId())).queue();
                    if(jointype == JoinType.PRIVATE){
                        event.getAuthor().openPrivateChannel().queue(chan -> chan.sendMessage(TranslatableText.get("commands.ww.create.private-game-code-message", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).replaceAll("%user%", event.getAuthor().getAsMention()).replaceAll("%key%", gm.getJoinKey())).queue());
                    }
                case"clean":
                case"clear":
                    if(args.length >= 3){
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
                case"lang":
                case"language":
                    if(args.length >= 3){
                        
                        DatabaseManager.setGuildLanguage(event.getGuild(), LoadedLocale.fromReference(args[2]));
                        event.getChannel().sendMessage(TranslatableText.get("commands.ww.lang.success", DatabaseManager.getGuildLanguage(event.getGuild()))).queue();
                    } else {
                        event.getMessage().reply(TranslatableText.get("commands.ww.lang.query", DatabaseManager.getGuildLanguage(event.getGuild()))).queue();
                    }
                    break;
                case"forceclean":
                    if(args.length >= 3){
                        String id = args[2];
                        if(!Main.getGamesList().containsKey(id) && id.length() == 8){
                            GameManager.forceClean(event.getGuild(), id);
                        }
                    }
            }
        }
        
        
        // game admin commands
        if(Main.isLinked(event.getChannel().getIdLong()) && event.isFromGuild() && !event.getAuthor().isSystem() && !event.getAuthor().isBot()){
            GameManager manager = Main.getManager(event.getChannel().getIdLong());
            if(manager != null && manager.getAdminChannel().getId().equals(event.getChannel().getId())){
                String[] commands = event.getMessage().getContentDisplay().split(" ");
    
                if(!DatabaseManager.getGuildLanguage(event.getGuild()).toString().substring(0,2).equalsIgnoreCase("en")){
                    String alias = TranslatableText.get("commands.aliases."+commands[0].toLowerCase(Locale.ROOT), DatabaseManager.getGuildLanguage(event.getGuild()), true);
                    commands[0] = alias != null ? alias : commands[0];
                }
                
                if(commands.length > 0){
                    switch(commands[0].toLowerCase(Locale.ROOT)){
                        case"clean":
                            manager.clean();
                            break;
                        case"start":
                            manager.startGame();
                            break;
                        case"language":
                        case"lang":
                            Main.logAdmin("?");
                            if(commands.length >= 2){
                                manager.setLanguage(LoadedLocale.fromReference(commands[1]));
                            }
                            break;
                        case"lock":
                            if(manager.isLocked() && manager.getAdminChannel() != null){
                                manager.getAdminChannel().sendMessage(TranslatableText.get("commands.admin.lock.error", manager.getLanguage())).queue();
                            } else {
                                manager.lock();
                            }
                            break;
                        case"unlock":
                            Main.logAdmin(manager.isLocked());
                            if(!manager.isLocked() && manager.getAdminChannel() != null){
                                manager.getAdminChannel().sendMessage(TranslatableText.get("commands.admin.unlock.error", manager.getLanguage())).queue();
                            } else {
                                manager.unlock();
                            }
                            break;
                        case"admin":
                            for(Member member : event.getMessage().getMentions().getMembers()){
                                manager.setAdmin(member.getUser());
                                event.getChannel().sendMessage(TranslatableText.get("commands.admin.admin.success",manager.getLanguage()).replaceAll("%user%",member.getAsMention())).queue();
                            }
                            break;
                    }
                }
            }
        }
    }
}
