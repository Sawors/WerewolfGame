package com.github.sawors.werewolfgame.extensions;

import java.net.URL;

public class ExtensionMetadata {
    String name;
    String version;
    String author;
    URL source;
    String description;

    ExtensionMetadata(String name, String version, String author, URL source, String description){
        this.name = name;
        this.version = version;
        this.author = author;
        this.source = source;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public URL getSource() {
        return source;
    }

    public void setSource(URL source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
