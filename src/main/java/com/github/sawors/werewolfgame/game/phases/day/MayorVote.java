package com.github.sawors.werewolfgame.game.phases.day;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.phases.GenericVote;
import com.github.sawors.werewolfgame.game.phases.PhaseType;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;

public class MayorVote extends GenericVote {
    
    public MayorVote(GameManager manager, Set<LinkedUser> votepool, TextChannel channel) {
        super(manager, votepool, channel, "yeah !!");
        this.type = PhaseType.DAY;
    }

    @Override
    public void validate() {

    }
}
