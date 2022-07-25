package io.github.sawors.werewolfgame.bundledextensions.classic;

import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.bundledextensions.classic.roles.cupid.Cupid;
import io.github.sawors.werewolfgame.bundledextensions.classic.roles.hunter.Hunter;
import io.github.sawors.werewolfgame.bundledextensions.classic.roles.littlegirl.LittleGirl;
import io.github.sawors.werewolfgame.bundledextensions.classic.roles.littlegirl.LittleGirlListenEvent;
import io.github.sawors.werewolfgame.bundledextensions.classic.roles.seer.Seer;
import io.github.sawors.werewolfgame.bundledextensions.classic.roles.witch.Witch;
import io.github.sawors.werewolfgame.extensionsloader.ExtensionMetadata;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.localization.BundledLocale;

import java.io.IOException;
import java.io.InputStream;

public class ClassicExtensionLoader extends WerewolfExtension {
    
    @Override
    public void onLoad() {
        registerNewRoles(
                new Cupid(this),
                new Hunter(this),
                new LittleGirl(this),
                new Seer(this),
                new Witch(this)
        );
        
        registerBackgroundEvents(
                new LittleGirlListenEvent(this)
        );
        
        // manually load locales only for bundled extensions
        try(InputStream input = this.getClass().getModule().getResourceAsStream(this.getClass().getPackage().getName()+ ".locales.en_UK.yml")){
            getTranslator().load(input, BundledLocale.en_UK.getLocale());
            getTranslator().printLoaded();
        } catch (IOException e){e.printStackTrace();}
        this.translator = Main.getTranslator();
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
