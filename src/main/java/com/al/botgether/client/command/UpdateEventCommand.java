package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.User;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import static com.al.botgether.client.command.CommandManager.*;

/**
 * Updates an Event title or description.
 */
@NoArgsConstructor
public class UpdateEventCommand implements Command {
    static final String COMMAND = "update";

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";

    @Override
    public void execute(MessageReceivedEvent event) {
        User user = jdaUserToAppUser(event.getAuthor());

        String[] tokens = tokenize(event.getMessage());
        String response;

        if (tokens.length < 4 || (!TITLE.equals(tokens[2]) && !DESCRIPTION.equals(tokens[2]))) {
            response = "Please use `" + COMMAND_PREFIX + "update <event_id> <field> <new_value>` where `<field>` must be **" +
                    TITLE + "** or **" + DESCRIPTION + "**.";
        }
        else if (!StringUtils.isNumeric(tokens[1])) {
            response = "Event id *" + tokens[1] + "* is invalid.";
        }
        else {
            HttpClient client = new HttpClient();
            response = client.get("/events/" + tokens[1]);

            if (client.getStatus().is2xxSuccessful()) {
                Gson gson = new Gson();
                String creatorId = gson.fromJson(response, EventDto.class).getCreatorDto().getId();

                if (user.getId().equals(creatorId)) {
                    EventDto eventDto = new EventDto();
                    eventDto.setId(Long.parseLong(tokens[1]));

                    if (tokens[2].equals(TITLE)) {
                        eventDto.setTitle(tokens[3]);
                    } else {
                        StringBuilder newDescription = new StringBuilder();
                        for (int i = 3; i < tokens.length; i++) {
                            newDescription.append(tokens[i]).append(" ");
                        }
                        eventDto.setDescription(newDescription.toString());
                    }

                    client.put("/events/" + tokens[1], gson.toJson(eventDto));
                    HttpStatus status = client.getStatus();
                    if (status.getValue() == 204) {
                        response = "Successfully updated the event";
                    } else {
                        response = getErrorMessage(status);
                    }
                } else {
                    response = "Only the event owner can modify it!";
                }
            } else {
                response = getErrorMessage(client.getStatus());
            }
        }

        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return  "***" + COMMAND + "*** - `" + COMMAND_PREFIX + COMMAND + " <event_id> <field> <new_value>` " +
                "updates the title or the description of an event. `<field>` must be **" +
                TITLE + "** or **" + DESCRIPTION + "**.";
    }
}
