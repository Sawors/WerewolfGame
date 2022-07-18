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
                    gm.addplayer(UserId.fromDiscordId(event.getUser().getId()));
                    event.deferEdit().queue();
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    List<ActionRow> rows = event.getMessage().getActionRows();
                    List<Button> disabled = new ArrayList<>();
                    for(ActionRow act : rows){

                        act.getButtons().forEach(bt -> disabled.add(bt.asDisabled().withLabel("Game Finished")));
                    }
                    event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(disabled).queue();
                    event.deferEdit().queue();
                }
            } else if(buttonid.contains("joinprivate:")){
                // button type = join private game
                String gameid = buttonid.replace("joinprivate:", "");
                GameManager gm = GameManager.fromId(gameid);
                if(gm != null){
                    TextInput joincode = TextInput.create("codeinput","Please input the private code of this game", TextInputStyle.SHORT)
                            .setPlaceholder("the unique 5 number code for this game")
                            .setRequired(true)
                            .setRequiredRange(4,6)
                            .build();
                    event.replyModal(Modal.create("codemodal:"+gm.getId(),"Private Game Code").addActionRow(joincode).build()).queue();
                    Main.logAdmin("joined private game");
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    List<ActionRow> rows = event.getMessage().getActionRows();
                    List<Button> disabled = new ArrayList<>();
                    for(ActionRow act : rows){
            
                        act.getButtons().forEach(bt -> disabled.add(bt.asDisabled().withLabel("Game Finished")));
                    }
                    event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(disabled).queue();
                    event.deferEdit().queue();
                }
                
            }
        }
    }
    
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        Main.logAdmin("l1");
        if(event.getModalId().contains("codemodal:")){
            Main.logAdmin("l2");
            String gmid = event.getModalId().replace("codemodal:", "");
            if(gmid.length()>1 && GameManager.fromId(gmid) != null){
                Main.logAdmin("l3");
                GameManager gm = GameManager.fromId(gmid);
                String key = gm.getJoinKey();
                ModalMapping input = event.getValue("codeinput");
                
                Main.logAdmin(input);
                
                if(input != null && key.equals(input.getAsString())){
                    Main.logAdmin("l4");
                    Main.logAdmin("added player to private game "+gm.getId()+" with key "+input.getAsString());
                    gm.addPlayer(UserId.fromDiscordId(event.getUser().getId()), input.getAsString());
                }
                event.deferEdit().queue();
            }
        }
    }
}
