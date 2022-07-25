package io.github.sawors.werewolfgame.localization;

import io.github.sawors.werewolfgame.YamlMapParser;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.security.InvalidKeyException;
import java.text.ParseException;

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
        if(!translator.getLoadedLocales().contains(locale)){
            return suppresserrors ? null : "***locale "+locale+" not loaded, it usually indicate an error in locale name***";
        }
        String error = "***key \""+key+"\" in locale "+locale+" not found, report this to the locale's author***";
        try{
            return YamlMapParser.getData(translator.getLocaleData(locale), key);
        } catch (ParseException e) {
            return suppresserrors ? null : error+" ***"+e.getMessage()+"***";
        } catch (InvalidKeyException e) {
            return suppresserrors ? null : error;
        }
    }
    
    /*public static @Nullable String get(@NotNull String textkey, @NotNull LoadedLocale locale, boolean suppreserrors){
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
    }*/



    public String getPluralForm(String word){
        return word+"s";
    }
    public String getSingularForm(String word){
        return word+"s";
    }
}
