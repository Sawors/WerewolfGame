package io.github.sawors.werewolfgame.discord;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.game.events.GenericVote;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DiscordInteractionsListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonid = event.getInteraction().getButton().getId();
        Guild guild = event.getGuild();
        if(buttonid != null && event.isFromGuild() && guild != null && buttonid.contains(":")){
            String gameid = buttonid.substring(buttonid.indexOf(":")+1,buttonid.indexOf(":")+1+8);
            String type = buttonid.substring(0,buttonid.indexOf(":"));
            GameManager gm = GameManager.fromId(gameid);
            if(gm != null){
                switch(type){
                    case"join":
                        if(!gm.getPlayerSet().contains(UserId.fromDiscordId(event.getUser().getId()))){
                            // player validated, adding it to the game
                            gm.addplayer(UserId.fromDiscordId(event.getUser().getId()));
                        } else {
                            Member mb = event.getMember();
                            //TODO : remember to provide a blocking system when the game is started / locked (game players locking system ?)
                            if(mb != null){
                                try{
                                    guild.moveVoiceMember(event.getMember(),gm.getMainVoiceChannel()).queue();
                                } catch (InsufficientPermissionException perm){
                                    Main.logAdmin("Not enough permissions to move members in guild "+guild.getName()+":"+guild.getId());
                                } catch (IllegalStateException | IllegalArgumentException ignored){}
                            }
                        }
                        event.deferEdit().queue();
                        break;
                    case"joinprivate":
                        if(!gm.getPlayerSet().contains(UserId.fromDiscordId(event.getUser().getId()))){
                            TranslatableText texts = new TranslatableText(Main.getTranslator(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild())));
                            TextInput joincode = TextInput.create("codeinput",texts.get("forms.private-game-code.code-field-title"), TextInputStyle.SHORT)
                                    .setPlaceholder(texts.get("forms.private-game-code.text-placeholder"))
                                    .setRequired(true)
                                    .setRequiredRange(4,6)
                                    .build();
                            event.replyModal(Modal.create("codemodal:"+gm.getId(),texts.get("forms.private-game-code.title")).addActionRow(joincode).build()).queue();
                            Main.logAdmin("joined private game");
                        } else {
                            Member mb = event.getMember();
                            //TODO : remember to provide a blocking system when the game is started / locked (game players locking system ?)
                            if(mb != null){
                                try{
                                    guild.moveVoiceMember(event.getMember(),gm.getMainVoiceChannel()).queue();
                                } catch (InsufficientPermissionException perm){
                                    Main.logAdmin("Not enough permissions to move members in guild "+guild.getName()+":"+guild.getId());
                                } catch (IllegalStateException | IllegalArgumentException ignored){}
                            }
                            event.deferEdit().queue();
                        }
                        break;
                    case"leave":
                        if(gm.getPlayerSet().contains(UserId.fromDiscordId(event.getUser().getId()))){
                            // player validated, removing it from the game
                            gm.removePlayer(UserId.fromDiscordId(event.getUser().getId()));
                        } else {
                            Main.logAdmin("Attempt to remove Discord user "+event.getUser().getAsTag()+" from game "+gm.getId()+" via the leave button failed, user not in the game (THIS NEEDS TO BE INSPECTED !)");
                        }
                        event.deferEdit().queue();
                        break;
                    case"start":
                        break;
                    case"vote":
                        String votedid = buttonid.substring(buttonid.indexOf("#")+1);
                        if(votedid.length() > 4){
                            Main.logAdmin("Voted",votedid);
                            UserId voted = UserId.fromString(votedid);
                            GameEvent current = gm.getCurrentEvent();
                            if(current instanceof GenericVote){
                                Main.logAdmin("Effectively vote");
                                ((GenericVote) current).setVote(UserId.fromDiscordId(event.getUser().getId()),voted);
                                Main.logAdmin(UserId.fromDiscordId(event.getUser().getId())+" -> "+voted);
                                ((GenericVote) current).validate(false, false);
                            }
                        }
                        event.deferEdit().queue();

                }
            } else {
                Main.logAdmin("Error : game "+gameid+" does not exist");
                if(type.contains("join")){
                    GameManager.setInviteExpired(event.getMessage());
                }
                GameManager.forceClean(Objects.requireNonNull(event.getGuild()), gameid);
                event.deferEdit().queue();
            }
        }
    }
    
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if(event.getModalId().contains("codemodal:")){
            String gmid = event.getModalId().replace("codemodal:", "");
            if(gmid.length()>1 && GameManager.fromId(gmid) != null){
                GameManager gm = GameManager.fromId(gmid);
                String key = gm.getJoinKey();
                ModalMapping input = event.getValue("codeinput");
                
                if(input != null && key.equals(input.getAsString())){
    
                    // player validated, adding it to the game
                    
                    Main.logAdmin("added player to private game "+gm.getId()+" with key "+input.getAsString());
                    gm.addPlayer(UserId.fromDiscordId(event.getUser().getId()), input.getAsString());
                }
                event.deferEdit().queue();
            }
        }
    }
}
