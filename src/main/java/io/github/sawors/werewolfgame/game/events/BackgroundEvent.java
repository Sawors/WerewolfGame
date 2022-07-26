package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

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
    public void onMessageSent(MessageReceivedEvent event){}
    public void onReactionAdded(MessageReactionAddEvent event){}
    public void onReactionRemoved(MessageReactionRemoveEvent event){}
    
}
