package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.User;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.al.botgether.client.command.CommandManager.getErrorMessage;
import static com.al.botgether.client.command.CommandManager.jdaUserToAppUser;

/**
 * Returns the list of the events where the user is
 */
@NoArgsConstructor
public class ListEventCommand implements Command {
    static final String COMMAND = "events";

    @Override
    public void execute(MessageReceivedEvent event) {
        User user = jdaUserToAppUser(event.getAuthor());
        HttpClient client = new HttpClient();

        String response = client.get("/availabilities/user/" + user.getId());
        HttpStatus status = client.getStatus();
        if (status.is2xxSuccessful()) {
            Gson gson = new Gson();
            List<AvailabilityDto> dtos = Arrays.stream(gson.fromJson(response, AvailabilityDto[].class))
                    .sorted(Comparator.comparing(AvailabilityDto::getAvailabilityDate, Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());

            if (!dtos.isEmpty()) {
                response = "A list of your events has been sent to your private messages.";

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                StringBuilder builder = new StringBuilder("__List of your events__\n");

                int i = 0;
                EventDto eventDto;
                String date;

                for (AvailabilityDto dto : dtos) {
                    eventDto = dto.getEventDto();
                    if (eventDto.getEventDate() != null) {
                        if (i++ == 10) break;
                        date = format.format(eventDto.getEventDate());
                    } else {
                        date = "Date not set.";
                    }
                    builder.append(eventDto.getTitle()).append("\t").append(date);
                }
                event.getAuthor().openPrivateChannel()
                        .queue(channel -> channel.sendMessage(builder.toString()).queue());
            }
            else {
                response = "You're not into any event.";
            }
        }
        else {
            response = getErrorMessage(status);
        }

        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - Get the event titles and ids you're in.";
    }
}
