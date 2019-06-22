package com.al.botgether.client.command;

import com.al.botgether.client.HttpStatus;
import com.al.botgether.entity.User;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

public class CommandManager extends ListenerAdapter {
    static final String COMMAND_PREFIX = "$";

    private final Map<String, Command> commands;

    public CommandManager() {
        commands = new HashMap<>();
        commands.put(AgendaCommand.COMMAND, new AgendaCommand());
        commands.put(AvailabilityCommand.COMMAND, new AvailabilityCommand());
        commands.put(BestDateCommand.COMMAND, new BestDateCommand());
        commands.put(CloseEventCommand.COMMAND, new CloseEventCommand());
        commands.put(CreateEventCommand.COMMAND, new CreateEventCommand());
        commands.put(DeleteEventCommand.COMMAND, new DeleteEventCommand());
        commands.put(ListEventCommand.COMMAND, new ListEventCommand());
        commands.put(HelpCommand.COMMAND, new HelpCommand(this));
        commands.put(RegisterCommand.COMMAND, new RegisterCommand());
        commands.put(UpdateEventCommand.COMMAND, new UpdateEventCommand());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();

        if (message.startsWith(COMMAND_PREFIX)) {
            for (Map.Entry<String, Command> entry : commands.entrySet()) {
                if (message.startsWith(COMMAND_PREFIX + entry.getKey())) {
                    entry.getValue().execute(event);
                    return;
                }
            }
            event.getChannel().sendMessage("Unknown command, please see supported commands with `" +
                    COMMAND_PREFIX + HelpCommand.COMMAND + "`").queue();
        }
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

    Map<String, Command> getCommands() {
        return commands;
    }
}
