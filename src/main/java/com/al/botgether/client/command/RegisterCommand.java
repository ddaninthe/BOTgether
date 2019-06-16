package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.al.botgether.client.command.CommandManager.getErrorMessage;
import static com.al.botgether.client.command.CommandManager.jdaUserToAppUser;

/**
 * Registers a User.
 */
public class RegisterCommand implements Command {
    static final String COMMAND = "register";

    @Override
    public void execute(MessageReceivedEvent event) {
        User user = jdaUserToAppUser(event.getAuthor());
        HttpClient httpClient = new HttpClient();
        Gson gson = new Gson();
        httpClient.post("/users", gson.toJson(EntityMapper.instance.userToUserDto(user)));

        HttpStatus status = httpClient.getStatus();
        String response;
        if (status.is2xxSuccessful()) {
            response = "Successfully registered!";
        }
        else {
            response = getErrorMessage(status);
        }
        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - Register you to the bot service.";
    }
}
