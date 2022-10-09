package io.github.sawors.werewolfgame.links.messaging;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public String getThumbnailUrl() {
        return thumbnailurl;
    }

    public void setThumbnailUrl(String thumbnailurl) {
        this.thumbnailurl = thumbnailurl;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addCategory(String title, String content){
        subcategories.put(title,content);
    }

    public void removeCategory(String title){
        subcategories.remove(title);
    }

    public @Nullable String getContent(String title){
        return subcategories.get(title);
    }

    public Set<String> getSubCategories(){
        return subcategories.keySet();
    }
}
