package io.github.sawors.werewolfgame.links;

import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.links.messaging.DefaultChannelType;
import io.github.sawors.werewolfgame.links.messaging.SimpleMessage;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;

public abstract class WerewolfLink {
    // I/O
    // Discord,
    // MC,
    // WEBSITE

    protected String linkid;
    private Map<String, String> contentmap;
    private GameManager manager;

    public WerewolfLink(GameManager manager){
        this.linkid = RandomStringUtils.randomAlphabetic(6);
        this.manager = manager;
    }

    public String getId(){
        return this.linkid;
    }
    /**
     * for sending messages in default channels :
     *  Admin
     *  Game
     *  Lobby
     * <p>
     * DO NOT SEND MESSAGES IN ROLE CHANNELS USING THIS, instead use WerewolfLink.sendRoleMessage(String channel, SimpleMessage message)
     */
    public void sendSystemMessage(DefaultChannelType channel, SimpleMessage message){

    }

    public void sendRoleMessage(String channel, SimpleMessage message){

    }

    public void setVote(String voteid, UserId voter, String voted){

    }




    // output events

}
