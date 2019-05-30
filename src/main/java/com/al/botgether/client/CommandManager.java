package com.al.botgether.client;

import com.al.botgether.dto.EventDto;
import com.al.botgether.entity.Event;
import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import net.dv8tion.jda.core.entities.Message;
import org.springframework.http.HttpStatus;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

class CommandManager {
    static final Map<String, Command> commands = new HashMap<>();
    private static final Gson gson = new Gson();

    static {
        commands.put("agenda", event -> event.getChannel()
                .sendMessage("TODO")
                .queue());


        commands.put("best", event -> {
           String[] tokens = tokenize(event.getMessage());

           String response;
           if (tokens.length > 1 && tokens[1].matches("^\\d+$")) {
               HttpClient httpClient = new HttpClient();
               response = httpClient.get("/availabilities/best/" + tokens[1]);

               HttpStatus status = httpClient.getStatus();

               if (status.is2xxSuccessful()) {
                   response = "TODO" + response;
                   // TODO
               }
               else if (status.value() == 404) {
                   response = "Event not found for id : " + tokens[1];
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
         * Creates an event with a title and an optional description.
         */
        commands.put("create", event -> {
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
        commands.put("delete", event -> {
            String[] tokens = tokenize(event.getMessage());

            String response;
            if (tokens.length > 1) {
                if (tokens[1].matches("^\\d$")) {
                    HttpClient client = new HttpClient();
                    client.delete("/events/" + tokens[1]);
                    HttpStatus status = client.getStatus();
                    if (status.value() == 204) {
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
        commands.put("events", event -> {
            User user = jdaUserToAppUser(event.getAuthor());
            HttpClient client = new HttpClient();

            event.getChannel().sendMessage("TODO").queue();
        });

        /*
         * Help command.
         * Ask for a user to refine his search.
         */
        commands.put("help", event -> event.getChannel()
                .sendMessage("__Commands__\n" +
                        "\n" +
                        "__First__ please say `$register` to register yourself to the service.\n" +
                        "\n" +
                        "***agenda*** - Get the events for this week.\n" +
                        "\n" +
                        "***avail*** - `$avail <action> <event_id> <date>` adds or removes an availability to an event. `<action>` must be **add** or **remove**\n" +
                        "\tThe date must be to the format : `TODO`\n" + "***best*** - `$best <event_id>` gets the best date of an event.\n" +
                        "\n" +
                        "***close*** - `$close <event_id>` sets an event date with the date given by `$best`.\n" +
                        "\n" +
                        "***create*** - `$create <title> <description>` creates an event with a *title*, *description* is optional but recommended.\n" +
                        "\n" +
                        "***delete*** - `$delete <event_id>` delete an event.\n" +
                        "\n" +
                        "***events*** - Get the event titles and ids you're in.\n" +
                        "\n" +
                        "***update*** - `$update <event_id> <field> <new_value>` updates the title or the description of an event. `<field>` must be **title** or **description**.")
                .queue());

        /*
         * Registers a user
         */
        commands.put("register", event -> {
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
        commands.put("update", event -> {
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
                response = client.get("/events/id");

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
                        if (status.value() == 204) {
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
            return "Server error : " + status.getReasonPhrase();
        }
        if (status.is4xxClientError()) {
            return "Client error : " + status.getReasonPhrase();
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
