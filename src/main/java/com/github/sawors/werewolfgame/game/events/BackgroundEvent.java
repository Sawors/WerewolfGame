package com.github.sawors.werewolfgame.game.events;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.GamePhase;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

import java.util.List;

public abstract class BackgroundEvent {
    
    public WerewolfExtension extension;
    public BackgroundEvent(WerewolfExtension extension){
        this.extension = extension;
    }
    
    public abstract void initialize(GameManager manager);
    
    public void onPlayerKilled(UserId victim){}
    public void onGameStart(){}
    public void onVoteClose(UserId voted, List<UserId> voters){}
    public void onPlayerVote(UserId voter, UserId voted){}
    public void onGameEventChange(GameEvent last, GameEvent next){}
    public void onGamePhaseChange(GamePhase last, GamePhase next){}
    public void onMessageSent(GenericMessageEvent event){}
    public void onReactionAdded(GenericMessageEvent event){}
    public void onReactionRemoved(GenericMessageEvent event){}
    
}
