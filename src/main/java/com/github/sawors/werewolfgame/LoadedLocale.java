package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.localization.TranslatableText;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LoadedLocale {
    
    String code;
    String name;
    String identifier;
    
    public LoadedLocale(@NotNull String code, @Nullable String name){
        if(name == null || name.equalsIgnoreCase("null")){
            this.name = code;
        }
        this.code = code;
        this.identifier = "";
    }
    
    public LoadedLocale(String code){
        this.code = code;
        this.name = code;
        
        if(TranslatableText.getLoadedLocales().contains(this)){
            this.name = List.copyOf(TranslatableText.getLoadedLocales()).get(TranslatableText.getLoadedLocales().indexOf(this)).getName();
        }
        this.identifier = "";
    }
    
    // Load only from Main
    protected LoadedLocale(@NotNull String code, @Nullable String name, @NotNull String referenceidentifier){
        if(name == null || name.equalsIgnoreCase("null")){
            this.name = code;
        } else {
            this.name = name;
        }
        this.code = code;
        Main.logAdmin(referenceidentifier);
        this.identifier = referenceidentifier.toLowerCase(Locale.ROOT);
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && Objects.equals(this.toString(), obj.toString());
    }
    
    public String getName(){
        return this.name;
    }
    public String getIdentifier(){
        return this.identifier;
    }
    
    public String getDisplay(){
         return code+" : "+name+" ("+identifier+")";
    }
    
    public static LoadedLocale fromReference(String reference){
        TranslatableText.printLoaded();
        if(reference.length() == 5 && reference.contains("_")){
            return new LoadedLocale(reference);
        }
        if(reference.length() == 2){
            for(LoadedLocale loc : TranslatableText.getLoadedLocales()){
                if(loc.toString().substring(0,2).equalsIgnoreCase(reference)){
                    return loc;
                }
            }
        }
        for(LoadedLocale loc : TranslatableText.getLoadedLocales()){
            Main.logAdmin("checking perfect",loc.getDisplay());
            if(loc.getIdentifier().equalsIgnoreCase(reference)){
                Main.logAdmin("found matching perfect",loc.getDisplay());
                return loc;
            }
        }
        for(LoadedLocale loc : TranslatableText.getLoadedLocales()){
            Main.logAdmin("checking names",loc);
            if(loc.getName().toLowerCase(Locale.ROOT).contains(reference)){
                Main.logAdmin("found matching and using name "+loc.getName(),loc.getDisplay());
                return loc;
            }
        }
        return Main.getLanguage();
    }
}
