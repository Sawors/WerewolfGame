package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.WerewolfPlayer;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.base.Mayor;
import io.github.sawors.werewolfgame.localization.TranslatableText;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MayorVoteEvent extends GenericVote implements RoleEvent {

    
    public MayorVoteEvent(WerewolfExtension extension) {
        // TODO : user-defined vote time
        super(extension);
        Main.logAdmin("Voters",voters);
        Main.logAdmin("Votepool",votepool);
    }

    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        Main.logAdmin("Winner",winner);
        closeVote();
        votechannel.sendMessage(new TranslatableText(getExtension().getTranslator(), manager.getLanguage()).get("votes.mayor.end")).queue();
        String name = DatabaseManager.getName(winner);
        WerewolfPlayer player = manager.getPlayerRoles().get(winner);
        if(player != null && player.isAlive()){
            player.addRole(new Mayor(getExtension()));
            manager.setMayor(winner);
        }
        votechannel.sendMessage(":tada: **"+name+"** :tada:").queueAfter(3, TimeUnit.SECONDS, m -> manager.nextEvent());
    }

    @Override
    public void onTie(List<UserId> tielist, Map<UserId, Integer> results) {
        Main.logAdmin("Ignoring Tie",tielist);
        onWin(tielist.get(0), results);
    }
    
    @Override
    public void start(GameManager manager) {
        // always add this
        this.votechannel = manager.getRoleChannel(getRole());
        
        this.votepool = manager.defaultVotePool();
        this.voters = manager.getRealPlayers();
        

        TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
        votemessage.setTitle(texts.get("votes.mayor.title"));
        votemessage.setDescription(texts.get("votes.mayor.description"));
        votemessage.addField(texts.get("roles.supplementary.title"),texts.get("roles.supplementary.description"),false);
        votemessage.addField(texts.get("roles.generic.role-description"),texts.get("roles.mayor.role-description"),false);
        votemessage.setThumbnail(texts.get("roles.mayor.thumbnail"));
        start(manager,votemessage,true);
    }
    
    @Override
    public PlayerRole getRole() {
        return new Mayor(this.extension);
    }
}
