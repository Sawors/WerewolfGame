package com.github.sawors.werewolfgame.localization;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.WillClose;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class TranslatableText {

    private static final Map<String, Map<String, Object>> locales = new HashMap<>();

    public static void load(File @NotNull ... yamlfile){
        for(File locale : yamlfile){
            try(InputStream input = new FileInputStream(locale)){
                locales.put(locale.getName(), new Yaml().load(input));
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    public static Set<String> getLoadedLocales(){
        return locales.keySet();
    }

    public static void load(@WillClose InputStream localestream, String localename){
        if(localestream == null){
            return;
        }
        locales.put(localename, new Yaml().load(localestream));
        try{
            localestream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public static String get(String textkey){
        return get(textkey, BundledLocale.DEFAULT);
    }

    public static String get(String textkey, @NotNull BundledLocale locale){
        return get(textkey, locale.toString());
    }

    public static String get(@NotNull String textkey, @NotNull String locale){
        if(!locales.containsKey(locale)){
            return "locale "+locale+" not loaded, it usually indicate an error in locale name";
        }
        String[] hierarchy = textkey.split("\\.");
        if(hierarchy.length <= 0){
            return "";
        }
        if(hierarchy[0].equals("plural-replacements")){
            return "";
        }
        //Main.logAdmin("Hierarchy"+ Arrays.toString(hierarchy));
        String error = "key \""+textkey+"\" in locale "+locale+" (loaded from stream) not found, it probably indicate an inexistent or outdated locale, report this to the locale's Author or on https://github.com/Sawors/WerewolfGame/issues/new";
        String translated;
        Map<String, Object> data = locales.get(locale);
        if(data.containsKey(textkey) && !(data.get(textkey) instanceof Collection)){
            return String.valueOf(data.get(textkey));
        }

        int level = 0;
        //Main.logAdmin("Hlevel : "+hierarchy[level]);

        // CUSTOM YAML PARSER MADE FOR THE OCCASION, TODO : Reuse this and make its own method
        Object text = data.get(hierarchy[level]);
        //Main.logAdmin("text : "+text);
        do{
            //Main.logAdmin("Type0 : "+text.getClass().getName());
            level++;
            String key = hierarchy[level];
            if(text instanceof Map){
                //Main.logAdmin("Map : "+text);
                //Main.logAdmin("Mapkey : "+key);
                if(((Map<?, ?>) text).containsKey(key)){
                    text = ((Map<?, ?>) text).get(key);
                    //Main.logAdmin("Newtext : "+text);
                } else {
                    return error+" (parsing error at level : "+key+")";
                }
            } else if (text instanceof List){
                //Main.logAdmin("List : "+text);
                if(((List<?>) text).contains(key)){
                    text = ((List<?>) text).get(((List<?>) text).indexOf(key));
                } else {
                    return error+" (parsing error at level : "+key+")";
                }
            }
            //Main.logAdmin("Type1 : "+text.getClass().getName());
        } while (text instanceof Collection || text instanceof Map);
        //Main.logAdmin("Type2 : "+text.getClass().getName());
        translated = String.valueOf(text);
        if(translated == null || translated.equals("")){
            return error;
        }
        return translated;
    }

    public static String getPluralForm(String word){
        return word+"s";
    }
    public static String getSingularForm(String word){
        return word+"s";
    }
}
