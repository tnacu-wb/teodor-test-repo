package uk.co.whitbread.hotel.account.service.worldline.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.hotel.account.client.worldline.model.TrustedPartnerCredentials;

@Slf4j
@UtilityClass
public class WorldlineUtils {
  public static String serializeHeader(TrustedPartnerCredentials authHeader) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(authHeader);
    } catch (JsonProcessingException e) {
      log.error("Error during TrustedPartnerCredentials header serialization.", e);
      return null;
    }
  }
}
