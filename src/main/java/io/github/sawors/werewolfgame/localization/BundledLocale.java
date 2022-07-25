package io.github.sawors.werewolfgame.localization;

import io.github.sawors.werewolfgame.Main;

public enum BundledLocale {
    DEFAULT, fr_FR, en_UK;


    public String getPath() {
        return "locales/" +super.toString()+".yml";
    }
    
    public LoadedLocale getLocale(){
        
        LoadedLocale fallback = Main.getTranslator().getDefaultLocale();
        
        switch(this){
            case en_UK:
                return new LoadedLocale("en_UK","English (United Kingdom)","english");
            case fr_FR:
                return new LoadedLocale("fr_FR","Français (France)","french");
        }
        return fallback;
    }
}
