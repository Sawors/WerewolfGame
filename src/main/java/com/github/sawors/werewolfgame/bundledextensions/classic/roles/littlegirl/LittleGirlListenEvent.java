package com.github.sawors.werewolfgame.bundledextensions.classic.roles.littlegirl;

import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.game.GameManager;
import com.github.sawors.werewolfgame.game.events.BackgroundEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

public class LittleGirlListenEvent extends BackgroundEvent {
    public LittleGirlListenEvent(WerewolfExtension extension) {
        super(extension);
    }
    
    @Override
    public void initialize(GameManager manager) {
    
    }
    
    @Override
    public void onMessageSent(GenericMessageEvent event) {
        super.onMessageSent(event);
    }
}
