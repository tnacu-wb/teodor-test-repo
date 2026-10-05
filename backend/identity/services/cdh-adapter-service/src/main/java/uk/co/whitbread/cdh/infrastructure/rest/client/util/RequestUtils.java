package uk.co.whitbread.cdh.infrastructure.rest.client.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.cdh.infrastructure.rest.client.properties.UriHeaders;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.CdhHeaders;

@UtilityClass
public class RequestUtils {

  public static <T> MultiValueMap<String, String> queryParamsToMap(T queryParams) {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> queryParamsMap = mapper.convertValue(queryParams, new TypeReference<>() {});
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    multiValueMap.setAll(queryParamsMap);
    return multiValueMap;
  }

  public static HttpHeaders buildHeaders(String subscriptionKey, String accessedBy) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(UriHeaders.SUBSCRIPTION_KEY_HEADER_NAME, subscriptionKey);
    headers.add(CdhHeaders.ACCESSED_BY.getHeader(), accessedBy);
    return headers;
  }

  public static HttpHeaders buildHeaders(String subscriptionKey, String accessedBy, String accessContext) {
    HttpHeaders headers = buildHeaders(subscriptionKey, accessedBy);
    headers.add(CdhHeaders.ACCESS_CONTEXT.getHeader(), accessContext);
    return headers;
  }
}
