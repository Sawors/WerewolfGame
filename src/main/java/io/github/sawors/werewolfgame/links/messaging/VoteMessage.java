package io.github.sawors.werewolfgame.links.messaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteMessage extends ComplexMessage{

    //  target, title
    Map<String, String> votepool = new HashMap<>();

    public VoteMessage(String content, String header, String title, String thumbnail, Map<String, String> subcategories, Map<String,String> votepool) {
        super(content, header, title, thumbnail, subcategories);
        this.votepool = Map.copyOf(votepool);
    }


    public void addVoteOption(String title, String target){
        votepool.put(title,target);
    }

    public void removeVoteOption(String target){
        votepool.remove(target);
    }

    public List<String> getVotepoolTargets(){
        return List.copyOf(votepool.keySet());
    }

    public String getTitle(String target){
        return votepool.get(target);
    }


}
