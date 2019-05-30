package com.al.botgether.client;

import com.al.botgether.entity.User;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

class CommandManager {
    static final Map<String, Command> commands = new HashMap<>();
    private static final Gson gson = new Gson();

    static {
        commands.put("agenda", event -> event.getChannel()
                .sendMessage("TODO")
                .queue());

        /*
         * Help command.
         * Ask for a user to refine his search.
         */
        commands.put("help", event -> event.getChannel()
                .sendMessage("Commands\n" +
                    "\n" +
                    "__First__ please say `$register` to register yourself to the service.\n" +
                    "\n" +
                    "***create*** - Create an event.\n" +
                    "***avail*** - `$avail <action> <event_id> <date>` adds or removes an availability to an event. `<action>` must be **add** or **remove**\n" +
                    "\tThe date must be to the format : `TODO`\n" +
                    "***event*** - Get the event names and ids you're in.\n" +
                    "***agenda*** - Get the events for this week.\n" +
                    "***best*** - `$best <event_id>` gets the best date of an event.\n" +
                    "***close*** - `$close <event_id>` sets an event date with the date given by `$best`.\n" +
                    "***update*** - `$update <field> <new_value>` updates the name or the description of an event. `<field>` must be **name** or **description**")
                .queue());

        /*
         * Registers a user
         */
        commands.put("register", event -> {
            User author = jdaUserToAppUser(event.getAuthor());
            HttpClient httpClient = new HttpClient();
            httpClient.post("/users", gson.toJson(EntityMapper.instance.userToUserDto(author)));

            HttpStatus status = httpClient.getStatus();
            String response = "No content";
            if (status.is2xxSuccessful()) {
                response = "Successfully registered!";
            }
            else if (status.is5xxServerError()) {
                response = "Server error: " + status.getReasonPhrase();
            }
            event.getChannel().sendMessage(response).queue();
        });
    }

    private static User jdaUserToAppUser(net.dv8tion.jda.core.entities.User jdaUser) {
        return new User(jdaUser.getId(), jdaUser.getName(), jdaUser.getDiscriminator(), null, null);
    }
}
