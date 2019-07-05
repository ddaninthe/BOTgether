package com.al.botgether.client;

import com.al.botgether.dto.AvailabilityDto;
import com.al.botgether.mapper.EntityMapper;
import com.google.gson.Gson;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.al.botgether.client.command.BestDateCommand.DATE_DISPLAY_PATTERN;

@Component
class Scheduler {
    private static JDA jda;
    private final HttpClient client;
    private final Gson gson;

    private Scheduler() {
        client = new HttpClient();
        gson = new Gson();
    }

    static void setJda(JDA jda) {
        Scheduler.jda = jda;
    }

    @Scheduled(cron = "0 0 9 * * *")
    private void alertUsers() {
        System.out.println("Called : " + jda);
        System.out.println("Port: " + client.getPort());
        if (jda == null) return;

        String response = client.get("/availabilities/today");
        HttpStatus status = client.getStatus();
        if (status.is2xxSuccessful()) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_DISPLAY_PATTERN);
            SimpleDateFormat parser = new SimpleDateFormat(EntityMapper.DATE_FORMAT);

            List<AvailabilityDto> dtos = Arrays.asList(gson.fromJson(response, AvailabilityDto[].class));

            dtos.forEach(dto -> {
                StringBuilder builder = new StringBuilder("Don't forget event **" + dto.getEventDto().getTitle() + "** ")
                        .append(" (id **").append(dto.getEventDto().getId()).append("**)");
                try {
                    Date date = parser.parse(dto.getAvailabilityDate());
                    builder.append(" on ").append(sdf.format(date));
                } catch (ParseException e) {
                    LoggerFactory.getLogger(Scheduler.class)
                            .error(String.format("Error while parsing date : %s", dto.getAvailabilityDate()), e);

                } finally {
                    User user = jda.getUserById(dto.getUserDto().getId());
                    user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(builder.toString()).queue());
                }
            });
        } else {
            LoggerFactory.getLogger(Scheduler.class).error(String.format("Error while alerting users. %s", response));
        }
    }
}
