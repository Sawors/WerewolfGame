package io.github.sawors.werewolfgame.extensionsloader;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.YamlMapParser;
import io.github.sawors.werewolfgame.game.events.BackgroundEvent;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.localization.Translator;
import net.dv8tion.jda.api.Permission;
import org.apache.commons.lang3.RandomStringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class WerewolfExtension {
    public Set<PlayerRole> roles = new HashSet<>();
    public List<BackgroundEvent> events = new ArrayList<>();
    public Translator translator;
    public final File resourcedirectory;
    public File langlocation;
    public File configfile;
    public String extensionid;
    public ExtensionMetadata meta;
    public static final Map<File, String> createdbundledlocales = new HashMap<>();
    
    public WerewolfExtension(){
        this.translator = new Translator();
        this.meta = loadMetadata();
        this.resourcedirectory = new File(Main.getExtensionsLocation()+File.separator+getMeta().getName());
        resourcedirectory.mkdirs();
        langlocation = new File(resourcedirectory+File.separator+"languages");
        langlocation.mkdirs();
        configfile = new File(resourcedirectory+File.separator+"config.yml");
        try{
            configfile.createNewFile();
            try(OutputStream out = new FileOutputStream(configfile); InputStream in = getClass().getModule().getResourceAsStream("config.yml")){
                if(in != null){
                    out.write(in.readAllBytes());
                }
            }
        } catch (IOException e){
            Main.logAdmin("Error while creating config.yml for extension "+meta.getName(),e.getMessage());
        }
        this.extensionid = RandomStringUtils.randomAlphanumeric(6);
        onLoad();
        reloadLanguages();
    }
    protected WerewolfExtension(Translator translator, File resourcedirectory){
        this.translator = translator;
        this.resourcedirectory = resourcedirectory;
        if(this.resourcedirectory != null){
            this.resourcedirectory.mkdirs();
        }
        this.extensionid = RandomStringUtils.randomAlphanumeric(6);
        //onLoad();
    }
    public abstract void onLoad();
    public Set<PlayerRole> getRoles() {
        Set<PlayerRole> newinstances = new HashSet<>();
        for(PlayerRole role : roles){
            try{
                newinstances.add(role.getClass().getConstructor(WerewolfExtension.class).newInstance(this));
            }catch (NoSuchMethodException |
                        InstantiationException |
                        IllegalAccessException |
                        InvocationTargetException e){
                e.printStackTrace();
            }
        }
        return newinstances;
    }
    public List<BackgroundEvent> getBackgroundEvents(){
        List<BackgroundEvent> newinstances = new ArrayList<>();
        for(BackgroundEvent event : events){
            try{
                newinstances.add(event.getClass().getConstructor(WerewolfExtension.class).newInstance(this));
            }catch (NoSuchMethodException |
                    InstantiationException |
                    IllegalAccessException |
                    InvocationTargetException e){
                e.printStackTrace();
            }
        }
        return newinstances;
    }
    public ExtensionMetadata getMeta(){
        return this.meta;
    }
    
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
    
    public void addBundledLocale(String... localesname){
        if(resourcedirectory != null){
            File languageslocation = new File(resourcedirectory+File.separator+"languages");
            for(String locale : localesname){

                String locfilename = locale.toLowerCase(Locale.ROOT).endsWith(".yml") || locale.toLowerCase(Locale.ROOT).endsWith(".yaml") ? locale : locale+".yml";

                try{
                    File file = new File(languageslocation+File.separator+locale+".yml");
                    boolean overwrite = false;
                    if(file.exists()){
                        try(InputStream in = new FileInputStream(file); InputStream ref = getClass().getModule().getResourceAsStream("locales/"+locfilename)) {
                            Map<String, Object> loaded = new Yaml().load(in);
                            Map<String, Object> reference = new Yaml().load(ref);

                            if(loaded == null || reference == null || !new HashSet<>(YamlMapParser.getallkeys(loaded)).containsAll(YamlMapParser.getallkeys(reference))){
                                overwrite = true;
                            }
                        }
                    }
                    if(!file.exists() || overwrite){
                        file.createNewFile();
                        try(OutputStream out = new FileOutputStream(file); InputStream in = getClass().getModule().getResourceAsStream("locales/"+locfilename)) {
                            if(in != null){
                                Main.logAdmin("["+meta.getName()+"] -> "+"Regenerating locale",file);
                                out.write(in.readAllBytes());
                                createdbundledlocales.put(file, locale);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public List<Permission> getDefaultRoleChannelAllow(){
        return List.of(
                Permission.VIEW_CHANNEL,
                Permission.MESSAGE_ADD_REACTION
        );
    }
    public List<Permission> getDefaultRoleChannelDeny(){
        return List.of(
                Permission.MANAGE_CHANNEL,
                Permission.MESSAGE_SEND
        );
    }
    
    @Override
    public String toString() {
        return "extension:"+this.resourcedirectory;
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
