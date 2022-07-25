package com.github.sawors.werewolfgame.extensionsloader;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.game.events.BackgroundEvent;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.localization.Translator;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.util.*;

public abstract class WerewolfExtension {
    public Set<PlayerRole> roles = new HashSet<>();
    public List<BackgroundEvent> events = new ArrayList<>();
    public Translator translator;
    public final File resourcedirectory;
    public String extensionid;
    
    public WerewolfExtension(){
        this.translator = new Translator();
        this.resourcedirectory = new File(Main.getExtensionsLocation()+File.separator+getMeta().getName());
        this.extensionid = RandomStringUtils.randomAlphanumeric(6);
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
    public abstract ExtensionMetadata getMeta();
    
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
        langlocation.mkdir();
        if(langlocation.exists() && langlocation.listFiles() != null){
            for(File file : Objects.requireNonNull(langlocation.listFiles())){
                translator.load(file);
            }
        }
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
