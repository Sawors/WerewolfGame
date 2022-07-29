package io.github.sawors.werewolfgame.game.events.night;

import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.events.GenericVote;
import io.github.sawors.werewolfgame.game.events.RoleEvent;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.WolfLike;
import io.github.sawors.werewolfgame.game.roles.base.Wolf;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WolfKillEvent extends GenericVote implements RoleEvent {
    public WolfKillEvent(WerewolfExtension extension, Set<UserId> voters, @Nullable TextChannel votechannel) {
        super(extension, voters, votechannel);
    }

    @Override
    public void start(GameManager manager) {
        Set<LinkedUser> votepool = manager.defaultVotePool();
        votepool.removeIf(us -> manager.getPlayerRoles().get(us.getId()) == null || manager.getPlayerRoles().get(us.getId()).getMainRole() instanceof WolfLike || !manager.getPlayerRoles().get(us.getId()).isAlive());
        this.votepool = votepool;
        TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
        votemessage.setTitle(texts.get("votes.wolves.title"));
        votemessage.setDescription(texts.get("votes.wolves.description"));
        votemessage.setThumbnail(texts.get("roles.wolf.thumbnail"));


        start(manager,votemessage);
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
        Collections.shuffle(tielist);
        onWin(tielist.get(0), results);
    }

    @Override
    public GamePhase getPhase() {
        return GamePhase.NIGHT_WOLVES;
    }

    @Override
    public PlayerRole getRole() {
        return new Wolf(Main.getRootExtensionn());
    }
}
