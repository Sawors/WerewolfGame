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
import io.github.sawors.werewolfgame.game.roles.TextRole;
import io.github.sawors.werewolfgame.game.roles.WolfLike;
import io.github.sawors.werewolfgame.game.roles.base.Wolf;
import io.github.sawors.werewolfgame.localization.TranslatableText;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WolfKillEvent extends GenericVote implements RoleEvent {
    public WolfKillEvent(WerewolfExtension extension) {
        super(extension);
    }

    @Override
    public void start(GameManager manager) {
        // always add this
        this.votechannel = manager.getRoleChannel(getRole());
        
        manager.setGamePhase(GamePhase.NIGHT_WOLVES);
        Set<LinkedUser> votepool = manager.defaultVotePool();
        votepool.removeIf(us -> manager.getPlayerRoles().get(us.getId()) == null || manager.getPlayerRoles().get(us.getId()).getMainRole() instanceof WolfLike || !manager.getPlayerRoles().get(us.getId()).isAlive());
        this.votepool = votepool;
        Set<UserId> voters = manager.getRealPlayers();
        voters.removeIf(us -> manager.getPlayerRoles().get(us) == null ||!(manager.getPlayerRoles().get(us).getMainRole() instanceof WolfLike) || !manager.getPlayerRoles().get(us).isAlive());
        this.voters = voters;
        TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
        votemessage.setTitle(texts.get("votes.wolves.title"));
        votemessage.setDescription(texts.get("votes.wolves.description"));
        votemessage.setThumbnail(texts.get("roles.wolf.thumbnail"));

        manager.getMainTextChannel().sendMessage(((TextRole)getRole()).getRoundStartAnnouncement(manager.getLanguage())).queue();
        
        start(manager,votemessage);
    }

    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        Main.logAdmin("Winner",winner);
        manager.killUser(winner);
        manager.getMainTextChannel().sendMessage(((TextRole)getRole()).getRoundEndAnnouncement(manager.getLanguage())).queue();
        closeVote();
        manager.setGamePhase(GamePhase.NIGHT_POSTWOLVES);
        manager.nextEvent();
    }

    @Override
    public void onTie(List<UserId> tielist, Map<UserId, Integer> results) {
        Main.logAdmin("Accepting Tie, no victim",tielist);
        manager.getMainTextChannel().sendMessage(((TextRole)getRole()).getRoundEndAnnouncement(manager.getLanguage())).queue();
        closeVote();
        manager.getMainTextChannel().sendMessage(((TextRole)getRole()).getRoundStartAnnouncement(manager.getLanguage())).queue();
        manager.setGamePhase(GamePhase.NIGHT_POSTWOLVES);
        manager.nextEvent();
    }

    @Override
    public PlayerRole getRole() {
        return new Wolf(Main.getRootExtensionn());
    }
}
