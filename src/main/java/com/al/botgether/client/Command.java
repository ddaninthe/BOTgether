package com.al.botgether.client;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

interface Command {
    void execute(MessageReceivedEvent event);
}
