package com.al.botgether.client.command;

import com.al.botgether.client.HttpClient;
import com.al.botgether.client.HttpStatus;
import com.al.botgether.dto.QuoteDto;
import com.google.gson.Gson;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class QuoteCommand implements Command {
    static final String COMMAND = "quote";

    @Override
    public void execute(MessageReceivedEvent event) {
        HttpClient client = new HttpClient();
        String response = client.get("/quotes");

        HttpStatus status = client.getStatus();

        if (status.is2xxSuccessful()) {
            Gson gson = new Gson();
            QuoteDto quote = gson.fromJson(response, QuoteDto.class);

            response = "*" + quote.getQuote() + "*\n" +
                    "- " + quote.getAuthor();
        } else {
            response = status.getErrorMessage();
        }

        event.getChannel().sendMessage(response).queue();
    }

    @Override
    public String getCommand() {
        return COMMAND;
    }

    @Override
    public String helpMessage() {
        return "***" + COMMAND + "*** - Display the quote of the day.";
    }
}
