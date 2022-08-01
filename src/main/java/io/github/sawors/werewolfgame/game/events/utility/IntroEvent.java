package io.github.sawors.werewolfgame.game.events.utility;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.game.events.GameEvent;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IntroEvent extends GameEvent {
    
    public IntroEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void start(GameManager manager) {
        TextChannel chan = manager.getMainTextChannel();
        TranslatableText texts = new TranslatableText(extension.getTranslator(), manager.getLanguage());
        chan.sendTyping().queue();
        chan.sendMessage(texts.get("events.story.1")).queueAfter(6, TimeUnit.SECONDS, m -> chan.sendTyping().queue());
        chan.sendMessage(texts.get("events.story.2")).queueAfter(6+4, TimeUnit.SECONDS, m -> chan.sendTyping().queue());
        chan.sendMessage(texts.get("events.story.3")).queueAfter(6+4+10, TimeUnit.SECONDS, m -> chan.sendTyping().queue());
        chan.sendMessage(texts.get("events.story.4")).queueAfter(6+4+10+8, TimeUnit.SECONDS, m -> chan.sendTyping().queue());
        Executors.newSingleThreadScheduledExecutor().schedule(manager::nextEvent,6+4+10+8+4,TimeUnit.SECONDS);
    }
}
