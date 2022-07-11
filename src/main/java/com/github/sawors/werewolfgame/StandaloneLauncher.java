package com.github.sawors.werewolfgame;

public class StandaloneLauncher {
    
    public static void main(String[] args){
        String token = "";
        if(args.length >=1){
            token = args[0];
            // > Token specified as argument as it is (imo) the safest way to store it. Maybe storing it to a file is better but I don't want to use I/O operation just for a single String
            // > Using the default config folder for storing/retrieving data is probably better in the end as the config directory file structure is predictable
        } else {
            System.out.println("Discord Token not specified, could not start the bot (specify token with the first argument when launching this jar)");
        }
    
        DiscordBot.initJDA(token, true);
    }
}
