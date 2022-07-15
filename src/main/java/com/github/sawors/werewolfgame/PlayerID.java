package com.github.sawors.werewolfgame;

import org.apache.commons.lang.RandomStringUtils;

import java.util.Locale;

public class PlayerID {
    String discriminant;

    @Override
    public String toString() {
        return "WW-"+discriminant.toUpperCase(Locale.ENGLISH);
    }

    public String getDiscriminant(){
        return discriminant;
    }

    /**
     *
     * @param id
     * the base String from which PlayerID will be constructed.<br><br>Four alphanumerical characters without
     * "WW-" before will be considered a discriminant (for instance if you input "04AB" the new PlayerID
     * will be WW-04AB).<br><br>If you input a well formatted PlayerID (WW-XXXX) it will be used as-is.<br><br>Every other input will
     * result in a random PlayerID.
     */
    public PlayerID(String id){
        Main.logAdmin(id.substring(0,3).toUpperCase(Locale.ENGLISH));
        if(id.substring(0,3).toUpperCase(Locale.ENGLISH).equals("WW-") && id.length() == 7){
            for(char ch : id.substring(3,7).toCharArray()){
                if(!Character.isLetterOrDigit(ch)){
                    this.discriminant = generateRandomDiscriminant();
                    return;
                }
            }

            this.discriminant = id.substring(3,7);
        } else if(id.length() == 4){
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

    public PlayerID(){
        this.discriminant = generateRandomDiscriminant();
    }

    private String generateRandomDiscriminant(){
        return RandomStringUtils.randomAlphanumeric(4).toUpperCase(Locale.ENGLISH);
    }
}
