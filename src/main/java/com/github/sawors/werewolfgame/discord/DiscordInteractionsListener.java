package com.github.sawors.werewolfgame.discord;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscordInteractionsListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonid = event.getInteraction().getButton().getId();
        if(buttonid != null){
            if(buttonid.contains("join:")){
                // button type = join
                String gameid = buttonid.replace("join:", "");
                GameManager gm = GameManager.fromId(gameid);
                if(gm != null){
                    if(!gm.getPlayerList().contains(UserId.fromDiscordId(event.getUser().getId()))){
                        gm.addplayer(UserId.fromDiscordId(event.getUser().getId()));
                    }
                    event.deferEdit().queue();
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    List<ActionRow> rows = event.getMessage().getActionRows();
                    List<Button> disabled = new ArrayList<>();
                    for(ActionRow act : rows){

                        act.getButtons().forEach(bt -> disabled.add(bt.asDisabled().withLabel("Game Finished").withStyle(ButtonStyle.SECONDARY)));
                    }
                    event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds()).setActionRow(disabled).queue();
                    event.deferEdit().queue();
                }
            } else if(buttonid.contains("joinprivate:")){
                // button type = join private game
                String gameid = buttonid.replace("joinprivate:", "");
                GameManager gm = GameManager.fromId(gameid);
                if(gm != null){
                    if(!gm.getPlayerList().contains(UserId.fromDiscordId(event.getUser().getId()))){
                        TextInput joincode = TextInput.create("codeinput","Please input your 5 number code", TextInputStyle.SHORT)
                                .setPlaceholder("very secret code here")
                                .setRequired(true)
                                .setRequiredRange(4,6)
                                .build();
                        event.replyModal(Modal.create("codemodal:"+gm.getId(),"Private Game Code").addActionRow(joincode).build()).queue();
                        Main.logAdmin("joined private game");
                    } else {
                        event.deferEdit().queue();
                    }
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    List<ActionRow> rows = event.getMessage().getActionRows();
                    List<Button> disabled = new ArrayList<>();
                    for(ActionRow act : rows){
            
                        act.getButtons().forEach(bt -> disabled.add(bt.asDisabled().withLabel("Game Finished")));
                    }
                    event.getMessage().editMessageEmbeds(event.getMessage().getEmbeds()).setActionRow(disabled).queue();
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
                    Main.logAdmin("added player to private game "+gm.getId()+" with key "+input.getAsString());
                    gm.addPlayer(UserId.fromDiscordId(event.getUser().getId()), input.getAsString());
                }
                event.deferEdit().queue();
            }
        }
    }
}
