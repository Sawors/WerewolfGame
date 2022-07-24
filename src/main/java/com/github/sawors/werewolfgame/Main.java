package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.bundledextensions.classic.ClassicExtensionLoader;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.roles.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.PrimaryRole;
import com.github.sawors.werewolfgame.localization.BundledLocale;
import com.github.sawors.werewolfgame.localization.LoadedLocale;
import com.github.sawors.werewolfgame.localization.Translator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private static Translator instancetranslator = new Translator();


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
        
        reloadLanguages();
        Main.logAdmin("Default language set to",getTranslator().getDefaultLocale().getName());
    
    
    
    
        //TODO : Extension loading
    
        // add root extension
        rootextension = new RootExtension(instancetranslator, datalocation);
    
        // add classic extension
        WerewolfExtension classicextension = new ClassicExtensionLoader();
        rolepool.addAll(classicextension.getRoles());
    
        Main.logAdmin("rolepool",rolepool);
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
        instancetranslator.load(Main.class.getClassLoader().getResourceAsStream(BundledLocale.en_UK.getPath()), BundledLocale.en_UK.getLocale());
        instancetranslator.load(Main.class.getClassLoader().getResourceAsStream(BundledLocale.fr_FR.getPath()), BundledLocale.fr_FR.getLocale());
        
        File[] toload = languageslocation.listFiles();
        if(toload != null){
            for(File locale : toload){
                if(locale.getName().toLowerCase(Locale.ROOT).endsWith(".yml") || locale.getName().toLowerCase(Locale.ROOT).endsWith(".yaml") ){
                    Main.logAdmin("Loading locale",locale.getAbsolutePath());
                    instancetranslator.load(locale);
                }
            }
        }
        LoadedLocale defaultlocale = new LoadedLocale(getConfigData("instance-language"));
        instancetranslator.setDefaultLocale(instancetranslator.getLoadedLocales().contains(defaultlocale) ? defaultlocale : defloc);
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
                    Map<String, Object> refconfig = new Yaml().load(reference);
                    String version = oldconfig.containsKey("config-version") ? YamlMapParser.getString(oldconfig, "config-version") : null;
                    String refversion = YamlMapParser.getString(refconfig, "config-version");
                    Main.logAdmin(version);
                    Main.logAdmin(refversion);
                    if(!(version != null && refversion != null && version.contains(".") && refversion.contains(".") && version.substring(0,version.indexOf(".")).equals(refversion.substring(0,version.indexOf("."))))){
                        // version conflict detected, adding missing fields to the old config
                        Set<String> oldfields = oldconfig.keySet();
                        Set<String> newfields = refconfig.keySet();
                        boolean outdated = false;
                        for(String field : newfields){
                            if(!oldfields.contains(field)){
                                oldconfig.put(field, refconfig.get(field));
                                outdated = true;
                            }
                        }
                        if(outdated){
                            try(OutputStream writer = new FileOutputStream(configfile)){
                                writer.write(new Yaml().dump(oldconfig).getBytes());
                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }

                    }
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
    
    private void loadExtensions(){
    
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
    
    private static Class<?> loadExtension(File file){
        URL pathtofile;
        try{
           pathtofile = new URL("file://"+file.getCanonicalPath());
           try(URLClassLoader loader = URLClassLoader.newInstance(new URL[]{pathtofile})){
               try(InputStream config = loader.getResourceAsStream("resources/extension.yml")){
                   Main.logAdmin(config);
                   Main.logAdmin(pathtofile);
                   Class<?> loaded0 = loader.loadClass("com/github/sawors/Main");
                   Main.logAdmin(loaded0.getMethods());
                   if(config != null){
                       Yaml yaml = new Yaml();
                       Map<String, String> data = yaml.load(config);
                       String main = data.get("main");
                       if(main != null){
                           Class<?> loaded = loader.loadClass(main);
                           if(loaded.isInstance(WerewolfExtension.class)){
                               Main.logAdmin("uwu loaded : "+loaded.getName());
                               loaded.getDeclaredMethod("load").invoke(null);
                               return loaded;
                           }
                       }
                   }
                   
               } catch (NoSuchMethodException e) {
                   e.printStackTrace();
               } catch (InvocationTargetException | IllegalAccessException e) {
                   throw new RuntimeException(e);
               }
           } catch (ClassNotFoundException e) {
               e.printStackTrace();
           }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
