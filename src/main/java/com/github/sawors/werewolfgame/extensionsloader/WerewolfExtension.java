package com.github.sawors.werewolfgame.extensionsloader;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.localization.Translator;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class WerewolfExtension {
    public Set<PlayerRole> roles = new HashSet<>();
    public Translator translator;
    public final File resourcedirectory;
    public String extensionid;
    
    public WerewolfExtension(){
        this.translator = new Translator();
        this.resourcedirectory = new File(Main.getExtensionsLocation()+File.separator+getMeta().getName());
        this.extensionid = RandomStringUtils.randomAlphanumeric(6);
        File langlocation = new File(resourcedirectory+File.separator+"languages");
        langlocation.mkdir();
        if(langlocation.exists() && langlocation.listFiles() != null){
            for(File file : Objects.requireNonNull(langlocation.listFiles())){
                translator.load(file);
            }
        }
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
        roles.addAll(List.of(role));
    };
    
    public Translator getTranslator(){
        return translator;
    }
    
    public void setTranslator(Translator translator){
        this.translator = translator;
    }
    
    public String getId(){
        return extensionid;
    }
}
