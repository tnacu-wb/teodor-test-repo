package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.TrustedPartnerCredentialsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;

@Slf4j
@UtilityClass
public class WorldlineUtils {

  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";
  private static final String COMPANY_NUMBER = "CompanyNumber";
  private static final String TRUSTED_PARTNER_CREDENTIALS = "TrustedPartnerCredentials";
  private static final String CULTURE_CODE = "CultureCode";
  private static final String IP_ADDRESS = "IPAddress";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static String serializeHeader(TrustedPartnerCredentialsDto authHeader) {
    try {
      return OBJECT_MAPPER.writeValueAsString(authHeader);
    } catch (JsonProcessingException e) {
      log.error("Error during TrustedPartnerCredentials header serialization.", e);
      return null;
    }
  }

  public static Consumer<HttpHeaders> setWorldlineHeaders(
      WorldlineHeadersDto worldlineHeadersDto) {
    return httpHeaders -> {
      httpHeaders.add(CONTENT_TYPE, APPLICATION_JSON);
      httpHeaders.add(COMPANY_NUMBER, worldlineHeadersDto
          .getCompanyNumber().toString());
      httpHeaders.add(TRUSTED_PARTNER_CREDENTIALS, serializeHeader(
          worldlineHeadersDto
              .getTrustedPartnerCredentialsDto()));
      httpHeaders.add(CULTURE_CODE, worldlineHeadersDto.getCultureCode());
      httpHeaders.add(IP_ADDRESS, worldlineHeadersDto.getIpAddress());
    };
  }

}
