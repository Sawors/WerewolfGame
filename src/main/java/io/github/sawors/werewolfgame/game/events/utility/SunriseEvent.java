package io.github.sawors.werewolfgame.game.events.utility;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.PhaseType;
import io.github.sawors.werewolfgame.game.WerewolfPlayer;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.UserSnowflake;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SunriseEvent extends GameEvent {
    
    public SunriseEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
        manager.setGamePhase(GamePhase.SUNRISE);
        TranslatableText texts = new TranslatableText(getExtension().getTranslator(), manager.getLanguage());
        manager.getMainTextChannel().sendMessage(texts.getVariableText("events.story.cycles.sun-rise")).queueAfter(3, TimeUnit.SECONDS);
        manager.getMainTextChannel().sendMessage(texts.get("events.game-info.village-wakeup")).queueAfter(3+3, TimeUnit.SECONDS, m -> {
            for(Map.Entry<UserId, WerewolfPlayer> entry : manager.getPlayerRoles().entrySet()){
                try{
                    if(entry.getValue().isAlive()){
                        manager.getGuild().mute(UserSnowflake.fromId(DatabaseManager.getDiscordId(entry.getKey())), false).queue();
                    }
                } catch (IllegalArgumentException | IllegalStateException ignored){}
            }
        });
        List<UserId> death = new ArrayList<>(manager.getPendingDeath());
        if(death.size() == 0){
            manager.getMainTextChannel().sendMessage(texts.get("events.game-info.no-kill-happened")).queueAfter(3+3+3, TimeUnit.SECONDS);
        } else if(death.size() == 1){
            manager.getMainTextChannel().sendMessage(texts.get("events.game-info.single-kill-happened")).queueAfter(3+3+3, TimeUnit.SECONDS);
        } else {
            manager.getMainTextChannel().sendMessage(texts.get("events.game-info.multiple-kills-happened").replaceAll("%x%", String.valueOf(death.size()))).queueAfter(3+3+3, TimeUnit.SECONDS);
        }
        int timer = 3+3+3+2+3;
        for(int i = 0; i<death.size(); i++){
            String name = "";
            String role = "";
            UserId id = death.get(i);
            if(manager.getPlayerRoles().containsKey(id)){
                name = id.toString();
                role = manager.getPlayerRoles().get(id).getMainRole() != null ? new TranslatableText(manager.getPlayerRoles().get(id).getMainRole().getExtension().getTranslator(), manager.getLanguage()).get("roles."+manager.getPlayerRoles().get(id).getMainRole().getRoleName()+".name") : "??????";
            }
            manager.confirmDeath(id);
            timer += 1;
            manager.getMainTextChannel().sendMessage(texts.get("events.game-info.death-announcement").replaceAll("%user%", name).replaceAll("%role%", role)).queueAfter(3+3+3+2+i, TimeUnit.SECONDS);
        }
    
        if(!manager.checkForWinCondition()){
            manager.buildQueue(PhaseType.DAY);
            Executors.newSingleThreadScheduledExecutor().schedule(manager::nextEvent,timer, TimeUnit.SECONDS);
        }
        
    }
}
