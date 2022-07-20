package com.github.sawors.werewolfgame.localization;

public enum BundledLocale {
    DEFAULT, fr_FR, en_UK;


    public String getPath() {
        return "locales/" +super.toString()+".yml";
    }
}
