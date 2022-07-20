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
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static HashMap<String, GameManager> activegames = new HashMap<>();
    private static boolean standalone;
    private static boolean discordenabled = false;
    private static boolean minecraftenabled = false;
    private static JDA jda;
    private static File datalocation;
    private static File dbfile;
    private static boolean usecaching = true;
    private static HashMap<UserId, LinkedUser> cachedusers = new HashMap<>();
    // I use this linking map to avoid creating a new JDA Event Listener each time a new GameManager is created
    // Long : Channel ID
    // String : GameManager ID
    private static HashMap<Long, String> channellink = new HashMap<>();
    // Use this to get all loaded roles
    private static Set<PlayerRole> rolepool = new HashSet<>();


    public static void init(boolean standalone, String token, File datastorage){
        Main.standalone = standalone;
        minecraftenabled = !Main.standalone && PluginLauncher.getPlugin().isEnabled();
        
        datastorage.mkdirs();
        datalocation = datastorage;
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
        TranslatableText.load(Main.class.getClassLoader().getResourceAsStream( BundledLocale.DEFAULT.getPath()), BundledLocale.DEFAULT.toString());
        File localespath = new File(datalocation+"/locales/");
        localespath.mkdirs();
        File[] toload = localespath.listFiles();
        if(toload != null){
            for(File locale : toload){
                if(locale.getName().toLowerCase(Locale.ENGLISH).endsWith(".yml") || locale.getName().toLowerCase(Locale.ENGLISH).endsWith(".yaml") ){
                    TranslatableText.load(locale);
                }
            }
        }


        Main.logAdmin("Translated : "+TranslatableText.get("invites.invite-body-customizable", BundledLocale.DEFAULT.toString()));
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
