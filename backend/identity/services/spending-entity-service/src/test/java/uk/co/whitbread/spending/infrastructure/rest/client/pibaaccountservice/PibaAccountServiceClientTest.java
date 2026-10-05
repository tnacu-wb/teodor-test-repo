package uk.co.whitbread.spending.infrastructure.rest.client.pibaaccountservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import feign.Request;
import feign.Request.HttpMethod;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.spending.infrastructure.rest.client.config.FeignErrorDecoderConfig;

class PibaAccountServiceClientTest {

  @Test
  void customErrorDecoder_WhenResponseBodyIsPresent_ThenItIsIncludedInTheExceptionMessage(){
    var responseBody = "{ \"error\":\"some error\"}";
    FeignErrorDecoderConfig config = new FeignErrorDecoderConfig();
    ErrorDecoder errorDecoder = config.errorDecoder();
    Request request = Request.create(HttpMethod.GET, "http://localhost/getTest",
          Collections.emptyMap(), null, (RequestTemplate) null);

    var result = errorDecoder.decode("getTetheredUserDetails",
          Response.builder()
                .status(500)
                .request(request)
                .body(responseBody, StandardCharsets.UTF_8).build());

    assertTrue(result.getMessage().contains(responseBody));
  }

  @Test
  void customErrorDecoder_WhenResponseBodyIsNptPresent_ThenNoExceptionIsThrown(){
    FeignErrorDecoderConfig config = new FeignErrorDecoderConfig();
    ErrorDecoder errorDecoder = config.errorDecoder();
    Request request = Request.create(HttpMethod.GET, "http://localhost/getTest",
          Collections.emptyMap(), null, (RequestTemplate) null);

    Exception getTetheredUserDetails = errorDecoder.decode("getTetheredUserDetails",
          Response.builder()
                .status(500)
                .request(request)
                .build());

    assertNotNull(getTetheredUserDetails);
  }

}
