package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.EventDto;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.al.botgether.client.command.CommandManager.getErrorMessage;
import static com.al.botgether.client.command.ListEventCommand.DISPLAY_FORMAT;

/**
 * Returns the list of the events title and date for this week
 */
@NoArgsConstructor
public class AgendaCommand implements Command {
    static final String COMMAND = "agenda";

    @Override
    public void execute(MessageReceivedEvent event) {
        HttpClient client = new HttpClient();
        String response = client.get("/events/agenda/" + event.getAuthor().getId());

        HttpStatus status = client.getStatus();

        if (status.is2xxSuccessful()) {
            Gson gson = new Gson();
            List<EventDto> eventDtos = Arrays.asList(gson.fromJson(response, EventDto[].class));

            SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_FORMAT);
            StringBuilder builder = new StringBuilder("__Agenda__\n");
            eventDtos.forEach(dto -> builder.append("\n")
                    .append(dto.getTitle())
                    .append(" #")
                    .append(dto.getId())
                    .append(": ")
                    .append(sdf.format(dto.getEventDate())));

            event.getAuthor().openPrivateChannel()
                    .queue(channel -> channel.sendMessage(builder.toString()).queue());
        }
        else if (status.getValue() == 404) {
            response = "You have not any event this week.";
            event.getChannel().sendMessage(response).queue();
        }
        else {
            event.getChannel().sendMessage(getErrorMessage(status)).queue();
        }
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
