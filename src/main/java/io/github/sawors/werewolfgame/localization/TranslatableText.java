package io.github.sawors.werewolfgame.localization;

import io.github.sawors.werewolfgame.YamlMapParser;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslatableText {

    Translator translator;
    LoadedLocale locale;

    
    public TranslatableText(@NotNull Translator translator, @Nullable LoadedLocale locale){
        this.locale = locale != null ? locale : translator.getDefaultLocale();
        this.translator = translator;
    }
    
    public String get(String key){
        return get(key, false);
    }
    
    public String get(String key, boolean suppresserrors){
        return get(key,suppresserrors, true);
    }
    
    public String get(String key, boolean suppresserrors, boolean usefallback){
        if(!translator.getLoadedLocales().contains(locale)){
            if(usefallback){
                return new TranslatableText(translator, translator.getDefaultLocale()).get(key,suppresserrors,false);
            } else {
                return suppresserrors ? null : "***locale "+locale+" not loaded, it usually indicate an error in locale name***";
            }
        }
        String error = "***key \""+key+"\" in locale "+locale+" not found, report this to the locale's author***";
        try{
            return YamlMapParser.getData(translator.getLocaleData(locale), key);
        } catch (ParseException e) {
            return suppresserrors ? null : error+" ***"+e.getMessage()+"***";
        } catch (InvalidKeyException e) {
            if(usefallback){
                return new TranslatableText(translator, translator.getDefaultLocale()).get(key,suppresserrors,false);
            } else {
                return suppresserrors ? null : error;
            }
        }
    }
    
    public Map<String, String> getMap(String key){
        Map<String, String> result = new HashMap<>();
        Object output =  null;
        Map<String, Object> localecontent = translator.getLocaleData(locale);
        if(localecontent != null && localecontent.size() > 0){
            try{
                output = YamlMapParser.getObject(localecontent, key);
            } catch (ParseException e) {
                return Map.of();
            }
        }
        if(output instanceof Map<?,?>){
            ((Map<?, ?>) output).forEach((k,value)-> result.put(k.toString(),value.toString()));
        } else if(output instanceof List<?>){
            for(int i = 0; i<((List<?>) output).size(); i++){
                result.put(String.valueOf(i), String.valueOf(((List<?>) output).get(i)));
            }
        } else {
            result.put("0", String.valueOf(result));
        }
        
        return result;
    }
    
    //TODO : add weight system to texts (but I don't think it's *that* useful)
    public String getVariableText(String key){
        List<String> texts = List.copyOf(getMap(key).values());
        return texts.get((int) (Math.random()*(texts.size()-1)));
    }

    public String getPluralForm(String word){
        return word+"s";
    }
    public String getSingularForm(String word){
        return word+"s";
    }
}
