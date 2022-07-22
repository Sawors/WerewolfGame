package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.extensions.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.PlayerRole;
import com.github.sawors.werewolfgame.game.roles.classic.*;
import com.github.sawors.werewolfgame.localization.BundledLocale;
import com.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.JDA;
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
    private static File datalocation;
    private static File dbfile;
    private static File configfile;
    private static boolean usecaching = true;
    private static HashMap<UserId, LinkedUser> cachedusers = new HashMap<>();
    // I use this linking map to avoid creating a new JDA Event Listener each time a new GameManager is created
    // Long : Channel ID
    // String : GameManager ID
    //TODO : Linking check
    private static HashMap<Long, String> channellink = new HashMap<>();
    // Use this to get all loaded roles
    private static Set<PlayerRole> rolepool = new HashSet<>();
    private static LoadedLocale instancelanguage;
    private static Map<String, Object> configmap;
    // String is the Discord User's ID
    private static Set<String> sudoers = new HashSet<>();
    private static String sudokey = "";


    public static void init(boolean standalone, String token, File datastorage){
        //[=======================Do Not Put Init Code Here=========================]
        datastorage.mkdirs();
        datalocation = datastorage;
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
        
        //TODO : Extension loading
        
        // add classic extension (by hand method)
        rolepool.add(new Cupid());
        rolepool.add(new Hunter());
        rolepool.add(new LittleGirl());
        rolepool.add(new Seer());
        rolepool.add(new Villager());
        rolepool.add(new Witch());
        rolepool.add(new Wolf());

        // load default locale
        
        BundledLocale defloc = BundledLocale.en_UK;
        
        TranslatableText.load(Main.class.getClassLoader().getResourceAsStream(defloc.getPath()), new LoadedLocale("en_UK","English (United Kingdom)","english"));
        TranslatableText.load(Main.class.getClassLoader().getResourceAsStream(BundledLocale.fr_FR.getPath()), new LoadedLocale("fr_FR","Français (France)","french"));
        reloadLanguages();
        TranslatableText.printLoaded();
        Main.logAdmin("Default language set to",instancelanguage.getName());
    }
    
    public static boolean isInstanceAdmin(String discordid){
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

    public static void reloadLanguages(){
        LoadedLocale defloc = new LoadedLocale("en_UK","English (United Kingdom)","english");
        File localespath = new File(datalocation+File.separator+"locales"+File.separator);
        localespath.mkdirs();
        File[] toload = localespath.listFiles();
        if(toload != null){
            for(File locale : toload){
                if(locale.getName().toLowerCase(Locale.ENGLISH).endsWith(".yml") || locale.getName().toLowerCase(Locale.ENGLISH).endsWith(".yaml") ){
                    Main.logAdmin("Loading locale",locale.getAbsolutePath());
                    TranslatableText.load(locale);
                }
            }
        }
        LoadedLocale defaultlocale = new LoadedLocale(getConfigData("instance-language"));
        if(TranslatableText.getLoadedLocales().contains(defaultlocale)){
            instancelanguage = defaultlocale;
        } else {
            instancelanguage = defloc;
        }
    }
    public static void reloadConfig(){
        try{
            configfile.createNewFile();
            boolean overwrite = true;
            try(InputStream loadold = new FileInputStream(configfile)){
                Map<String, Object> oldconfig = new Yaml().load(loadold);
                String regen = YamlMapParser.getString(oldconfig, "regenerate");
                if(regen != null && regen.length() >= 2){
                    overwrite = !regen.equalsIgnoreCase("false");
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
    
    public static @NotNull LoadedLocale getLanguage(){
        return instancelanguage;
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
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        time = "[WerewolfGame : "+time+"] ";

        String message = time+text;
        if(standalone){
            System.out.println(message);
        } else {
            PluginLauncher.logToOp(message);
        }
    }

    public static void logAdmin(Object title, Object text){
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        time = "[WerewolfGame : "+time+"] ";
        String message = time+title+" : "+text;
        if(standalone){
            System.out.println(message);
        } else {
            PluginLauncher.logToOp(message);
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
        if(TranslatableText.getLoadedLocales().contains(language)){
            instancelanguage = language;
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
