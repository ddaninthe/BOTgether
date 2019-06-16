package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import static com.al.botgether.client.command.CommandManager.getErrorMessage;
import static com.al.botgether.client.command.CommandManager.tokenize;

/**
 * Deletes an event.
 */
@NoArgsConstructor
public class DeleteEventCommand implements Command {
    static final String COMMAND = "delete";

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] tokens = tokenize(event.getMessage());

        String response;
        if (tokens.length > 1) {
            if (StringUtils.isNumeric(tokens[1])) {
                HttpClient client = new HttpClient();
                client.delete("/events/" + tokens[1], null);
                HttpStatus status = client.getStatus();
                if (status.getValue() == 204) {
                    response = "Event deleted!";
                }
                else {
                    response = getErrorMessage(status);
                }
            }
            else {
                response = "Event id *" + tokens[1] + "* is invalid.";
            }
        } else {
            response = "Missing `<event_id>`.";
        }

        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - `" + COMMAND_PREFIX + COMMAND + " <event_id>` deletes an event.";
    }
}
