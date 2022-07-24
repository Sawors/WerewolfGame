package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

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

    public GenericVote(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters,@Nullable TextChannel votechannel, @Nullable String votemessagebody, @Nullable Integer votetime){
        super(manager);
        this.votepool = votepool;
        this.voters = voters;
        this.votechannel = votechannel != null ? votechannel : gm.getMainTextChannel();
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

        if((votemap.keySet().containsAll(voters) && !wait) || force){

            onValidationSuccess(force);

            //Main.logAdmin("Votes Complete", votemap);
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
                onTie(Set.copyOf(votewinnertie));
                return;
            }

//            Main.logAdmin("Vote Scores", occurences);
//            Main.logAdmin("Vote Ranks", sortedvotes);
//            Main.logAdmin("Selected",sortedvotes.get(0));

            // always at the end of true validating methods
            onWin(sortedvotes.get(0));
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


    // events

    public void onWin(UserId winner){}
    public void onTie(Set<UserId> tied){}
    public void onValidationFail(){}
    public void onValidationSuccess(boolean forced){}
    public void onValidationAttempt(boolean force, boolean wait){}
    public void onVote(UserId voter, UserId voted){}
    public void onVoteNew(UserId voter, UserId voted){}
    public void onVoteChanged(UserId voter, UserId voted){}
    public void onTimeOut(Integer basetime){}
}
