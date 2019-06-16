package com.al.botgether.client.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {
    String COMMAND_PREFIX = "$";

    void execute(MessageReceivedEvent event);
    String getCommand();
    String helpMessage();
}
