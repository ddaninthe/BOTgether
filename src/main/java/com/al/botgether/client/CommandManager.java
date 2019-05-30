package com.al.botgether.client;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class CommandManager {
    static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("agenda", event -> event.getMessage().getChannel()
                .flatMap(channel -> channel.createMessage("World!"))
                .then());
    }

    /*
     * Help command.
     * Ask for a user to refine his search.
     */
    static {
        commands.put("help", event -> event.getMessage().getChannel()
            .flatMap(channel -> channel.createMessage("Commands\n" +
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
                    "***update*** - `$update <field> <new_value>` updates the name or the description of an event. `<field>` must be **name** or **description**"))
            .then());
    }
}
