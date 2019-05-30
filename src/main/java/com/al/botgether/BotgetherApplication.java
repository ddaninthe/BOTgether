package com.al.botgether;

import com.al.botgether.client.BotClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotgetherApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotgetherApplication.class, args);

        BotClient.startBot();
    }
}
