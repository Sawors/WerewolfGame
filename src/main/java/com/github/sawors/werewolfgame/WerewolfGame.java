package com.github.sawors.werewolfgame;

import net.dv8tion.jda.api.JDA;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public final class WerewolfGame extends JavaPlugin {

    static JavaPlugin instance;
    @Override
    public void onEnable() {
        instance = this;

        // init Config
        this.saveDefaultConfig();

        // init JDA (Discord Bot)
        JDA jda = DiscordBot.initJDA();
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

    public static void logAdmin(Object txt){
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        time = "[WerewolfGame : "+time+"] ";

        Bukkit.getLogger().log(Level.INFO, time+txt.toString());
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.isOp()){
                p.sendMessage(Component.text(ChatColor.YELLOW+time+txt));
            }
        }
    }
}
