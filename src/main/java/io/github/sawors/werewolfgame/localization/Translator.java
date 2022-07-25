package io.github.sawors.werewolfgame.localization;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.YamlMapParser;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import javax.annotation.WillClose;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translator {
    
    private final Map<LoadedLocale, Map<String, Object>> locales = new HashMap<>();
    private LoadedLocale defaultlocale;
    
    public Translator(LoadedLocale defaultlocale){
        this.defaultlocale = defaultlocale;
    }
    public Translator(){
        this.defaultlocale = null;
    }
    
    public void clearLoadedLocales(){
        locales.clear();
    }
    
    public @Nullable Map<String, Object> getLocaleData(LoadedLocale locale){
        return locales.get(locale);
    }
    
    public void setDefaultLocale(LoadedLocale locale){
        this.defaultlocale = locale;
    }
    
    public void load(File @NotNull ... yamlfile){
        for(File locale : yamlfile){
            try(InputStream input = new FileInputStream(locale)){
                Map<String, Object> structure = new Yaml().load(input);
                String code = locale.getName().substring(0,locale.getName().indexOf("."));
                String name = structure.containsKey("locale-name") ? YamlMapParser.getString(structure,"locale-name") : code;
                locales.put(new LoadedLocale(code,name), structure);
                if(defaultlocale == null){
                    defaultlocale = new LoadedLocale(code,name);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public void load(@WillClose InputStream localestream, LoadedLocale locale){
        if(localestream == null){
            return;
        }
        locales.put(locale, new Yaml().load(localestream));
        if(defaultlocale == null){
            defaultlocale = locale;
        }
        try{
            localestream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public List<LoadedLocale> getLoadedLocales(){
        return List.copyOf(locales.keySet());
    }
    
    public void printLoaded(){
        for(LoadedLocale locale : locales.keySet()){
            Main.logAdmin("Locale",locale.getDisplay());
        }
    }
    
    public LoadedLocale getDefaultLocale() {
        return defaultlocale;
    }
}
