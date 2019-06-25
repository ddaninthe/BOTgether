package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.User;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.al.botgether.client.command.BestDateCommand.DATE_DISPLAY_PATTERN;
import static com.al.botgether.client.command.CommandManager.*;

/**
 * Sets an event to its best date.
 */
@NoArgsConstructor
public class CloseEventCommand implements Command {
    static final String COMMAND = "close";

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] tokens = tokenize(event.getMessage());

        String response;
        if (tokens.length < 2) {
            response = "Please use `" + COMMAND_PREFIX + "close <event_id>`.";
        }
        else if (StringUtils.isNumeric(tokens[1])) {
            User user = jdaUserToAppUser(event.getAuthor());
            HttpClient client = new HttpClient();
            response = client.get("/events/" + tokens[1]);

            HttpStatus status = client.getStatus();
            if (status.is2xxSuccessful()) {
                Gson gson = new Gson();
                String creatorId = gson.fromJson(response, EventDto.class).getCreatorDto().getId();

                if (user.getId().equals(creatorId)) {
                    EventDto eventDto = new EventDto();
                    eventDto.setId(Long.parseLong(tokens[1]));

                    response = client.put("/events/" + tokens[1] + "/best", null);
                    status = client.getStatus();
                    if (status.getValue() == 200) {
                        Date bestDate = gson.fromJson(response, Date.class);
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_DISPLAY_PATTERN);
                        response = "Event " + tokens[1] + " date has been set to " + sdf.format(bestDate);
                    } else {
                        response = getErrorMessage(status);
                    }
                } else {
                    response = "Only the event creator can close it!";
                }
            } else {
                response = getErrorMessage(status);
            }
        } else {
            response = "Event id *" + tokens[1] + "* is invalid.";
        }
        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - `" + COMMAND_PREFIX + COMMAND + " <event_id>` sets an " +
                "event date with the date given by `" + COMMAND_PREFIX + BestDateCommand.COMMAND + "`.";
    }
}
