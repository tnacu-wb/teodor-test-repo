package uk.co.whitbread.cdh.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import uk.co.whitbread.cdh.infrastructure.rest.client.properties.UriHeaders;
import uk.co.whitbread.cdh.infrastructure.rest.client.util.RequestUtils;

class RequestUtilsTest {

  @Test
  void testBuildHeaders() {
    HttpHeaders httpHeaders = RequestUtils.buildHeaders("subscriptionKey", "accessedBy");
    assertEquals("subscriptionKey", httpHeaders.get(UriHeaders.SUBSCRIPTION_KEY_HEADER_NAME).get(0));
  }

  @Test
  void testBuildHeadersWithContext() {
    HttpHeaders httpHeaders = RequestUtils.buildHeaders("subscriptionKey", "accessedBy", "accessContext");
    assertEquals("subscriptionKey", httpHeaders.get(UriHeaders.SUBSCRIPTION_KEY_HEADER_NAME).get(0));
  }

  @Test
  void testBuildHeadersWithParamsToMap() {
    HashMap<String, String> queryParams = new HashMap<>();
    queryParams.put("subscriptionKey", "subscriptionKey");
    MultiValueMap<String, String> subscriptionKey = RequestUtils.queryParamsToMap(queryParams);
    assertEquals("subscriptionKey", subscriptionKey.get("subscriptionKey").get(0));
  }



}
