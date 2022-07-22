package com.github.sawors.werewolfgame.localization;

import com.github.sawors.werewolfgame.LoadedLocale;
import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.YamlMapParser;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import javax.annotation.WillClose;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslatableText {

    private static final Map<LoadedLocale, Map<String, Object>> locales = new HashMap<>();

    public static void load(File @NotNull ... yamlfile){
        for(File locale : yamlfile){
            try(InputStream input = new FileInputStream(locale)){
                Map<String, Object> structure = new Yaml().load(input);
                String code = locale.getName().substring(0,locale.getName().indexOf("."));
                String name = structure.containsKey("locale-name") ? YamlMapParser.getString(structure,"locale-name") : code;
                locales.put(new LoadedLocale(code,name), structure);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static List<LoadedLocale> getLoadedLocales(){
        return List.copyOf(locales.keySet());
    }

    public static void load(@WillClose InputStream localestream, LoadedLocale locale){
        if(localestream == null){
            return;
        }
        locales.put(locale, new Yaml().load(localestream));
        try{
            localestream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static @NotNull String get(@NotNull String textkey, @NotNull LoadedLocale locale){
        String output = get(textkey,locale,false);
        return output != null ? output : "how can this happen ?";
    }
    
    public static @Nullable String get(@NotNull String textkey, @NotNull LoadedLocale locale, boolean suppreserrors){
        if(!locales.containsKey(locale)){
            return suppreserrors ? null : "***locale "+locale+" not loaded, it usually indicate an error in locale name***";
        }
        String error = "***key \""+textkey+"\" in locale "+locale+" not found, report this to the locale's author***";
        try{
            return YamlMapParser.getData(locales.get(locale), textkey);
        } catch (ParseException e) {
            return suppreserrors ? null : error+" ***"+e.getMessage()+"***";
        } catch (InvalidKeyException e) {
            return suppreserrors ? null : error;
        }
    }

    public static String getPluralForm(String word){
        return word+"s";
    }
    public static String getSingularForm(String word){
        return word+"s";
    }
    
    public static void printLoaded(){
        for(LoadedLocale locale : locales.keySet()){
            Main.logAdmin("Locale",locale.getDisplay());
        }
    }
}
