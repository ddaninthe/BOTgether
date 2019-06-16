package com.al.botgether.client.command;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Comparator;

/**
 * Displays the list of the available commands.
 */
public class HelpCommand implements Command {
    public static final String COMMAND = "help";

    @Override
    public void execute(MessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();

        StringBuilder builder = new StringBuilder("__Commands__\n");
        builder.append("__First__ please use `" + COMMAND_PREFIX + RegisterCommand.COMMAND +
                "` to register to the bot service.\n");

        CommandManager.commands.values().stream()
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
