package uk.co.whitbread.piba.registration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.registration.util.LogUtils;

@Component
@Slf4j
public class RegistrationCodeParser {

  public String parseRegistrationCode(String registrationCode) {
    String returnStr = capitalise(registrationCode.length() > 2
        ? registrationCode.substring(registrationCode.length() - 2)
        : registrationCode);
    log.info("returnStr from parse RegistrationCode - {} and registrationCode",
        LogUtils.sanitisedStringWithMaxLengthLimit(
            returnStr, 50),
        LogUtils.sanitisedStringWithMaxLengthLimit(
            registrationCode, 50));
    return returnStr;
  }

  private String capitalise(String countryCode) {
    return countryCode.toUpperCase();
  }
}
