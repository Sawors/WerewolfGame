package com.github.sawors.werewolfgame.database;

import com.github.sawors.werewolfgame.DatabaseManager;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;
import java.util.UUID;

public class UserId {
    String discriminant;
    static final int iduniquelength = 8;

    @Override
    public String toString() {
        return discriminant.toLowerCase(Locale.ENGLISH);
    }

    public String getDiscriminant(){
        return discriminant;
    }

    /**
     *
     * @param id
     * the base String from which PlayerID will be constructed.<br><br>Four alphanumerical characters without
     * "WW-" before will be considered a discriminant (for instance if you input "04AB3D" the new PlayerID
     * will be WW-04AB3D).<br><br>If you input a well formatted PlayerID (WW-XXXXXX) it will be used as-is.<br><br>Every other input will
     * result in a random PlayerID.
     */
    public UserId(String id){
        if(id.length() == iduniquelength){
            for(char ch : id.toCharArray()){
                if(!Character.isLetterOrDigit(ch)){
                    this.discriminant = generateRandomDiscriminant();
                    return;
                }
            }
            this.discriminant = id;
        } else {
            this.discriminant = generateRandomDiscriminant();
        }
    }

    public UserId(){
        this.discriminant = generateRandomDiscriminant();
    }

    private String generateRandomDiscriminant(){
        return RandomStringUtils.randomAlphanumeric(iduniquelength).toLowerCase(Locale.ENGLISH);
    }
    
    public static UserId fromString(String id) throws IllegalArgumentException{
        if(id.length() == iduniquelength){
            for(char ch : id.toCharArray()){
                if(!Character.isLetterOrDigit(ch)){
                    throw new IllegalArgumentException(id+" is not a correctly formatted PlayerId (only alphanumeric characters are allowed)");
                }
            }
            return new UserId(id);
        } else {
            throw new IllegalArgumentException(id+" is not a correctly formatted PlayerId (only "+iduniquelength+" characters allowed, got "+id.length()+")");
        }
    }
    
    public static UserId fromDiscordId(String discordid){
        return DatabaseManager.getUserId(discordid);
    }
    
    public static UserId fromMinecraftUUID(UUID mcuuid){
        return DatabaseManager.getUserId(mcuuid);
    }
}
