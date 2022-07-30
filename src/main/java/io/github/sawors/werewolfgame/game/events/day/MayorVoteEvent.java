package io.github.sawors.werewolfgame.game.events.day;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GenericVote;
import io.github.sawors.werewolfgame.game.events.RoleEvent;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.base.Mayor;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Map;

public class MayorVoteEvent extends GenericVote implements RoleEvent {

    
    public MayorVoteEvent(WerewolfExtension extension, TextChannel channel) {
        // TODO : user-defined vote time
        super(extension, channel);
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
        this.votepool = manager.defaultVotePool();
        this.voters = manager.getRealPlayers();

        TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
        votemessage.setTitle(texts.get("votes.mayor.title"));
        votemessage.setDescription(texts.get("votes.mayor.description"));
        votemessage.addField(texts.get("roles.supplementary.title"),texts.get("roles.supplementary.description"),false);
        votemessage.addField(texts.get("roles.generic.role-description"),texts.get("roles.mayor.role-description"),false);
        votemessage.setThumbnail(texts.get("roles.mayor.thumbnail"));
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
