package com.al.botgether.client;

import com.al.botgether.client.command.AvailabilityCommand;
import com.al.botgether.client.command.CommandManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.ap.shaded.freemarker.template.utility.DateUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandManagerTest {
    @Test
    public void should_parse_date() throws ParseException {
        String dateToParse = "01/01/2019 14";
        SimpleDateFormat sdf = new SimpleDateFormat(AvailabilityCommand.DATE_FORMAT);
        sdf.setTimeZone(DateUtil.UTC);
        Date date = sdf.parse(dateToParse);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        format.setTimeZone(DateUtil.UTC);
        Date expectedDate = format.parse("2019-01-01 14");

        assertThat(date).isNotNull();
        assertThat(date.getTime()).isEqualTo(expectedDate.getTime());
    }

    @Test
    public void should_get_date_from_avail_command() throws ParseException {
        String avail = "$avail add 100 01/01/2019 14";
        String[] tokens = avail.split(" ");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH");
        sdf.setTimeZone(DateUtil.UTC);
        Date date = sdf.parse(tokens[3] + " " + tokens[4]);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(DateUtil.UTC);
        Date expectedDate = format.parse("2019-01-01 14:00:00");

        assertThat(date).isNotNull();
        assertThat(date.getTime()).isEqualTo(expectedDate.getTime());
    }
}
