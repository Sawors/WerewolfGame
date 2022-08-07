package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.localization.TranslatableText;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class VillageVoteEvent extends GenericVote {
    public VillageVoteEvent(WerewolfExtension extension) {
        //TODO : allow user to change vote time during game configuration
        super(extension);
        this.votetime = 5*60;
    }

    @Override
    public void start(GameManager manager) {
        
        if(manager.checkForWinCondition()){
            return;
        }
        
        // always add this
        this.votechannel = manager.getMainTextChannel();
        
        manager.setGamePhase(GamePhase.VILLAGE_VOTE);
        Set<LinkedUser> votepool = manager.defaultVotePool();
        votepool.removeIf(us -> manager.getPlayerRoles().get(us.getId()) == null || !manager.getPlayerRoles().get(us.getId()).isAlive());
        this.votepool = votepool;
        Set<UserId> voters = manager.getRealPlayers();
        voters.removeIf(us -> manager.getPlayerRoles().get(us) == null || !manager.getPlayerRoles().get(us).isAlive());
        voters.add(UserId.fromString("sawors01"));
        this.voters = voters;
        // TODO vote
        TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
        votemessage.setTitle(texts.get("votes.village.title"));
        votemessage.setDescription(texts.get("votes.village.description"));
        votemessage.setThumbnail(texts.get("roles.villager.thumbnail"));
        start(manager,votemessage,true);
    }
    
    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        Main.logAdmin("Winner",winner);
        closeVote();
        String name = DatabaseManager.getName(winner);
        votechannel.sendMessage(new TranslatableText(getExtension().getTranslator(), manager.getLanguage()).get("votes.village.end").replaceAll("%user%", name)).queue();
        manager.setGamePhase(GamePhase.NIGHT_POSTWOLVES);
        manager.killUser(winner);
        manager.nextEvent();
    }
    
    @Override
    public void onTie(List<UserId> tielist, Map<UserId, Integer> results) {
        Main.logAdmin("Ignoring Tie",tielist);
        onWin(tielist.get(0), results);
    }
    
    @Override
    public PlayerRole getRole() {
        return null;
    }
}
