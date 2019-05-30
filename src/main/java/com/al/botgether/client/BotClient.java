package com.al.botgether.client;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class BotClient {
    private static final String TOKEN = "NTY1OTcxNDUxMzcxMzIzMzk0.XO-pBQ.9HAmIQuOtGgS8SgfX93CUc7Irsg";
    private static final String COMMAND_PREFIX = "$";

    public static void startBot() {
        final DiscordClient client = new DiscordClientBuilder(TOKEN).build();
        client.getEventDispatcher().on(MessageCreateEvent.class)
            .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
                .flatMap(content -> Flux.fromIterable(CommandManager.commands.entrySet())
                    .filter(entry -> content.startsWith(COMMAND_PREFIX + entry.getKey()))
                    .flatMap(entry -> entry.getValue().execute(event))
                    .next()))
            .subscribe();
        client.login().block();
    }
}
