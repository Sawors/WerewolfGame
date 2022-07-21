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
        String id = category.getName().replace("\uD83D\uDC3A WEREWOLF : ", "");
        List<GuildChannel> chans = category.getChannels();
        if(chans.size() > 0){
            for(int i = 0; i<chans.size(); i++){
                Main.unlinkChannel(chans.get(i).getIdLong());
                Main.logAdmin("Deleted channel "+id+":"+chans.get(i).getName());
                chans.get(i).delete().queue();
                if(i >= chans.size()-1){
                    Main.logAdmin("Deleted category "+category.getName());
                    category.delete().queue();
                }
            }
        }
    }
}
