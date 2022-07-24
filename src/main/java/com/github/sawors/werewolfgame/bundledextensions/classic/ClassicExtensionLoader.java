package com.github.sawors.werewolfgame.bundledextensions.classic;

import com.github.sawors.werewolfgame.bundledextensions.classic.roles.cupid.Cupid;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.hunter.Hunter;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.littlegirl.LittleGirl;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.lover.Lover;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.seer.Seer;
import com.github.sawors.werewolfgame.bundledextensions.classic.roles.witch.Witch;
import com.github.sawors.werewolfgame.extensionsloader.ExtensionMetadata;
import com.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import com.github.sawors.werewolfgame.localization.BundledLocale;

import java.io.IOException;
import java.io.InputStream;

public class ClassicExtensionLoader extends WerewolfExtension {
    
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
        
        // manually load locales only for bundled extensions
        try(InputStream input = this.getClass().getModule().getResourceAsStream("bundledextensions.classic.locales.en_UK.yml")){
            getTranslator().load(input, BundledLocale.en_UK.getLocale());
            getTranslator().printLoaded();
        } catch (IOException e){e.printStackTrace();}
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
