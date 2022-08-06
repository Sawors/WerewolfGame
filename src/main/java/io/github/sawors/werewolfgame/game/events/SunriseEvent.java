package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.GamePhase;
import io.github.sawors.werewolfgame.game.PhaseType;
import io.github.sawors.werewolfgame.game.WerewolfPlayer;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.UserSnowflake;

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

        manager.buildQueue(PhaseType.DAY);
        Executors.newSingleThreadScheduledExecutor().schedule(manager::nextEvent,3+3+1, TimeUnit.SECONDS);
        
    }
}
