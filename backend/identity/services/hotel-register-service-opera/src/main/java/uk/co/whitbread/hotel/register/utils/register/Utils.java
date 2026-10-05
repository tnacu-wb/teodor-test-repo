package uk.co.whitbread.hotel.register.utils.register;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class Utils {

  public static String sanitizeInputString(String input) {
    return input == null ?
        null : input.replace("\n", "").replace("\r", "");
  }

}
