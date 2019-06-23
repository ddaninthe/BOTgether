package com.al.botgether.client;

import com.al.botgether.client.command.CommandManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotClient {
    private static final String TOKEN = "NTY1OTcxNDUxMzcxMzIzMzk0.XO-pBQ.9HAmIQuOtGgS8SgfX93CUc7Irsg";

    public static void startBot() {
        try {
            JDA jda = new JDABuilder(TOKEN).build();
            jda.addEventListener(new CommandManager());
        } catch (LoginException e) {
            LoggerFactory.getLogger(BotClient.class).error("LoginException: ", e);
        }
    }
}
