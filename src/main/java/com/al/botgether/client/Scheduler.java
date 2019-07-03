package com.al.botgether.client;

import com.al.botgether.dto.AvailabilityDto;
import com.google.gson.Gson;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static com.al.botgether.client.command.BestDateCommand.DATE_DISPLAY_PATTERN;

class Scheduler {
    private final JDA jda;
    private final HttpClient client;
    private final Gson gson;

    Scheduler(JDA jda) {
        this.jda = jda;
        client = new HttpClient();
        gson = new Gson();
    }

    // Every day at 10:00 am
    //@Scheduled(cron = "0 0 10 * * * *")
    private void alertUsers() {
        System.out.println("Port: " + client.getPort());

        String response = client.get("/availabilities/today");
        HttpStatus status = client.getStatus();
        if (status.is2xxSuccessful()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_DISPLAY_PATTERN);

            List<AvailabilityDto> dtos = Arrays.asList(gson.fromJson(response, AvailabilityDto[].class));

            dtos.forEach(dto -> {
                String message = "Don't forget event " + dto.getEventDto().getTitle() + " at " +
                        sdf.format(dto.getAvailabilityDate());
                User user = jda.getUserById(dto.getUserDto().getId());
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
            });
        } else {
            LoggerFactory.getLogger(Scheduler.class).error("Error while alerting users. " + response);
        }
    }
}
