package com.github.sawors.werewolfgame.game.phases;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Set;

public abstract class GenericVote extends GameEvent {
    
    private Set<LinkedUser> votepool;
    private MessageBuilder votemessage = new MessageBuilder();
    private String messagebody;
    private TextChannel votechannel;
    
    public GenericVote(GameManager manager, Set<LinkedUser> votepool, TextChannel votechannel){
        super(manager);
        this.votepool = votepool;
        this.votechannel = votechannel;
    };
    public GenericVote(GameManager manager, Set<LinkedUser> votepool,TextChannel votechannel, String votemessagebody){
        super(manager);
        this.votepool = votepool;
        this.messagebody = votemessagebody;
        this.votechannel = votechannel;
    };
    
    public void setVotePool(Set<LinkedUser> votepool){
        this.votepool = votepool;
    }

    @Override
    public void start() {
        votemessage.append("");

        votechannel.sendMessage(votemessage.build()).queue();
    }
}
