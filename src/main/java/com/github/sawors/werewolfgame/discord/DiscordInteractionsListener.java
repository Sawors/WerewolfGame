package com.github.sawors.werewolfgame.discord;

import com.github.sawors.werewolfgame.DatabaseManager;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.localization.TranslatableText;
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
        if(buttonid != null && event.isFromGuild() && guild != null){
            if(buttonid.contains("join:")){
                // button type = join
                String gameid = buttonid.replace("join:", "");
                GameManager gm = GameManager.fromId(gameid);
                if(gm != null){
                    if(!gm.getPlayerList().contains(UserId.fromDiscordId(event.getUser().getId()).toString())){
                        
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
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    GameManager.setInviteExpired(event.getMessage(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild())));
                    event.deferEdit().queue();
                }
            } else if(buttonid.contains("joinprivate:")){
                // button type = join private game
                String gameid = buttonid.replace("joinprivate:", "");
                GameManager gm = GameManager.fromId(gameid);
                if(gm != null){
                    if(!gm.getPlayerList().contains(UserId.fromDiscordId(event.getUser().getId()).toString())){
                        TextInput joincode = TextInput.create("codeinput",TranslatableText.get("forms.private-game-code.code-field-title", DatabaseManager.getGuildLanguage(Objects.requireNonNull(guild))), TextInputStyle.SHORT)
                                .setPlaceholder(TranslatableText.get("forms.private-game-code.text-placeholder", DatabaseManager.getGuildLanguage(Objects.requireNonNull(event.getGuild()))))
                                .setRequired(true)
                                .setRequiredRange(4,6)
                                .build();
                        event.replyModal(Modal.create("codemodal:"+gm.getId(),TranslatableText.get("forms.private-game-code.title", DatabaseManager.getGuildLanguage(Objects.requireNonNull(guild)))).addActionRow(joincode).build()).queue();
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
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    GameManager.setInviteExpired(event.getMessage(), DatabaseManager.getGuildLanguage(Objects.requireNonNull(guild)));
                    event.deferEdit().queue();
                }
                
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
