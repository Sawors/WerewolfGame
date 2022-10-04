package io.github.sawors.werewolfgame.links.messaging;

import java.util.HashMap;
import java.util.Map;

public class ComplexMessage extends SimpleMessage{

    String thumbnailurl = "";
    String header = "";
    //   title, content
    Map<String, String> subcategories = new HashMap<>();
    String title = "";

    public ComplexMessage(String content, String header, String title, String thumbnail, Map<String,String> subcategories) {
        super(content);
        this.header = header;
        this.title = title;
        this.thumbnailurl = thumbnail;
        this.subcategories.putAll(subcategories);
    }



}
