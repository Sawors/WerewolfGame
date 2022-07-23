package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.database.UserPreference;
import com.github.sawors.werewolfgame.database.UserTag;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LinkedUser {
    private UserId id;
    private UUID minecraftid;
    private String discordid;
    private String name;
    private HashSet<UserPreference> preferences;
    private HashSet<UserTag> tags;

    public LinkedUser(UserId id, String name, UUID mcid, String discordid, @Nullable Set<UserPreference> preferences, @Nullable Set<UserTag> tags){
        this.id = id;
        this.minecraftid = mcid;
        this.discordid = discordid;
        this.name = name;
        this.preferences = preferences == null ? new HashSet<>() : new HashSet<>(preferences);
        this.tags = tags == null ? new HashSet<>() : new HashSet<>(tags);
    }
    
    public LinkedUser(){
        this.id = new UserId();
        this.minecraftid = null;
        this.discordid = "";
        this.name = "";
        this.preferences = new HashSet<>();
        this.tags = new HashSet<>();
    }
    
    public static @Nullable LinkedUser fromId(UserId id){
        
        if(Main.useUserCache()){
            return Main.getCachedUser(id);
        }
        LinkedUser retrieveduser = DatabaseManager.retrieveUserData(id);
        if(retrieveduser == null){
            retrieveduser = new LinkedUser(new UserId(), RandomStringUtils.randomAlphabetic((int)(Math.random()*8)),UUID.randomUUID(),"",null,null);
        }
        return retrieveduser;
    }
    
    public UserId getId(){
        return id;
    }
    
    public UUID getMinecraftId() {
        return minecraftid;
    }
    
    public void setMinecraftid(UUID minecraftid) {
        this.minecraftid = minecraftid;
    }
    
    public String getDiscordId() {
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
        return preferences == null ? null : Set.copyOf(preferences);
    }
    
    public void addPreference(UserPreference preference) {
        preferences.add(preference);
    }
    
    public void removePreference(UserPreference preference){
        preferences.remove(preference);
    }
    
    public Set<UserTag> getTags() {
        return tags == null ? null : Set.copyOf(tags);
    }
    
    public void addTag(UserTag tag) {
        tags.add(tag);
    }
    
    public void removetag(UserTag tag){
        tags.remove(tag);
    }
}
