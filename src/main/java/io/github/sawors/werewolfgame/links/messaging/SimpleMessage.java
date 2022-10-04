package io.github.sawors.werewolfgame.links.messaging;

import java.util.HashMap;
import java.util.Map;

public class SimpleMessage {
    HashMap<TextStyleEffect,String> contentmap = new HashMap<>();

    // to do cross-platform formating we will use by default Discord (Markdown) text formatting :
    // *italic*
    // **bold**
    // ***bold-italic***
    public SimpleMessage(String content){
        // no parsing for the moment, only the message
        contentmap.put(TextStyleEffect.NORMAL,content);
        // TODO :
        //  implement style parsing + * stripping



        /*// skip the parsing ?
        char[] charray = content.toCharArray();


        for(int i = 0; i<charray.length; i++){
            char ch = charray[i];
            // we must detect the type and innit a build sequence
            if (ch == '*') {
                // build sequence started, detecting the type

            }
        }*/
    }

    public Map<TextStyleEffect,String> getContent(){
        return Map.copyOf(contentmap);
    }

    public String getContentRaw(){
        StringBuilder str = new StringBuilder();
        for(String cont : contentmap.values()){
            str.append(cont);
        }

        // strip all the "*" until I make a clean parsing method
        String otp = str.toString().replaceAll("\\*","");;


        return otp;
    }

}
