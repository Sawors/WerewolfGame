package io.github.sawors.werewolfgame.extensionsloader;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.game.events.BackgroundEvent;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.localization.Translator;
import org.apache.commons.lang3.RandomStringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class WerewolfExtension {
    public Set<PlayerRole> roles = new HashSet<>();
    public List<BackgroundEvent> events = new ArrayList<>();
    public Translator translator;
    public final File resourcedirectory;
    public String extensionid;
    public ExtensionMetadata meta;
    
    public WerewolfExtension(){
        this.translator = new Translator();
        this.resourcedirectory = new File(Main.getExtensionsLocation()+File.separator+getMeta().getName());
        resourcedirectory.mkdirs();
        this.extensionid = RandomStringUtils.randomAlphanumeric(6);
        this.meta = loadMetadata();
        reloadLanguages();
        onLoad();
    }
    protected WerewolfExtension(Translator translator, File resourcedirectory){
        this.translator = translator;
        this.resourcedirectory = resourcedirectory;
        this.extensionid = RandomStringUtils.randomAlphanumeric(6);
        //onLoad();
    }
    public abstract void onLoad();
    public Set<PlayerRole> getRoles() {
        return roles;
    }
    public List<BackgroundEvent> getBackgroundEvents(){
        return events;
    }
    public ExtensionMetadata getMeta(){
        return this.meta;
    };
    
    public void registerNewRoles(PlayerRole... role){
        this.roles.addAll(List.of(role));
    }
    
    public void registerBackgroundEvents(BackgroundEvent... events){
        this.events.addAll(List.of(events));
    }
    
    public Translator getTranslator(){
        return translator;
    }
    
    public void setTranslator(Translator translator){
        this.translator = translator;
    }
    
    public String getId(){
        return extensionid;
    }
    
    public void reloadLanguages(){
        translator.clearLoadedLocales();
        File langlocation = new File(resourcedirectory+File.separator+"languages");
        langlocation.mkdirs();
        if(langlocation.exists() && langlocation.listFiles() != null){
            for(File file : Objects.requireNonNull(langlocation.listFiles())){
                translator.load(file);
            }
        }
    }
    
    private ExtensionMetadata loadMetadata(){
        
        String name = "unkown extension";
        String author = "unkown author";
        String version = "unkown version";
        String description = "";
        String source = "unknown source";
        try(InputStream in = this.getClass().getClassLoader().getResourceAsStream("extension.yml")){
            Map<String, String> data = new Yaml().load(in);
            name = data.get("name") != null ? data.get("name") : name;
            author = data.get("author") != null ? data.get("author") : author;
            version = data.get("version") != null ? data.get("version") : version;
            description = data.get("description") != null ? data.get("description") : description;
            source = data.get("source") != null ? data.get("source") : source;
        } catch (IOException | NullPointerException e){
            e.printStackTrace();
        }
        
        return new ExtensionMetadata(name,version,author,source,description);
    }
    
    @Override
    public String toString() {
        return "extension:"+this.resourcedirectory.toString();
    }
    
    @Override
    public int hashCode() {
        return this.getClass().getCanonicalName().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass();
    }
}
