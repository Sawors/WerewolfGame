package io.github.sawors.werewolfgame.commands;

import io.github.sawors.werewolfgame.DatabaseManager;
import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.database.UserId;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public class RegisterUserCommand implements GameCommand{

    LinkedUser toreg;

    public RegisterUserCommand(User discorduser){
        toreg = new LinkedUser(new UserId(), discorduser.getName(),null,discorduser.getId(),null,null);
    }
    public RegisterUserCommand(User discorduser, String name){
        toreg = new LinkedUser(new UserId(), name,null,discorduser.getId(),null,null);
    }

    @Override
    public void execute() {
        DatabaseManager.registerUser(toreg);
    }

    public void execute(MessageChannel channel) {
        DatabaseManager.registerUser(toreg);
        channel.sendMessage("You are now registered with UserID `"+DatabaseManager.getUserId(toreg.getDiscordId())+"`").queue();
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }
}
