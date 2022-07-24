package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class GenericVote extends GameEvent {

    protected Set<LinkedUser> votepool;
    protected EmbedBuilder votemessage = new EmbedBuilder();
    protected String messagebody;
    protected TextChannel votechannel;
    //       |            has voted for
    protected Map<UserId,               UserId> votemap = new HashMap<>();
    protected Set<UserId> voters;

    public GenericVote(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters, TextChannel votechannel){
        super(manager);
        this.votepool = votepool;
        this.votechannel = votechannel;
        this.voters = voters;
    };
    public GenericVote(GameManager manager, Set<LinkedUser> votepool, Set<UserId> voters, TextChannel votechannel, String votemessagebody){
        super(manager);
        this.votepool = votepool;
        this.messagebody = votemessagebody;
        this.votechannel = votechannel;
        this.voters = voters;
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

    public abstract void validate(boolean force, boolean wait);


    public void setVote(UserId voter, UserId voted){
        votemap.put(voter,voted);
        Main.logAdmin(votemap);
    }

    public Map<UserId, UserId> getVoteMap(){
        return Map.copyOf(votemap);
    }
}
