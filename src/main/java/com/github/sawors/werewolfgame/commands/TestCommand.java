package com.github.sawors.werewolfgame.commands;

import com.github.sawors.werewolfgame.Main;
import com.github.sawors.werewolfgame.database.UserId;
import net.dv8tion.jda.api.entities.Message;

public class TestCommand implements GameCommand{
    @Override
    public void execute() {
        Main.logAdmin("Player IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(new UserId());
        }
        Main.logAdmin("\nGame IDs");
        for(int i = 0; i<=8; i++){
            Main.logAdmin(Main.generateRandomGameId());
        }
    }
    
    public void execute(Message msg) {
        /*MessageChannel chan = msg.getChannel();
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor("Game invitation")
                .setTitle(
                " souhaite organiser une partie le **20/10/2020** pour le jeu **AmongUs** à partir de **21h00**\n" +
                        "\n" +
                        "on va essayer après le flop de 17h\n" +
                        "\n" +
                        "[ 20/10/2020 | @AmongUs | 21h00 ]\n" +
                        "\n" +
                        "\n"
        )
        .setColor(0xe0afd7)
        .addField("Rules", "Never gonna give you up",  false)
        .addField("Join","si vous voulez indiquer que vous pourrez participer cliquez sur l'emoji \" :ok_hand: \" sous ce message, si vous n'êtes pas sur, cliquez sur l'emoji \" :thinking: \" sous ce message, si vous ne pouvez pas, ajoutez \":x: \" en réaction",false)
        .setThumbnail("https://sypahwellness.com/wp-content/uploads/2019/08/Les_Loups_Garous.png")
        .setFooter("click on Join Game to join")
        .setTimestamp(LocalDateTime.now())
        ;
        
        chan.sendMessageEmbeds(embed.build()).queue();*/
    }
    
    @Override
    public boolean isAdminOnly() {
        return true;
    }
}
