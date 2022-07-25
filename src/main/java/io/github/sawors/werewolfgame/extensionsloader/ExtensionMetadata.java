package io.github.sawors.werewolfgame.extensionsloader;

public class ExtensionMetadata {
    String name;
    String version;
    String author;
    String source;
    String description;

    public ExtensionMetadata(String name, String version, String author, String source, String description){
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
