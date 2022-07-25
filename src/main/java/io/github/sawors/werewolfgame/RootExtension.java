package io.github.sawors.werewolfgame;

import io.github.sawors.werewolfgame.extensionsloader.ExtensionMetadata;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.localization.Translator;

import java.io.File;

public class RootExtension extends WerewolfExtension {
    
    public RootExtension(Translator translator, File resourcedirectory) {
        super(translator, resourcedirectory);
    }
    
    @Override
    public void onLoad() {
    
    }
    
    @Override
    public ExtensionMetadata getMeta() {
        return new ExtensionMetadata(
                "root",
                "0.1",
                "WerewolfGame Dev Team, Sawors",
                "https://github.com/Sawors/WerewolfGame",
                "created for loading purposes"
        );
    }
}
