package io.github.sawors.werewolfgame.links;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;

public abstract class WerewolfLink {
    // I/O
    // Discord,
    // MC,
    // WEBSITE

    protected String linkid;
    private Map<String, String> contentmap;

    public WerewolfLink(){
        this.linkid = RandomStringUtils.randomAlphabetic(6);
    }

    public String getId(){
        return this.linkid;
    }



    // output events

}
