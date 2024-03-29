package io.github.sawors.werewolfgame.database;

import io.github.sawors.werewolfgame.DatabaseManager;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class UserId {
    String discriminant;
    static final int iduniquelength = 8;

    @Override
    public String toString() {
        return discriminant.toLowerCase(Locale.ROOT);
    }
    @Override
    public boolean equals(Object o){
        return o.getClass() == this.getClass() && Objects.equals(this.toString(), o.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
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
    
    public static @Nullable UserId fromString(String id) {
        if(id.length() == iduniquelength){
            for(char ch : id.toCharArray()){
                if(!Character.isLetterOrDigit(ch)){
                    return null;
                }
            }
            return new UserId(id);
        } else {
            return null;
        }
    }
    
    public static UserId fromDiscordId(String discordid){
        return DatabaseManager.getUserId(discordid);
    }
    
    public static UserId fromMinecraftUUID(UUID mcuuid){
        return DatabaseManager.getUserId(mcuuid);
    }
}
