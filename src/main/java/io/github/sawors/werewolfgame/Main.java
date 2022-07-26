package io.github.sawors.werewolfgame;

import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.roles.PlayerRole;
import io.github.sawors.werewolfgame.game.roles.PrimaryRole;
import io.github.sawors.werewolfgame.localization.BundledLocale;
import io.github.sawors.werewolfgame.localization.LoadedLocale;
import io.github.sawors.werewolfgame.localization.Translator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    //TODO : Linking check
    private static HashMap<String, GameManager> activegames = new HashMap<>();
    private static boolean standalone;
    private static boolean discordenabled = false;
    private static boolean minecraftenabled = false;
    private static JDA jda;
    private static boolean usecaching = true;
    private static HashMap<UserId, LinkedUser> cachedusers = new HashMap<>();
    // I use this linking map to avoid creating a new JDA Event Listener each time a new GameManager is created
    // Long : Channel ID
    // String : GameManager ID
    //TODO : Linking check
    private static HashMap<Long, String> channellink = new HashMap<>();
    private static Map<String, Object> configmap;
    // String is the Discord User's ID
    private static String instancename = RandomStringUtils.randomNumeric(6);
    private static List<PrivateChannel> logchannels = new ArrayList<>();
    //
    // Data Storage
    private static File datalocation;
    private static File dbfile;
    private static File configfile;
    private static File languageslocation;
    private static File extensionslocation;
    //
    // Role Loading Data
    private static Set<PlayerRole> rolepool = new HashSet<>();
    private static WerewolfExtension rootextension = new RootExtension(null, null);
    //
    // Language Data
    private static final Translator instancetranslator = new Translator();
    //
    // Extensions
    private static final Set<WerewolfExtension> extensions = new HashSet<>();
    private static final Map<File, BundledLocale> createdbundledlocales = new HashMap<>();


    public static void init(boolean standalone, String token, File datastorage){
        //[=======================Do Not Put Init Code Here=========================]
        datastorage.mkdirs();
        datalocation = datastorage;
        languageslocation = new File(datalocation+File.separator+"languages");
        languageslocation.mkdir();
        extensionslocation = new File(datalocation+File.separator+"extensions");
        extensionslocation.mkdir();
        Main.standalone = standalone;
        try{
            Main.logAdmin("Data stored under "+datalocation.getCanonicalPath());
        }catch (IOException e){
            e.printStackTrace();
        }
        configfile = new File(datalocation+File.separator+"config.yml");
        //[=========================================================================]
        
        if((token == null || token.length() < 8) && getConfigData("discord-token") != null && getConfigData("discord-token").length() > 8){
            token = getConfigData("discord-token");
        }
        
        minecraftenabled = !Main.standalone && PluginLauncher.getPlugin().isEnabled();
        
        if(standalone){
            reloadConfig();
        }
        
        dbfile = new File(datalocation+File.separator+"database.db");
        try{
            Main.logAdmin("Database located at "+dbfile);
            dbfile.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
            Main.logAdmin("database creation failed, could not access the file");
        }
        
        jda = DiscordBot.initJDA(token, Main.standalone);
        if(jda != null){
            discordenabled = true;
        }
        
        DatabaseManager.connectInit();


        // shut down if no execution context is found
        if(!minecraftenabled && !discordenabled){
            throw new RuntimeException("No execution context found, " +
                    "please enable at least one : " +
                    "Minecraft by successfully starting the program as a plugin and/or Discord by providing an API token." +
                    "If launched in standalone mode, provide a Discord API token as the first argument when launching the Jar file (no interaction with Minecraft possible in this mode)");
        }

        String configname = getConfigData("instance-name");
        instancename = configname.equals("") ? "I"+instancename : configname+" : I"+instancename;
        
        
        //TODO : for this and config add overwriting only for missing fields, useless to overwrite the whole file
        BundledLocale[] bundledlocales = {
                BundledLocale.en_UK,
                BundledLocale.fr_FR
        };
        for(BundledLocale locale : bundledlocales){
            try{
                File file = new File(languageslocation+File.separator+locale+".yml");
                boolean overwrite = false;
                if(file.exists()){
                    try(InputStream in = new FileInputStream(file); InputStream ref = Main.class.getClassLoader().getResourceAsStream(locale.getPath())) {
                        Map<String, Object> loaded = new Yaml().load(in);
                        Map<String, Object> reference = new Yaml().load(ref);
        
                        if(!loaded.keySet().containsAll(reference.keySet())){
                            overwrite = true;
                        }
                    }
                }
                if(!file.exists() || overwrite){
                    file.createNewFile();
                    try(OutputStream out = new FileOutputStream(file); InputStream in = Main.class.getClassLoader().getResourceAsStream(locale.getPath())) {
                        if(in != null){
                            Main.logAdmin("Regenerating locale",file);
                            out.write(in.readAllBytes());
                            createdbundledlocales.put(file, locale);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        reloadLanguages();
        Main.logAdmin("Default language set to",instancetranslator.getDefaultLocale().getDisplay());
    
    
    
    
        //TODO : Extension loading
        
        //TODO :create jar files for bundled jar extensions (to be added)
        //      alternatively provide a way to download extensions from web <----- IMPORTANT !!!!! Provide full handling of web based extensions
        
        // load .jar extensions
        File[] exts = extensionslocation.listFiles();
        if(exts != null){
            for(File file : exts){
                loadExtension(file);
            }
        }
        // add root extension
        rootextension = new RootExtension(instancetranslator, datalocation);
        extensions.add(new RootExtension(instancetranslator, datalocation));
    
        // add classic extension
        //extensions.add(new ClassicExtensionLoader());
    }
    
    protected static boolean isInstanceAdmin(String discordid){
        try(InputStream input = new FileInputStream(configfile)){
            Map<String, Object> yamlconfig = new Yaml().load(input);
            if(yamlconfig.containsKey("admin-users") && yamlconfig.get("admin-users") instanceof List){
                Object adminlist = yamlconfig.get("admin-users");
                return adminlist instanceof List<?> && ((List<?>) adminlist).contains(discordid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static Set<WerewolfExtension> getLoadedExtensions(){
        return Set.copyOf(extensions);
    }

    protected static String getInstanceName(){
        return instancename;
    }

    protected static void addLogChannel(PrivateChannel chan){
        logchannels.add(chan);
    }
    protected static void removeLogChannel(PrivateChannel chan){
        logchannels.remove(chan);
    }
    protected static List<String> getLogChannels(){
        List<String> ids = new ArrayList<>();
        logchannels.forEach(chan -> ids.add(chan.getId()));
        return ids;
    }
    
    public static File getDataLocation(){
        return datalocation;
    }
    public static File getLocalesLocation(){
        return languageslocation;
    }
    public static File getExtensionsLocation(){
        return extensionslocation;
    }

    public static void reloadLanguages(){
    
        // DEFAULT FALLBACK LOCALE (DO NOT CHANGE)
        LoadedLocale defloc = BundledLocale.en_UK.getLocale();
        
        instancetranslator.clearLoadedLocales();
        /*
        instancetranslator.load(Main.class.getClassLoader().getResourceAsStream(BundledLocale.en_UK.getPath()), BundledLocale.en_UK.getLocale());
        instancetranslator.load(Main.class.getClassLoader().getResourceAsStream(BundledLocale.fr_FR.getPath()), BundledLocale.fr_FR.getLocale());
        */
        
        File[] toload = languageslocation.listFiles();
        if(toload != null){
            for(File locale : toload){
                if(locale.getName().toLowerCase(Locale.ROOT).endsWith(".yml") || locale.getName().toLowerCase(Locale.ROOT).endsWith(".yaml") ){
                    if(createdbundledlocales.containsKey(locale)){
                        try(InputStream stream = new FileInputStream(locale)){
                            Main.logAdmin("Loading bundled locale",locale.getAbsolutePath());
                            instancetranslator.load(stream,createdbundledlocales.get(locale).getLocale());
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    } else {
                        Main.logAdmin("Loading locale",locale.getAbsolutePath());
                        instancetranslator.load(locale);
                    }
                }
            }
        }
        LoadedLocale defaultlocale = new LoadedLocale(getConfigData("instance-language"));
        instancetranslator.setDefaultLocale(instancetranslator.getLoadedLocales().contains(defaultlocale) ? instancetranslator.getLoadedLocales().get(instancetranslator.getLoadedLocales().indexOf(defaultlocale)) : defloc);
        for(LoadedLocale loc : instancetranslator.getLoadedLocales()){
            Main.logAdmin("Loaded locale",loc.getDisplay());
        }
        rootextension.setTranslator(instancetranslator);
    }
    public static @NotNull Translator getTranslator(){
        return instancetranslator;
    }
    public static WerewolfExtension getRootExtensionn(){
        return rootextension;
    }
    public static void reloadConfig(){
        try{
            configfile.createNewFile();
            boolean overwrite = true;
            try(InputStream loadold = new FileInputStream(configfile); InputStream reference = Main.class.getClassLoader().getResourceAsStream("config.yml")){
                Map<String, Object> oldconfig = new Yaml().load(loadold);
                String regen = YamlMapParser.getString(oldconfig, "regenerate");
                if(regen != null && regen.length() >= 2){
                    overwrite = !regen.equalsIgnoreCase("false");
                }

                if(!overwrite){
                    // check for missing fields
                    Map<String, Object> refconfig = new Yaml().load(reference);
                    overwrite = !oldconfig.keySet().containsAll(refconfig.keySet());
                }
            } catch (FileNotFoundException e){
                overwrite = true;
            }
            if(overwrite){
                try(OutputStream writer = new FileOutputStream(configfile); InputStream config = Main.class.getClassLoader().getResourceAsStream("config.yml")){
                    if(config != null){
                        Main.logAdmin("regenerating config.yml (replacing the old file if it existed)");
                        writer.write(config.readAllBytes());
                    }
                }
            } else {
                Main.logAdmin("config file found, loading it !");
            }
            try(InputStream config = new FileInputStream(configfile)){
                configmap = new Yaml().load(config);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    private void loadRoles(Set<PrimaryRole> toload){
        for(PrimaryRole role : toload){
            role.onLoad();
            role.getEvents();
        }
    }
    
    //TODO : handle config without bukkit methods
    public static Map<String, Object> getConfigMap(){
        return Map.copyOf(configmap);
    }
    public static String getConfigData(String key){
        return YamlMapParser.getString(configmap,key);
    }
    public static void saveConfig() throws IOException {
        try(OutputStream writer = new FileOutputStream(datalocation+File.separator+"config.yml")){
            writer.write(new Yaml().dump(configmap).getBytes());
        }
    }
    
    public static Set<PlayerRole> getRolePool(){
        return rolepool;
    }
    
    public static void linkChannel(Long channelid, String gamemanagerid){
        channellink.put(channelid, gamemanagerid);
    }
    
    public static boolean isLinked(Long channelid){
        return channellink.containsKey(channelid);
    }
    
    public static void unlinkChannel(Long channelid){
        channellink.remove(channelid);
    }
    
    public static GameManager getManager(Long channelid){
        return GameManager.fromId(getManagerId(channelid));
    }
    
    public static String getManagerId(Long channelid){
        return channellink.get(channelid);
    }
    
    protected static File getDbFile(){
        return dbfile;
    }

    public static void registerNewGame(GameManager manager){
        activegames.put(manager.getId(), manager);
    }
    
    public static void removeGame(String managerid){
        activegames.remove(managerid);
    }

    public static String generateRandomGameId(){
        return RandomStringUtils.randomNumeric(8);
    }

    public static JDA getJDA(){
        return jda;
    }

    public static void logAdmin(Object text){
        logAdmin("",text);
    }

    public static void logAdmin(Object title, Object text){
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        time = "[WerewolfGame : "+time+"] ";
        String message = title.equals("") ? time+text : time+title+" : "+text;
        if(standalone){
            System.out.println(message);
        } else {
            PluginLauncher.logToOp(message);
        }
        if(logchannels.size() > 0){
            for(PrivateChannel channel : logchannels){
                channel.sendMessage(message).queue();
            }
        }
    }

    public static HashMap<String, GameManager> getGamesList(){
        return activegames;
    }
    
    public static void cacheUser(UserId id, LinkedUser user){
        cachedusers.put(id,user);
    }
    
    public static void cacheUser(LinkedUser user){
        cachedusers.put(user.getId(),user);
    }
    
    public static boolean useUserCache(){
        return usecaching;
    }
    
    protected static LinkedUser getCachedUser(UserId id){
        return cachedusers.get(id);
    }
    
    //
    //  Instance Commands
    //
    protected static void setInstanceLanguage(LoadedLocale language){
        if(instancetranslator.getLoadedLocales().contains(language)){
            instancetranslator.setDefaultLocale(language);
        }
    }
    // // // // // // // // // //
    private static void loadExtension(File extensionjar){
        if(extensionjar != null && extensionjar.toString().toLowerCase(Locale.ROOT).endsWith(".jar")){
                Main.logAdmin("Loading extension",extensionjar);
                try(JarFile extension = new JarFile(extensionjar)){
                    String mainname = "";
                    Enumeration<JarEntry> findyaml = extension.entries();
                    while(findyaml.hasMoreElements()){
                        JarEntry tocheck = findyaml.nextElement();
                        if(tocheck.getName().equals("extension.yml")){
                            Main.logAdmin("extension.yml found for",extensionjar.getName());
                            try(InputStream input = extension.getInputStream(tocheck)){
                                Map<String, String> data = new Yaml().load(input);
                                mainname = data.get("main");
                            }
                        }
                    }
                    Enumeration<JarEntry> findmain = extension.entries();
                    while(findmain.hasMoreElements()){
                        JarEntry tocheck = findmain.nextElement();
                        if(tocheck.getRealName().endsWith("/"+mainname+".class")){
                            Main.logAdmin("Main class found for",extensionjar.getName());
                            URL[] classpath = {extensionjar.toURI().toURL()};
                            try(URLClassLoader classloader = URLClassLoader.newInstance(classpath)){
                                Class<?> cl = classloader.loadClass(tocheck.getName().replaceAll("/",".").substring(0,tocheck.getName().length()-".class".length()));
                                Constructor<?> ctor = cl.getConstructor();
                                Object crinst = ctor.newInstance();
                                if(crinst instanceof WerewolfExtension){
                                    extensions.add((WerewolfExtension) crinst);
                                    Main.logAdmin("Successfully loaded extension",extensionjar.getName());
                                }
                        
                                break;
                            } catch (
                                    ClassNotFoundException |
                                    InvocationTargetException |
                                    NoSuchMethodException |
                                    InstantiationException |
                                    IllegalAccessException e) {
                                Main.logAdmin("Error in loading extension",extensionjar);
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
            
            
            
            
            
            
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
    
    }
}
