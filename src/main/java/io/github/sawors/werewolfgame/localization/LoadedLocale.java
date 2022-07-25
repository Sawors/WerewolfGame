package io.github.sawors.werewolfgame.localization;

import io.github.sawors.werewolfgame.Main;
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
        
        if(Main.getTranslator().getLoadedLocales().contains(this)){
            this.name = List.copyOf(Main.getTranslator().getLoadedLocales()).get(Main.getTranslator().getLoadedLocales().indexOf(this)).getName();
        }
        this.identifier = "";
    }
    
    // Load only from Main
    public LoadedLocale(@NotNull String code, @Nullable String name, @NotNull String referenceidentifier){
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
        Main.logAdmin("Loaded locales",Main.getTranslator().getLoadedLocales());
        if(reference.length() == 5 && reference.contains("_")){
            Main.logAdmin("Locale code found for locale "+reference+", using it for generation");
            return new LoadedLocale(reference);
        }
        if(reference.length() == 2){
            for(LoadedLocale loc : Main.getTranslator().getLoadedLocales()){
                if(loc.toString().substring(0,2).equalsIgnoreCase(reference)){
                    Main.logAdmin("Locale code language indicator found, "+reference+" -> "+loc.getDisplay());
                    return loc;
                }
            }
        }
        for(LoadedLocale loc : Main.getTranslator().getLoadedLocales()){
            Main.logAdmin("checking perfect match (identifier)",loc.getDisplay());
            if(loc.getIdentifier().equalsIgnoreCase(reference)){
                Main.logAdmin("Found matching perfect, using ",loc.getDisplay());
                return loc;
            }
        }
        for(LoadedLocale loc : Main.getTranslator().getLoadedLocales()){
            Main.logAdmin("checking name",loc);
            if(loc.getName().toLowerCase(Locale.ROOT).contains(reference)){
                Main.logAdmin("Found matching and using name "+loc.getName(),loc.getDisplay());
                return loc;
            }
        }
        Main.logAdmin("Found no locale matching "+reference+", using instance locale instead ("+Main.getTranslator().getDefaultLocale().getDisplay()+")");
        return Main.getTranslator().getDefaultLocale();
    }
}
