package com.github.sawors.werewolfgame.localization;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.YamlMapParser;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.WillClose;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        return Set.copyOf(locales.keySet());
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
        return get(textkey, Main.getLanguage());
    }

    public static String get(String textkey, @NotNull BundledLocale locale){
        return get(textkey, locale.toString());
    }

    public static String get(@NotNull String textkey, @NotNull String locale){
        if(!locales.containsKey(locale)){
            return "***locale "+locale+" not loaded, it usually indicate an error in locale name***";
        }
        String error = "***key \""+textkey+"\" in locale "+locale+" (loaded from stream) not found, it probably indicate an inexistent or outdated locale, report this to the locale's Author or on*** https://github.com/Sawors/WerewolfGame/issues/new";
        try{
            return YamlMapParser.getData(locales.get(locale), textkey);
        } catch (ParseException e) {
            return error+" ***"+e.getMessage()+"***";
        } catch (InvalidKeyException e) {
            return error;
        }
    }

    public static String getPluralForm(String word){
        return word+"s";
    }
    public static String getSingularForm(String word){
        return word+"s";
    }
}
