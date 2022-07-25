package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import javax.annotation.Nullable;
import java.util.*;

public abstract class GenericVote extends GameEvent {

    protected Integer votetime = 30;
    protected Set<LinkedUser> votepool;
    protected EmbedBuilder votemessage = new EmbedBuilder();
    protected String messagebody;
    protected TextChannel votechannel;
    //       |            has voted for
    protected Map<UserId,               UserId> votemap = new HashMap<>();
    protected Set<UserId> voters;
    protected Set<UserId> votewinnertie = new HashSet<>();
    public GameManager manager;
    protected Message buttonmessage = null;

    public GenericVote(WerewolfExtension extension, Set<LinkedUser> votepool, Set<UserId> voters, @Nullable TextChannel votechannel, @Nullable String votemessagebody, @Nullable Integer votetime){
        super(extension);
        this.votepool = votepool;
        this.voters = voters;
        this.votechannel = votechannel;
        this.messagebody = votemessagebody != null ? votemessagebody : "";
        this.votetime = votetime != null && votetime > 0 ? votetime : 30;
    };
    
    public void setVotePool(Set<LinkedUser> votepool){
        this.votepool = votepool;
    }
    public Set<LinkedUser> getVotePool( ){
        return Set.copyOf(this.votepool);
    }
    public Set<UserId> getVoters(){return Set.copyOf(this.voters);}
    public boolean canVote(UserId user){
        return voters.contains(user);
    }
    public boolean canVote(LinkedUser user){
        return voters.contains(user.getId());
    }

    public void validate(boolean force, boolean wait){

        onValidationAttempt(force,true);
        Main.logAdmin(votemap.keySet());
        Main.logAdmin(voters);
        Main.logAdmin("contains",(votemap.keySet().containsAll(voters)));
        Main.logAdmin("wait",wait);
        Main.logAdmin("force",force);

        if((votemap.keySet().containsAll(voters) && !wait) || force){

            onValidationSuccess(force);

            Main.logAdmin("Votes Complete", votemap);
            Map<UserId, Integer> occurences = new HashMap<>();
            for(UserId voted : votemap.values()){
                occurences.put(voted, Collections.frequency(votemap.values(),voted));
            }
            ArrayList<UserId> sortedvotes = new ArrayList<>(occurences.keySet());
            //shuffle here to avoid predictable election behaviour
            Collections.shuffle(sortedvotes);
            sortedvotes.sort(Comparator.comparingInt(occurences::get));
            Collections.reverse(sortedvotes);

            if(sortedvotes.size() >= 2 && Objects.equals(occurences.get(sortedvotes.get(0)), occurences.get(sortedvotes.get(1)))){
                // Tie !
                for(UserId user : sortedvotes){
                    // referencing from the winner
                    if(Objects.equals(occurences.get(user), occurences.get(sortedvotes.get(0)))){
                        votewinnertie.add(user);
                    } else {
                        break;
                    }
                }
                onTie(List.copyOf(votewinnertie), occurences);
                return;
            }

            Main.logAdmin("Vote Scores", occurences);
            Main.logAdmin("Vote Ranks", sortedvotes);
            Main.logAdmin("Selected",sortedvotes.get(0));

            // always at the end of true validating methods
            onWin(sortedvotes.get(0), Map.copyOf(occurences));
        } else {
            onValidationFail();
        }
    };


    public void setVote(UserId voter, UserId voted){
        if(votemap.containsKey(voter)){
            onVoteChanged(voter,voted);
        } else {
            onVoteNew(voter,voted);
        }
        onVote(voter,voted);
        votemap.put(voter,voted);
        Main.logAdmin(votemap);
    }

    public Map<UserId, UserId> getVoteMap(){
        return Map.copyOf(votemap);
    }

    public void start(GameManager manager, EmbedBuilder embed){

        this.manager = manager;
        if(votechannel == null){
            votechannel = manager.getMainTextChannel();
        }
        
        List<ActionRow> votebuttons = new ArrayList<>();
        List<Button> tempbuttons = new ArrayList<>();
        for(LinkedUser user : votepool){
            tempbuttons.add(Button.primary("vote:"+manager.getId()+"#"+user.getId(), user.getName()));
            if(tempbuttons.size() >= 3){
                votebuttons.add(ActionRow.of(tempbuttons));
                tempbuttons.clear();
            }
        }
        if(tempbuttons.size()>0){
            //
            votebuttons.add(ActionRow.of(tempbuttons));
            tempbuttons.clear();
        }

        votechannel.sendMessageEmbeds(embed.build()).setActionRows(votebuttons).queue(msg -> this.buttonmessage = msg);
    }
    
    public void closeVote(){
        List<ActionRow> rows = new ArrayList<>();
        for(ActionRow row : buttonmessage.getActionRows()){
            List<Button> disabled = new ArrayList<>();
            for(Button button : row.getButtons()){
                disabled.add(button.asDisabled().withStyle(ButtonStyle.SECONDARY));
            }
            rows.add(ActionRow.of(disabled));
        }
        
        buttonmessage.editMessageEmbeds(new EmbedBuilder(votemessage).build()).setActionRows(rows).queue();
    }

    // events
    //  Validation events
    public void onValidationFail(){}
    public void onValidationSuccess(boolean forced){}
    public void onValidationAttempt(boolean force, boolean wait){}
    //  Vote Events
    public void onVote(UserId voter, UserId voted){}
    public void onVoteNew(UserId voter, UserId voted){}
    public void onVoteChanged(UserId voter, UserId voted){}
    // Vote result Events
    public void onWin(UserId winner, Map<UserId, Integer> results){}
    public void onTie(List<UserId> tied, Map<UserId, Integer> results){}
    // Vote time events
    public void onTimeOut(Integer basetime){}
    public void onTimeHalf(Integer basetime, Integer elapsedtime){};
}
