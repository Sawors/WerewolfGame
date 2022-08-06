package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.localization.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DeathValidateEvent extends GameEvent {

    boolean foreshadowing = true;

    public DeathValidateEvent(WerewolfExtension extension, boolean foreshadowing) {
        super(extension);
        this.foreshadowing = foreshadowing;
    }
    
    @Override
    public void start(GameManager manager) {
        List<UserId> death = new ArrayList<>(manager.getPendingDeath());
        TranslatableText texts = new TranslatableText(getExtension().getTranslator(), manager.getLanguage());
        if(foreshadowing){
            if(death.size() == 0){
                manager.getMainTextChannel().sendMessage(texts.get("events.game-info.no-kill-happened")).queueAfter(0, TimeUnit.SECONDS);
            } else if(death.size() == 1){
                manager.getMainTextChannel().sendMessage(texts.get("events.game-info.single-kill-happened")).queueAfter(0, TimeUnit.SECONDS);
            } else {
                manager.getMainTextChannel().sendMessage(texts.get("events.game-info.multiple-kills-happened").replaceAll("%x%", String.valueOf(death.size()))).queueAfter(0, TimeUnit.SECONDS);
            }
        }

        int timer = 2;
        for(int i = 0; i<death.size(); i++){
            String name = "";
            String role = "";
            UserId id = death.get(i);
            if(manager.getPlayerRoles().containsKey(id)){
                name = id.toString();
                role = manager.getPlayerRoles().get(id).getMainRole() != null ? new TranslatableText(manager.getPlayerRoles().get(id).getMainRole().getExtension().getTranslator(), manager.getLanguage()).get("roles."+manager.getPlayerRoles().get(id).getMainRole().getRoleName()+".name") : "??????";
            }
            timer += 1;
            if(i < death.size()-1){
                manager.getMainTextChannel().sendMessage(texts.get("events.game-info.death-announcement").replaceAll("%user%", name).replaceAll("%role%", role)).queueAfter(2+i, TimeUnit.SECONDS, u -> {
                    if(!manager.checkForWinCondition()){
                        manager.confirmDeath(id);
                    }
                });
            } else {
                manager.getMainTextChannel().sendMessage(texts.get("events.game-info.death-announcement").replaceAll("%user%", name).replaceAll("%role%", role)).queueAfter(2+i, TimeUnit.SECONDS, u -> {
                    if(!manager.checkForWinCondition()){
                        manager.confirmDeath(id);
                    }
                });
            }
        }

        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
           if(!this.isDisabled()){
               manager.nextEvent();
           }
        },timer+2, TimeUnit.SECONDS);

    }
}
