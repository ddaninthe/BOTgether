package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.al.botgether.client.command.CommandManager.*;

/*
 * Creates an event with a title and an optional description.
 */
public class CreateEventCommand implements Command {
    static final String COMMAND = "create";

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";

    @Override
    public void execute(MessageReceivedEvent event) {
        String[] tokens = tokenize(event.getMessage());

        String response;
        if (tokens.length < 2) {
            response = "Please use `" + COMMAND_PREFIX + "create <" + TITLE + "> <" + DESCRIPTION + ">`, *" +
                    DESCRIPTION + "* is optional but recommended.";
        }
        else  {
            Gson gson = new Gson();
            User user = jdaUserToAppUser(event.getAuthor());
            String description;
            if (tokens.length == 2) {
                description = null;
            } else {
                StringBuilder descriptionBuilder = new StringBuilder();
                for (int i = 2; i < tokens.length; i++) {
                    descriptionBuilder.append(tokens[i]).append(" ");
                }
                description = descriptionBuilder.toString();
            }

            Event userEvent = new Event(0, tokens[1], description, null, user, null);

            HttpClient httpClient = new HttpClient();
            response = httpClient.post("/events", gson.toJson(EntityMapper.instance.eventToEventDto(userEvent)));
            HttpStatus status = httpClient.getStatus();

            if (status.is2xxSuccessful()) {
                EventDto createdEvent = gson.fromJson(response, EventDto.class);
                response = "Created event **" + createdEvent.getTitle() + "** with id ***" + createdEvent.getId() + "***";
            }
            else {
                response = getErrorMessage(status);
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
        return "***" + COMMAND + "*** - `" + COMMAND_PREFIX + COMMAND + " <" + TITLE + "> <" + DESCRIPTION +
                ">` creates an event with a *" + TITLE + "*, *" + DESCRIPTION + "* is optional but recommended.";
    }
}
