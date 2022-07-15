package com.github.sawors.werewolfgame.database;

import com.github.sawors.werewolfgame.Main;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class UserId {
    String discriminant;
    int iduniquelength = 8;

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
        Main.logAdmin(id);
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
}
