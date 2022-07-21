package com.github.sawors.werewolfgame;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class YamlMapParser {
    public static String getData(Map<String, Object> loadedyaml, String key) throws InvalidKeyException, ParseException {
        if(key == null){
            throw new InvalidKeyException("Do not use null keys");
        }
        String output;
        String[] hierarchy = key.split("\\.");
    
        if(hierarchy.length <= 0){
            throw new InvalidKeyException("Do not use null keys");
        }
        
        if(loadedyaml.containsKey(key) && !(loadedyaml.get(key) instanceof Collection)){
            return String.valueOf(loadedyaml.get(key));
        }
    
        int level = 0;
        //Main.logAdmin("Hlevel : "+hierarchy[level]);
    
        // CUSTOM YAML PARSER MADE FOR THE OCCASION, TODO : Reuse this and make its own method
        Object text = loadedyaml.get(hierarchy[level]);
        //Main.logAdmin("text : "+text);
        do{
            //Main.logAdmin("Type0 : "+text.getClass().getName());
            level++;
            String localkey = hierarchy[level];
            if(text instanceof Map){
                //Main.logAdmin("Map : "+text);
                //Main.logAdmin("Mapkey : "+key);
                if(((Map<?, ?>) text).containsKey(localkey)){
                    text = ((Map<?, ?>) text).get(localkey);
                    //Main.logAdmin("Newtext : "+text);
                } else {
                    throw new ParseException("Parsing error at level : "+key,level);
                }
            } else if (text instanceof List){
                //Main.logAdmin("List : "+text);
                if(((List<?>) text).contains(localkey)){
                    text = ((List<?>) text).get(((List<?>) text).indexOf(localkey));
                } else {
                    throw new ParseException("Parsing error at level : "+key,level);
                }
            }
            //Main.logAdmin("Type1 : "+text.getClass().getName());
        } while (text instanceof Collection || text instanceof Map);
        //Main.logAdmin("Type2 : "+text.getClass().getName());
        output = String.valueOf(text);
        if(output == null || output.equals("")){
            throw new InvalidKeyException("Key not found while parsing");
        }
        return output;
    }
    
    public static @Nullable String getString(@NotNull Map<String, Object> loadedyaml, String key){
        try{
            return getData(loadedyaml,key);
        } catch (ParseException | InvalidKeyException eparse){
            return null;
        }
    }
    
    public static @Nullable Integer getInt(@NotNull Map<String, Object> loadedyaml, String key){
        try{
            String data = getData(loadedyaml,key);
            return Integer.valueOf(data);
        } catch (ParseException | InvalidKeyException eparse){
            return null;
        }
        
    }
}
