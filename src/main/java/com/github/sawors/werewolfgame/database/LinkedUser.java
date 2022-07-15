package com.github.sawors.werewolfgame.database;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LinkedUser {
    UserId id;
    UUID minecraftid;
    String discordid;
    String name;
    HashSet<UserPreference> preferences;
    HashSet<UserTag> tags;

    public LinkedUser(UUID mcid, String discordid, String name, Set<UserPreference> preferences, Set<UserTag> tags){
        id = new UserId();
        this.minecraftid = mcid;
        this.discordid = discordid;
        this.name = name;
        this.preferences = new HashSet<>(preferences);
        this.tags = new HashSet<>(tags);
    }
    
    public LinkedUser(){
        id = new UserId();
        this.minecraftid = null;
        this.discordid = null;
        this.name = null;
        this.preferences = null;
        this.tags = null;
    }
    
    public static LinkedUser getFromId(UserId id){
        return new LinkedUser();
    }
    
    public UUID getMinecraftid() {
        return minecraftid;
    }
    
    public void setMinecraftid(UUID minecraftid) {
        this.minecraftid = minecraftid;
    }
    
    public String getDiscordid() {
        return discordid;
    }
    
    public void setDiscordid(String discordid) {
        this.discordid = discordid;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Set<UserPreference> getPreferences() {
        return Set.copyOf(preferences);
    }
    
    public void addPreference(UserPreference preference) {
        preferences.add(preference);
    }
    
    public void removePreference(UserPreference preference){
        preferences.remove(preference);
    }
    
    public Set<UserTag> getTags() {
        return Set.copyOf(tags);
    }
    
    public void addTag(UserTag tag) {
        tags.add(tag);
    }
    
    public void removetag(UserTag tag){
        tags.remove(tag);
    }
}
