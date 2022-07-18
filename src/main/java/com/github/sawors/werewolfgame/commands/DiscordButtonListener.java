package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DiscordButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonid = event.getInteraction().getButton().getId();
        if(buttonid != null){
            if(buttonid.contains("join:")){
                // button type = join
                String gameid = buttonid.replace("join:", "");
                GameManager gm = GameManager.fromId(gameid);
                if(gm != null){
                    gm.addPlayer(UserId.fromDiscordId(event.getUser().getId()));
                } else {
                    Main.logAdmin("Error : game "+gameid+" does not exist");
                    List<ActionRow> rows = event.getMessage().getActionRows();
                    List<Button> disabled = new ArrayList<>();
                    for(ActionRow act : rows){

                        act.getButtons().forEach(bt -> disabled.add(bt.asDisabled().withLabel("Game Finished")));
                    }
                    event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(disabled).queue();
                }
                event.deferEdit().queue();
            }
        }
    }
}
