package com.github.sawors.werewolfgame.game.phases.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GenericVote;
import com.github.sawors.werewolfgame.game.phases.PhaseType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MayorVote extends GenericVote {

    
    public MayorVote(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters ,TextChannel channel) {
        super(manager, votepool, voters, channel, "Vote for the best Mayor !");
        Main.logAdmin("Voters",voters);
        Main.logAdmin("Votepool",votepool);
        this.type = PhaseType.DAY;
    }

    @Override
    public void validate(boolean force, boolean wait) {
        if((votemap.keySet().containsAll(voters) && !wait) || force){
            Main.logAdmin("Votes", votemap);
        }
    }

    @Override
    public void start() {
        votemessage.setTitle("Electing the Mayor");
        votemessage.setDescription(messagebody);
        votemessage.addField("Supplementary Role","This role does not replace your original role",false);
        votemessage.addField("Role","The mayor will decide who should die if a tie happens when the Village decides which player to eliminate. \nWhen the Mayor dies a new Mayor is designated by the old one just before passing away",false);

        List<ActionRow> votebuttons = new ArrayList<>();
        List<Button> tempbuttons = new ArrayList<>();
        for(LinkedUser user : votepool){
            if(tempbuttons.size() >= 3){
                votebuttons.add(ActionRow.of(tempbuttons));
                tempbuttons.clear();
            } else {
                tempbuttons.add(Button.primary("vote:"+gm.getId()+"#"+user.getId(), user.getName()));
            }
        }


        votechannel.sendMessageEmbeds(votemessage.build()).setActionRows(votebuttons).queue();
    }
}
