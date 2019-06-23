package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.al.botgether.client.command.CommandManager.*;

/**
 * Returns the best date for an event.
 */
@NoArgsConstructor
public class BestDateCommand implements Command {
    static final String COMMAND = "best";

    static final String DATE_DISPLAY_PATTERN = "dd/MM/yyyy HH:mm";

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] tokens = tokenize(event.getMessage());

        String response;
        if (tokens.length > 1 && StringUtils.isNumeric(tokens[1])) {
            HttpClient httpClient = new HttpClient();
            response = httpClient.get("/availabilities/best/" + tokens[1]);

            HttpStatus status = httpClient.getStatus();

            if (status.is2xxSuccessful()) {
                Gson gson = new Gson();
                Date bestDate = gson.fromJson(response, Date.class);
                SimpleDateFormat format = new SimpleDateFormat(DATE_DISPLAY_PATTERN);
                response = format.format(bestDate);
            }
            else if (status.getValue() == 404) {
                response = "No availability found for event **" + tokens[1] + "**.";
            }
            else {
                response = getErrorMessage(status);
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
        return "***" + COMMAND + "*** - `" + COMMAND_PREFIX + COMMAND + " <event_id>` gets the best date of an event.";
    }
}
