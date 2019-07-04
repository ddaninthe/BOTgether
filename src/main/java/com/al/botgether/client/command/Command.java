package com.al.botgether.client.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

interface Command {
    void execute(MessageReceivedEvent event);
    String getCommand();
    String helpMessage();
}
