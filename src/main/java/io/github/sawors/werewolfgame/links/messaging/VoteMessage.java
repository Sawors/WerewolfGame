package io.github.sawors.werewolfgame.links.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteMessage extends ComplexMessage{

    //  target, title
    Map<String, String> votepool = new HashMap<>();

    public VoteMessage(String content) {
        super(content);
    }

    public void addVoteOption(String title, String target){
        votepool.put(title,target);
    }

    public void removeVoteOption(String target){
        votepool.remove(target);
    }

    public List<String> getVotePool(){
        return List.copyOf(votepool.keySet());
    }


}
