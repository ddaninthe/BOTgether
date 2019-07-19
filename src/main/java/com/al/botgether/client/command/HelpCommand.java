package com.al.botgether.client.command;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Comparator;

import static com.al.botgether.client.command.CommandManager.COMMAND_PREFIX;

/**
 * Displays the list of the available commands.
 */
public class HelpCommand implements Command {
    public static final String COMMAND = "help";

    private final CommandManager manager;
    HelpCommand(CommandManager commandManager) {
        manager = commandManager;
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        StringBuilder builder = new StringBuilder("__Commands__\n\n");
        builder.append("__First__ please use `" + COMMAND_PREFIX + RegisterCommand.COMMAND +
                "` to register to the bot service.\n");

        manager.getCommands().values().stream()
                .sorted(Comparator.comparing(Command::getCommand))
                .forEach(command -> builder.append("\n").append(command.helpMessage()));

        channel.sendMessage(builder.toString()).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - Display the list of available commands.";
    }
}
