package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

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
        Main.logAdmin("p0");
        // always add this
        this.votechannel = manager.getMainTextChannel();
        
        manager.setGamePhase(GamePhase.VILLAGE_VOTE);
        Set<LinkedUser> votepool = manager.defaultVotePool();
        votepool.removeIf(us -> manager.getPlayerRoles().get(us.getId()) == null || !manager.getPlayerRoles().get(us.getId()).isAlive());
        this.votepool = votepool;
        Set<UserId> voters = manager.getRealPlayers();
        voters.removeIf(us -> manager.getPlayerRoles().get(us) == null || !manager.getPlayerRoles().get(us).isAlive());
        this.voters = voters;
        // TODO vote
        Main.logAdmin("p1");
        TranslatableText texts = new TranslatableText(Main.getTranslator(), manager.getLanguage());
        votemessage.setTitle(texts.get("votes.village.title"));
        votemessage.setDescription(texts.get("votes.village.description"));
        votemessage.setThumbnail(texts.get("roles.villager.thumbnail"));
        Main.logAdmin("Village vote",votemessage);
        Main.logAdmin("p2");
        start(manager,votemessage);
        Main.logAdmin("p3");
    }
    
    /*
    * // always add this
        this.votechannel = getRole() != null && getRole() instanceof TextRole trole && manager.getRoleChannel(trole) != null ? manager.getRoleChannel(trole) : manager.getMainTextChannel();
        
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
        
        manager.getMainTextChannel().sendMessage(((TextRole)getRole()).getAnnouncementMessage(manager.getLanguage())).queue();
        
        start(manager,votemessage);*/
    
    @Override
    public void onWin(UserId winner, Map<UserId, Integer> results) {
        /*
        Main.logAdmin("Winner",winner);
        closeVote();
        votechannel.sendMessage(new TranslatableText(getExtension().getTranslator(), manager.getLanguage()).get("votes.mayor.end")).queue();
        User w = manager.getDiscordUser(winner);
        String name = w != null ? w.getName() : winner.toString();
        WerewolfPlayer player = manager.getPlayerRoles().get(winner);
        if(player != null && player.isAlive()){
            player.addRole(new Mayor(getExtension()));
            manager.setMayor(winner);
        }
        votechannel.sendMessage(":tada: **"+name+"** :tada:").queueAfter(3, TimeUnit.SECONDS, m -> manager.nextEvent());
        */
        Main.logAdmin("Winner",winner);
        closeVote();
        User w = manager.getDiscordUser(winner);
        String name = w != null ? w.getName() : winner.toString();
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
    
    @Override
    public void start(GameManager manager, EmbedBuilder embed) {
        super.start(manager, embed);
    }
}
