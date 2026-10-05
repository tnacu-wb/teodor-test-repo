package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DateUtils.class)
public class DateUtilsTest {

  @Test
  void getDateFromString_OK() throws ParseException {

    String dateAsString = "2012-12-21 11:00:00.0";
    String pattern = "yyyy-MM-dd HH:mm:ss.S";
    Date expectedDate = new SimpleDateFormat(pattern).parse(dateAsString);

    Date date = DateUtils.getDateFromString(dateAsString, pattern);

    assertEquals(expectedDate, date);
  }

  @Test
  void getDateFromString_Throws() {
    String dateAsString = "2012-12-21 11:00:00.0";
    String badPattern = "yyyy   MM";

    assertThrows(HotelReservationException.class,
        () -> DateUtils.getDateFromString(dateAsString, badPattern));
  }
}
