package com.github.sawors.werewolfgame.discord;

import com.github.sawors.werewolfgame.Main;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.GuildChannel;

import javax.annotation.Nullable;
import java.util.List;

public class DiscordManager {
    public static void cleanCategory(@Nullable Category category){
        if(category == null || !category.getName().contains("WEREWOLF : ")){
            return;
        }
        String id = category.getName().replace("WEREWOLF : ", "");
        Main.logAdmin("Cleaning game "+id);
        List<GuildChannel> chans = category.getChannels();
        if(chans.size() > 0){
            for(int i = 0; i<chans.size(); i++){
                Main.unlinkChannel(chans.get(i).getIdLong());
                if(i != chans.size()-1){
                    Main.logAdmin("Deleted channel "+id+":"+chans.get(i).getName());
                    chans.get(i).delete().queue();
                } else {
                    // "a" is unused
                    Main.logAdmin("Deleted channel "+id+":"+chans.get(i).getName());
                    chans.get(i).delete().queue(a -> category.delete().queue());
                }
            }
        } else {
            category.delete().queue();
        }
    }
}