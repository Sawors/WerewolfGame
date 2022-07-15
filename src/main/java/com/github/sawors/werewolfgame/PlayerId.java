package com.github.sawors.werewolfgame;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class PlayerId {
    String discriminant;
    String tag = "WW-";
    int iduniquelength = 6;

    @Override
    public String toString() {
        return tag+discriminant.toUpperCase(Locale.ENGLISH);
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
    public PlayerId(String id){
        Main.logAdmin(id.substring(0,tag.length()).toUpperCase(Locale.ENGLISH));
        if(id.substring(0,tag.length()).toUpperCase(Locale.ENGLISH).equals("WW-") && id.length() == tag.length()+iduniquelength){
            for(char ch : id.substring(tag.length(),tag.length()+iduniquelength).toCharArray()){
                if(!Character.isLetterOrDigit(ch)){
                    this.discriminant = generateRandomDiscriminant();
                    return;
                }
            }

            this.discriminant = id.substring(tag.length(),tag.length()+iduniquelength);
        } else if(id.length() == iduniquelength){
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

    public PlayerId(){
        this.discriminant = generateRandomDiscriminant();
    }

    private String generateRandomDiscriminant(){
        return RandomStringUtils.randomAlphanumeric(iduniquelength).toUpperCase(Locale.ENGLISH);
    }
}
