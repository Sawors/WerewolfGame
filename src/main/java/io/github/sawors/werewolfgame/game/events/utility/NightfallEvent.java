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

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NightfallEvent extends GameEvent {
    
    public NightfallEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
        manager.setGamePhase(GamePhase.NIGHT_PREWOLVES);
        TranslatableText texts = new TranslatableText(getExtension().getTranslator(), manager.getLanguage());
        manager.getMainTextChannel().sendMessage(texts.get("events.story.night-fall")).queueAfter(3, TimeUnit.SECONDS);
        manager.getMainTextChannel().sendMessage(texts.get("events.village-sleep")).queueAfter(3+2, TimeUnit.SECONDS, m -> {
            for(Map.Entry<UserId, WerewolfPlayer> entry : manager.getPlayerRoles().entrySet()){
                try{
                    if(entry.getValue().isAlive()){
                        manager.getGuild().mute(UserSnowflake.fromId(DatabaseManager.getDiscordId(entry.getKey())), true).queue();
                    }
                } catch (IllegalArgumentException | IllegalStateException ignored){}
            }
        });
        manager.buildQueue(PhaseType.NIGHT);
        
        Executors.newSingleThreadScheduledExecutor().schedule(manager::nextEvent,3+2+2,TimeUnit.SECONDS);
    }
}
