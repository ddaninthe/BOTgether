package com.al.botgether.client;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.Availability;
import com.al.botgether.entity.AvailabilityKey;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import net.dv8tion.jda.core.entities.Message;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

class CommandManager {
    private static final String DATE_FORMAT = "dd/MM/yyyy hh";

    static final Map<String, Command> commands = new HashMap<>();
    private static final Gson gson = new Gson();

    private static final class CommandList {
        private static final String WEEK_AGENDA = "agenda";
        private static final String AVAILABILITY = "avail";
        private static final String BEST_DATE = "best";
        private static final String CLOSE_EVENT = "close";
        private static final String CREATE_EVENT = "create";
        private static final String DELETE_EVENT = "delete";
        private static final String LIST_EVENTS = "events";
        private static final String HELP = "help";
        private static final String REGISTER = "register";
        private static final String UPDATE_EVENT = "update";
    }

    static {
        /*
         * Returns the list of the events title and date for this week
         */
        commands.put(CommandList.WEEK_AGENDA, event -> {
            // TODO
            String response = "TODO";

            event.getChannel().sendMessage(response).queue();
        });

        /*
         * Gets, adds or removes an availability for an event.
         */
        commands.put(CommandList.AVAILABILITY, event -> {
            String[] tokens = tokenize(event.getMessage());

            String response;
            if (tokens.length > 2) {
                if (tokens[2].matches("^\\d+$")){
                    User user = jdaUserToAppUser(event.getAuthor());
                    HttpClient client = new HttpClient();

                    if (tokens[1].equals("all")) {
                        response = client.get("/availabilities/user/" + user.getId());
                        List<AvailabilityDto> dtos = Arrays.stream(gson.fromJson(response, AvailabilityDto[].class))
                                .filter(dto -> dto.getEventDto().getId() == Long.parseLong(tokens[2]))
                                .collect(Collectors.toList());

                        if (dtos.size() > 0) {
                            StringBuilder responseBuilder = new StringBuilder("List of dates :\n");
                            for (AvailabilityDto dto : dtos) {
                                responseBuilder.append(dto.getAvailabilityDate().toString()).append("\n");
                            }
                            response = responseBuilder.toString();
                        } else {
                            response = "You have not added any availability for this event.";
                        }
                    }
                    else if (tokens.length > 4) {
                        // TODO: fix date
                        if (tokens[1].equals("add") || tokens[1].equals("remove")) {
                            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                            sdf.setTimeZone(DateUtil.UTC);
                            try {
                                Date date = sdf.parse(tokens[3] + " " + tokens[4]);

                                AvailabilityKey key = new AvailabilityKey(user.getId(), Long.parseLong(tokens[2]), date);
                                Availability availability = new Availability();
                                availability.setId(key);

                                if (tokens[1].equals("add")) {
                                    client.post("/availabilities",
                                            gson.toJson(EntityMapper.instance.availabilityToAvailabilityDto(availability)));
                                    HttpStatus status = client.getStatus();
                                    if (status.is2xxSuccessful()) {
                                        response = "Availability added!";
                                    } else {
                                        response = getErrorMessage(status);
                                    }
                                } else {
                                    client.delete("/availabilities", gson.toJson(availability));
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
                response = "Please use `$avail <action> <event_id> <date>`, **date** is mandatory if you used the **add** or **remove** action";
            }

            event.getChannel().sendMessage(response).queue();
        });

        /*
         * Returns the best date for an event.
         */
        commands.put(CommandList.BEST_DATE, event -> {
           String[] tokens = tokenize(event.getMessage());

           String response;
           if (tokens.length > 1 && tokens[1].matches("^\\d+$")) {
               HttpClient httpClient = new HttpClient();
               response = httpClient.get("/availabilities/best/" + tokens[1]);

               HttpStatus status = httpClient.getStatus();

               if (status.is2xxSuccessful()) {
                   Date bestDate = gson.fromJson(response, Date.class);
                   SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
        });

        /*
         * Sets an event to its best date
         */
        commands.put(CommandList.CLOSE_EVENT, event -> {
            String[] tokens = tokenize(event.getMessage());

            String response;
            if (tokens.length < 2) {
                response = "Please use `$close <event_id>`.";
            }
            else if (tokens[1].matches("^\\d$")) {
                User user = jdaUserToAppUser(event.getAuthor());
                HttpClient client = new HttpClient();
                response = client.get("/events/");

                if (client.getStatus().is2xxSuccessful()) {
                    String creatorId = gson.fromJson(response, EventDto.class).getCreatorDto().getId();

                    if (user.getId().equals(creatorId)) {
                        EventDto eventDto = new EventDto();
                        eventDto.setId(Long.parseLong(tokens[1]));

                        client.put("/events/" + tokens[1], gson.toJson(eventDto));
                        HttpStatus status = client.getStatus();
                        if (status.getValue() == 200) {
                            response = "Successfully updated the event";
                        } else {
                            response = getErrorMessage(status);
                        }
                    } else {
                        response = "Only the event creator can close it!";
                    }
                }
            } else {
                response = "Event id *" + tokens[1] + "* is invalid.";
            }

            event.getChannel().sendMessage(response).queue();
        });

        /*
         * Creates an event with a title and an optional description.
         */
        commands.put(CommandList.CREATE_EVENT, event -> {
            String[] tokens = tokenize(event.getMessage());

            String response;
            if (tokens.length < 2) {
                response = "Please use `$create <title> <description>`, *description* is optional but recommended.";
            }
            else  {
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
        });

        /*
         * Deletes an event
         */
        commands.put(CommandList.DELETE_EVENT, event -> {
            String[] tokens = tokenize(event.getMessage());

            String response;
            if (tokens.length > 1) {
                if (tokens[1].matches("^\\d$")) {
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
        });

        /*
         * Return the list of the events where the user is
         */
        commands.put(CommandList.LIST_EVENTS, event -> {
            User user = jdaUserToAppUser(event.getAuthor());
            HttpClient client = new HttpClient();

            String response = client.get("/availabilities/user/" + user.getId());
            HttpStatus status = client.getStatus();
            if (status.is2xxSuccessful()) {
                List<AvailabilityDto> dtos = Arrays.stream(gson.fromJson(response, AvailabilityDto[].class))
                        .sorted(Comparator.comparing(AvailabilityDto::getAvailabilityDate, Comparator.nullsLast(Comparator.reverseOrder())))
                        .collect(Collectors.toList());

                if (dtos.size() > 0) {
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
                            date = "Date not set";
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
        });

        /*
         * Help command.
         * Ask for a user to refine his search.
         */
        commands.put(CommandList.HELP, event -> event.getChannel()
                .sendMessage("__Commands__\n" +
                        "\n" +
                        "__First__ please say `$" + CommandList.REGISTER + "` to register to the bot service.\n" +
                        "\n" +
                        "***" + CommandList.WEEK_AGENDA + "*** - Get the events for this week.\n" +
                        "\n" +
                        "***" + CommandList.AVAILABILITY + "*** - `$" + CommandList.AVAILABILITY + " <action> <event_id> <date>` get all availabilities for an event or adds/removes a new one. " +
                        "`<action>` must be **all**, **add** or **remove**. `<date>` is mandatory when using **add** or **remove** and must be to the format : `" + DATE_FORMAT + "`" +
                        "\n" +
                        "***" + CommandList.BEST_DATE + "*** - `$" + CommandList.BEST_DATE + " <event_id>` gets the best date of an event.\n" +
                        "\n" +
                        "***" + CommandList.CLOSE_EVENT + "*** - `$" + CommandList.CLOSE_EVENT + " <event_id>` sets an event date with the date given by `$" + CommandList.BEST_DATE + "`.\n" +
                        "\n" +
                        "***" + CommandList.CREATE_EVENT + "*** - `$" + CommandList.CREATE_EVENT + " <title> <description>` creates an event with a *title*, *description* is optional but recommended.\n" +
                        "\n" +
                        "***" + CommandList.DELETE_EVENT + "*** - `" + CommandList.DELETE_EVENT + " <event_id>` deletes an event.\n" +
                        "\n" +
                        "***" + CommandList.LIST_EVENTS + "*** - Get the event titles and ids you're in.\n" +
                        "\n" +
                        "***" + CommandList.UPDATE_EVENT + "*** - `$" + CommandList.UPDATE_EVENT + " <event_id> <field> <new_value>` updates the title or the description of an event. `<field>` must be **title** or **description**.")
                .queue());

        /*
         * Registers a user
         */
        commands.put(CommandList.REGISTER, event -> {
            User user = jdaUserToAppUser(event.getAuthor());
            HttpClient httpClient = new HttpClient();
            httpClient.post("/users", gson.toJson(EntityMapper.instance.userToUserDto(user)));

            HttpStatus status = httpClient.getStatus();
            String response;
            if (status.is2xxSuccessful()) {
                response = "Successfully registered!";
            }
            else {
                response = getErrorMessage(status);
            }
            event.getChannel().sendMessage(response).queue();
        });
        
        /*
         * Updates an Event title or description
         */
        commands.put(CommandList.UPDATE_EVENT, event -> {
            User user = jdaUserToAppUser(event.getAuthor());

            String[] tokens = tokenize(event.getMessage());
            String response;

            if (tokens.length < 4 || (!tokens[2].equals("title") && !tokens[2].equals("description"))) {
                response = "Please use `$update <event_id> <field> <new_value>` where `<field>` must be **title** or **description**.";
            }
            else if (!tokens[1].matches("^\\d+$")) {
                response = "Event id *" + tokens[1] + "* is invalid.";
            }
            else {
                HttpClient client = new HttpClient();
                response = client.get("/events/" + tokens[1]);

                if (client.getStatus().is2xxSuccessful()) {
                    String creatorId = gson.fromJson(response, EventDto.class).getCreatorDto().getId();

                    if (user.getId().equals(creatorId)) {
                        EventDto eventDto = new EventDto();
                        eventDto.setId(Long.parseLong(tokens[1]));

                        if (tokens[2].equals("title")) {
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
                        response = "Only the event creator can modify it!";
                    }
                } else {
                    response = getErrorMessage(client.getStatus());
                }
            }

            event.getChannel().sendMessage(response).queue();
        });
    }

    private static String getErrorMessage(HttpStatus status) {
        if (status.is5xxServerError()) {
            return "Server error : " + status.getErrorMessage();
        }
        if (status.is4xxClientError()) {
            return "Client error : " + status.getErrorMessage();
        }
        if (status.is2xxSuccessful()) {
            return "Success!";
        }
        return "No content";
    }

    private static String[] tokenize(Message message) {
        return message.getContentDisplay().split(" ");
    }

    private static User jdaUserToAppUser(net.dv8tion.jda.core.entities.User jdaUser) {
        return new User(jdaUser.getId(), jdaUser.getName(), jdaUser.getDiscriminator(), null, null);
    }
}
