package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.al.botgether.client.command.CommandManager.*;

/**
 * Gets, adds or removes an availability for an event.
 */
@NoArgsConstructor
public class AvailabilityCommand implements Command {
    static final String COMMAND = "avail";

    public static final String DATE_FORMAT = "dd/MM/yyyy hh";
    private static final String ACTION_ALL = "all";
    private static final String ACTION_ADD = "add";
    private static final String ACTION_REMOVE = "remove";

    @Override
    public void execute(MessageReceivedEvent event) {
        Gson gson = new Gson();

        String[] tokens = tokenize(event.getMessage());

        String response;
        if (tokens.length > 2) {
            if (StringUtils.isNumeric(tokens[2])){
                User user = jdaUserToAppUser(event.getAuthor());
                HttpClient client = new HttpClient();

                if (ACTION_ALL.equals(tokens[1])) {
                    response = client.get("/availabilities/user/" + user.getId());
                    List<AvailabilityDto> dtos = Arrays.stream(gson.fromJson(response, AvailabilityDto[].class))
                            .filter(dto -> dto.getEventDto().getId() == Long.parseLong(tokens[2]))
                            .collect(Collectors.toList());

                    if (!dtos.isEmpty()) {
                        StringBuilder responseBuilder = new StringBuilder("__List of dates__\n");
                        for (AvailabilityDto dto : dtos) {
                            responseBuilder.append(dto.getAvailabilityDate()).append("\n");
                        }
                        response = responseBuilder.toString();
                    } else {
                        response = "You have not added any availability for this event.";
                    }
                }
                else if (tokens.length > 4) {
                    if (ACTION_ADD.equals(tokens[1]) || ACTION_REMOVE.equals(tokens[1])) {
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                        try {
                            Date date = sdf.parse(tokens[3] + " " + tokens[4]);
                            AvailabilityKey key = new AvailabilityKey(user.getId(), Long.parseLong(tokens[2]), date);
                            Availability availability = new Availability();
                            availability.setId(key);

                            if (ACTION_ADD.equals(tokens[1])) {
                                client.post("/availabilities",
                                        gson.toJson(EntityMapper.instance.availabilityToAvailabilityDto(availability)));
                                HttpStatus status = client.getStatus();
                                if (status.is2xxSuccessful()) {
                                    response = "Availability added!";
                                } else {
                                    response = getErrorMessage(status);
                                }
                            } else {
                                client.delete("/availabilities",
                                        gson.toJson(EntityMapper.instance.availabilityToAvailabilityDto(availability)));
                                HttpStatus status = client.getStatus();
                                if (status.getValue() == 204) {
                                    response = "Availability removed!";
                                } else {
                                    response = getErrorMessage(status);
                                }
                            }
                        } catch (ParseException e) {
                            response = "Invalid date, format must be `" + DATE_FORMAT + "`.";
                        }
                    } else {
                        response = "Invalid action : *" + tokens[1] + "*";
                    }
                }
                else {
                    response = "You must provide a *date* with format `" + DATE_FORMAT + "`.";
                }
            } else {
                response = "Event id *" + tokens[2] + "* is invalid.";
            }
        } else {
            response = "Please use `" + COMMAND_PREFIX + "avail <action> <event_id> <date>`, **date** is mandatory " +
                    "if you used the **" + ACTION_ADD + "** or **" + ACTION_REMOVE + "** action";
        }

        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - `" + COMMAND_PREFIX + COMMAND + " <action> <event_id> <date>` get all " +
                "availabilities for an event or adds/removes a new one. `<action>` must be **" + ACTION_ALL +
                "**, **" + ACTION_ADD + "** or **" + ACTION_REMOVE + "**. `<date>` is mandatory when using **" +
                ACTION_ADD + "** or **" + ACTION_REMOVE + "** and must be to the format : `" + DATE_FORMAT + "`";
    }
}
