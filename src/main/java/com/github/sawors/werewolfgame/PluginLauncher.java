package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.commands.MinecraftCommandListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class PluginLauncher extends JavaPlugin {

    static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;
        try{
            Objects.requireNonNull(getServer().getPluginCommand("ww")).setExecutor(new MinecraftCommandListener());
        } catch (NullPointerException e){
            Bukkit.getLogger().log(Level.WARNING, "One of the plugin command has not been correctly defined, please see the following stacktrace for more info");
            e.printStackTrace();
        }
        // init Config
        this.saveDefaultConfig();

        Main.init(false, getMainConfig().getString("discord-token"), this.getDataFolder());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static FileConfiguration getMainConfig(){
        return getPlugin().getConfig();
    }

    public static JavaPlugin getPlugin(){
        return instance;
    }
    
    static void logToOp(Object text){
        Bukkit.getLogger().log(Level.INFO, text.toString());
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.isOp()){
                p.sendMessage(Component.text(ChatColor.YELLOW+text.toString()));
            }
        }
    }
}
