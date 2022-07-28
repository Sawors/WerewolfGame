package io.github.sawors.werewolfgame.game.events.day;

import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GenericVote;
import io.github.sawors.werewolfgame.game.events.RoleEvent;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.base.Mayor;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MayorVoteEvent extends GenericVote implements RoleEvent {

    
    public MayorVoteEvent(WerewolfExtension extension, Set<LinkedUser> votepool, Set<UserId> voters , TextChannel channel) {
        // TODO : user-defined vote time
        super(extension, votepool, voters, channel, "Vote for the best Mayor !",30);
        Main.logAdmin("Voters",voters);
        Main.logAdmin("Votepool",votepool);
    }

    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        Main.logAdmin("Winner",winner);
        closeVote();
        manager.nextEvent();
    }

    @Override
    public void onTie(List<UserId> tielist, Map<UserId, Integer> results) {
        Main.logAdmin("Ignoring Tie",tielist);
        onWin(tielist.get(0), results);
    }

    @Override
    public void onValidationFail() {

    }

    @Override
    public void onValidationSuccess(boolean forced) {

    }

    @Override
    public void start(GameManager manager) {
        votemessage.setTitle("📩 Electing the Mayor");
        votemessage.setDescription(messagebody);
        votemessage.addField("Supplementary Role","This role does not replace your original role",false);
        votemessage.addField("Role","The mayor will decide who should die if a tie happens when the Village decides which player to eliminate. \nWhen the Mayor dies a new Mayor is designated by the old one just before passing away",false);

        start(manager,votemessage);
    }
    
    @Override
    public GamePhase getPhase() {
        return GamePhase.FIRST_DAY;
    }
    
    @Override
    public PlayerRole getRole() {
        return new Mayor(this.extension);
    }
}
