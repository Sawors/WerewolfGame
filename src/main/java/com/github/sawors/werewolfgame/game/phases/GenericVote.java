package com.github.sawors.werewolfgame.game.phases;

import com.github.sawors.werewolfgame.LinkedUser;
import com.github.sawors.werewolfgame.game.GameManager;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class GenericVote extends GameEvent {

    protected Set<LinkedUser> votepool;
    protected MessageBuilder votemessage = new MessageBuilder();
    protected String messagebody;
    protected TextChannel votechannel;

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
        votemessage.append(messagebody);
        List<Button> votebuttons = new ArrayList<>();
        for(LinkedUser user : votepool){
            votebuttons.add(Button.primary("vote:mayor#"+user.getId(), user.getName()));
        }

        votemessage.setActionRows(ActionRow.of(votebuttons));

        votechannel.sendMessage(votemessage.build()).queue();
    }

    public abstract void validate();
}
