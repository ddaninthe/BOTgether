package com.al.botgether;

import com.al.botgether.client.BotClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BotgetherApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotgetherApplication.class, args);

        BotClient.startBot();
    }
}
