package com.al.botgether.client.command;

import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Returns the list of the events title and date for this week
 */
@NoArgsConstructor
public class AgendaCommand implements Command {
    static final String COMMAND = "agenda";

    @Override
    public void execute(MessageReceivedEvent event) {
        // TODO
        event.getChannel().sendMessage("TODO").queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - Get the events for this week.";
    }
}
