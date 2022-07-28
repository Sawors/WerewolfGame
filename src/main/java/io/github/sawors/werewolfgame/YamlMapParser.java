package io.github.sawors.werewolfgame;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.*;

public class YamlMapParser {
    public static String getData(Map<String, Object> loadedyaml, String key) throws InvalidKeyException, ParseException {
        if(key == null || loadedyaml == null){
            throw new InvalidKeyException("Do not use null keys or null maps");
        }
        String output;
        String[] hierarchy = key.split("\\.");
    
        if(hierarchy.length <= 0){
            throw new InvalidKeyException("Do not use null keys");
        }
        
        if(loadedyaml.containsKey(key) && !(loadedyaml.get(key) instanceof Collection)){
            output = String.valueOf(loadedyaml.get(key));
            if(output.equals("null")){
                return null;
            }
            return output;
        }
    
        int level = 0;
        Object text = loadedyaml.get(hierarchy[level]);
        try{
            do{
                level++;
                String localkey = hierarchy[level];
                if(text instanceof Map){
                    if(((Map<?, ?>) text).containsKey(localkey)){
                        text = ((Map<?, ?>) text).get(localkey);
                    } else {
                        throw new ParseException("Parsing error at level : "+key,level);
                    }
                } else if (text instanceof List){
                    if(((List<?>) text).contains(localkey)){
                        text = ((List<?>) text).get(((List<?>) text).indexOf(localkey));
                    } else {
                        throw new ParseException("Parsing error at level : "+key,level);
                    }
                }
            } while (text instanceof Collection || text instanceof Map);
        } catch (IndexOutOfBoundsException ignored){} // ignoring because we still return "text" in the end
        output = String.valueOf(text);
        if(output == null || output.equals("")){
            throw new InvalidKeyException("Key not found while parsing");
        }
        if(output.equals("null")){
            return null;
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

    public static List<String> getallkeys(Map<?,?> maptoparse){
        List<String> fields = new ArrayList<>();

        Queue<Map<?, ?>> submaps = new LinkedList<>();
        submaps.add(maptoparse);
        //TODO : add full key building mechanic to check for missing fields and warn user (keys like : "roles.witch.channel")
        //  with each field telling the whole path to it thus making it really unique and removing possible duplications (using YAML default duplicate error)
        while(submaps.size() > 0){
            Map<?, ?> toparse = submaps.poll();
            for(Map.Entry<?, ?> entry : toparse.entrySet()){
                if(entry.getValue() instanceof Map map){
                    submaps.add(map);
                }
                fields.add(entry.getKey().toString());
            }
        }
        return fields;
    }
}
