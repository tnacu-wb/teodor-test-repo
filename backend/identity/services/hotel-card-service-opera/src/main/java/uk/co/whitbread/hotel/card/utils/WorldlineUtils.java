package uk.co.whitbread.hotel.card.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.client.worldline.model.TrustedPartnerCredentials;

@Component
@RequiredArgsConstructor
@Slf4j
public final class WorldlineUtils {

  private final ObjectMapper objectMapper;

  /**
   * Surrounds the given id with curly braces. If id is null, return null.
   * @param id - id to format
   * @return returns id formatter as {id}
   */
  public String formatId(final String id) {
    return Optional.ofNullable(id).map(val -> String.format("{%s}", val))
        .orElse(null);
  }

  public String serializeHeader(TrustedPartnerCredentials authHeader) {
    try {
      return objectMapper.writeValueAsString(authHeader);
    } catch (JsonProcessingException e) {
      log.error("Error during TrustedPartnerCredentials header serialization.", e);
      return null;
    }
  }

  public String serializeObject(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      log.error("Error during object serialization.", e);
      return null;
    }
  }

}
