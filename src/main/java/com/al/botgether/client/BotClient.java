package com.al.botgether.client;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.Map;

public class BotClient extends ListenerAdapter {
    private static final String TOKEN = "NTY1OTcxNDUxMzcxMzIzMzk0.XO-pBQ.9HAmIQuOtGgS8SgfX93CUc7Irsg";
    private static final String COMMAND_PREFIX = "$";

    public static void startBot() {
        try {
            JDA jda = new JDABuilder(TOKEN).build();
            jda.addEventListener(new BotClient());
        } catch (LoginException e) {
            System.out.println("LoginException: " + e);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentDisplay();

        if (message.startsWith(COMMAND_PREFIX)) {
            for (Map.Entry<String, Command> entry : CommandManager.commands.entrySet()) {
                if (message.startsWith(COMMAND_PREFIX + entry.getKey())) {
                    entry.getValue().execute(event);
                    return;
                }
            }
            event.getChannel().sendMessage("Unknown command, please see supported commands with `$help`").queue();
        }
    }
}
