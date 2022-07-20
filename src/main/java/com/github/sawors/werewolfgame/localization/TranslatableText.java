package com.github.sawors.werewolfgame.localization;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TranslatableText {

    private static Map<String, File> locales = new HashMap<>();

    public static void load(File... yamlfile){
        for(File locale : yamlfile){
            locales.put(locale.getName(), locale);
        }
    }

    public static String get(String textkey, String locale){
        String translated = "no";

        if(translated == null || translated.equals("")){
            translated = "key "+textkey+" in locale "+locale+" not found, it probably indicate an inexistent or outdated locale, report this to the locale's Author or on https://github.com/Sawors/WerewolfGame/issues/new";
        }
        return translated;
    }
}
