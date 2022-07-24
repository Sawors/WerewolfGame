package com.github.sawors.werewolfgame.bundledextensions.classic;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.cupid.Cupid;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.hunter.Hunter;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.littlegirl.LittleGirl;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.lover.Lover;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.seer.Seer;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.witch.Witch;
import com.github.sawors.werewolfgame.extensionsloader.ExtensionMetadata;
import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;

public class ClassicExtensionLoader extends WerewolfExtension {
    
    
    public ClassicExtensionLoader(Main loader) {
        super(loader);
    }
    
    @Override
    public void onLoad() {
        registerNewRoles(
                new Cupid(),
                new Hunter(),
                new LittleGirl(),
                new Lover(),
                new Seer(),
                new Witch()
        );
    }
    
    @Override
    public ExtensionMetadata getMeta() {
        return new ExtensionMetadata(
                "Classic Role Pack",
                "0.1",
                "WerewolfGame Development Team, Sawors",
                "https://github.com/Sawors/WerewolfGame",
                "The default extension pack for WerewolfGame containing all the base roles"
        );
    }
}
