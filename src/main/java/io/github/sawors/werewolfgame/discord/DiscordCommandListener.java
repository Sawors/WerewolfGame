package io.github.sawors.werewolfgame.discord;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.commands.RegisterGuildCommand;
import io.github.sawors.werewolfgame.commands.RegisterUserCommand;
import io.github.sawors.werewolfgame.commands.TestCommand;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.GameType;
import io.github.sawors.werewolfgame.game.JoinType;
import io.github.sawors.werewolfgame.localization.LoadedLocale;
import io.github.sawors.werewolfgame.localization.TranslatableText;
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
            TranslatableText texts = new TranslatableText(Main.getTranslator(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild())));
            if(!DatabaseManager.getGuildLanguage(event.getGuild()).toString().substring(0,2).equalsIgnoreCase("en")){
                String alias = texts.get("commands.aliases."+args[1].toLowerCase(Locale.ROOT), true);
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
                        event.getChannel().sendMessage(texts.get("commands.error-messages.private-message-error")).queue();
                    }
                    break;
                case"set":
                    if(args.length >= 3 && event.isFromGuild()){
                        switch (args[2]){
                            case"admin":
                                String adminsuccess = texts.get("commands.ww.set.admin-text-success");
                                if(event.getMessage().getMentions().getChannels().size() > 0){
                                    DatabaseManager.setGuildAdminChannel((TextChannel) event.getMessage().getMentions().getChannels().get(0));
                                    event.getChannel().sendMessage(adminsuccess.replaceAll("%channel%", event.getMessage().getMentions().getChannels().get(0).getAsMention())).queue();
                                } else {
                                    DatabaseManager.setGuildAdminChannel(event.getTextChannel());
                                    event.getChannel().sendMessage(adminsuccess.replaceAll("%channel%", event.getTextChannel().getAsMention())).queue();
                                }
                                return;
                            case"invites":
                                String succesmsg = texts.get("commands.ww.set.invites-text-success");
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
                                            event.getChannel().sendMessage(texts.get("commands.ww.set.waiting-vocal-existing-success").replaceAll("%name%", voicename.toString())).queue();
                                            return;
                                        }
                                    }
                                    try{
                                        ChannelAction<VoiceChannel> act = event.getGuild().createVoiceChannel(voicename.toString());
                                        Consumer<VoiceChannel> setchan = DatabaseManager::setGuildWaitingChannel;
                                        act.queue(setchan);
                                        event.getChannel().sendMessage(texts.get("commands.ww.set.waiting-vocal-created-success").replaceAll("%name%", voicename.toString())).queue();
                                        return;
                                    } catch (InsufficientPermissionException e){
                                        event.getChannel().sendMessage(texts.get("commands.ww.set.waiting-vocal-permission-error").replaceAll("%name%", voicename.toString())).queue();
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
                    event.getChannel().sendMessage(texts.get("commands.ww.create.success").replaceAll("%id%", gm.getId())).queue();
                    if(jointype == JoinType.PRIVATE){
                        event.getAuthor().openPrivateChannel().queue(chan -> chan.sendMessage(texts.get("commands.ww.create.private-game-code-message").replaceAll("%user%", event.getAuthor().getAsMention()).replaceAll("%key%", gm.getJoinKey())).queue());
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
                        event.getChannel().sendMessage(new TranslatableText(Main.getTranslator(),DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).get("commands.ww.lang.success")).queue();
                    } else {
                        event.getMessage().reply(new TranslatableText(Main.getTranslator(),DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))).get("commands.ww.lang.query")).queue();
                    }
                    break;
                case"forceclean":
                    if(args.length >= 3){
                        String id = args[2];
                        if(!Main.getGamesList().containsKey(id) && id.length() == 8){
                            GameManager.forceClean(event.getGuild(), id);
                        }
                    }
                case"regme":
                    if(args.length >= 3){
                        new RegisterUserCommand(event.getAuthor(), args[2]).execute(event.getChannel());
                    } else {
                        new RegisterUserCommand(event.getAuthor()).execute(event.getChannel());
                    }
            }
        }
        
        
        // game admin commands
        if(Main.isLinked(event.getChannel().getIdLong()) && event.isFromGuild() && !event.getAuthor().isSystem() && !event.getAuthor().isBot()){
            GameManager manager = Main.getManager(event.getChannel().getIdLong());
            if(manager != null && manager.getAdminChannel().getId().equals(event.getChannel().getId())){
                String[] commands = event.getMessage().getContentDisplay().split(" ");
    
                if(!DatabaseManager.getGuildLanguage(event.getGuild()).toString().substring(0,2).equalsIgnoreCase("en")){
                    String alias = new TranslatableText(Main.getTranslator(),DatabaseManager.getGuildLanguage(event.getGuild())).get("commands.aliases."+commands[0].toLowerCase(Locale.ROOT), true);
                    commands[0] = alias != null ? alias : commands[0];
                }
                
                if(commands.length > 0){
                    TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
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
                                if(manager.getGamePhase().equals(GamePhase.BEFORE_GAME)){
                                    manager.setLanguage(LoadedLocale.fromReference(commands[1]));
                                } else {
                                    manager.getAdminChannel().sendMessage(new TranslatableText(Main.getTranslator(),manager.getLanguage()).get("commands.admin.lang.error")).queue();
                                }
                            }
                            break;
                        case"lock":
                            if(manager.isLocked() && manager.getAdminChannel() != null){
                                manager.getAdminChannel().sendMessage(texts.get("commands.admin.lock.error")).queue();
                            } else {
                                manager.lock();
                            }
                            break;
                        case"unlock":
                            Main.logAdmin(manager.isLocked());
                            if(!manager.isLocked() && manager.getAdminChannel() != null){
                                manager.getAdminChannel().sendMessage(texts.get("commands.admin.unlock.error")).queue();
                            } else {
                                manager.unlock();
                            }
                            break;
                        case"admin":
                            for(Member member : event.getMessage().getMentions().getMembers()){
                                manager.setAdmin(member.getUser());
                                event.getChannel().sendMessage(texts.get("commands.admin.admin.success").replaceAll("%user%",member.getAsMention())).queue();
                            }
                            break;
                    }
                }
            }
        }
    }
}
