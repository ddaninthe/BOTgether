package com.al.botgether.client.command;

import com.al.botgether.client.HttpStatus;
import com.al.botgether.entity.User;
import net.dv8tion.jda.core.entities.Message;

import java.util.*;

public class CommandManager {
    public static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put(AgendaCommand.COMMAND, new AgendaCommand());
        commands.put(AvailabilityCommand.COMMAND, new AvailabilityCommand());
        commands.put(BestDateCommand.COMMAND, new BestDateCommand());
        commands.put(CloseEventCommand.COMMAND, new CloseEventCommand());
        commands.put(CreateEventCommand.COMMAND, new CreateEventCommand());
        commands.put(DeleteEventCommand.COMMAND, new DeleteEventCommand());
        commands.put(ListEventCommand.COMMAND, new ListEventCommand());
        commands.put(HelpCommand.COMMAND, new HelpCommand());
        commands.put(RegisterCommand.COMMAND, new RegisterCommand());
        commands.put(UpdateEventCommand.COMMAND, new UpdateEventCommand());
    }

    static String getErrorMessage(HttpStatus status) {
        if (status.is5xxServerError()) {
            return "Server error : " + status.getErrorMessage();
        }
        if (status.is4xxClientError()) {
            return "Client error : " + status.getErrorMessage();
        }
        if (status.is2xxSuccessful()) {
            return "Success!";
        }
        return "No content";
    }

    static String[] tokenize(Message message) {
        return message.getContentDisplay().split(" ");
    }

    static User jdaUserToAppUser(net.dv8tion.jda.core.entities.User jdaUser) {
        return new User(jdaUser.getId(), jdaUser.getName(), jdaUser.getDiscriminator(), null, null);
    }
}
