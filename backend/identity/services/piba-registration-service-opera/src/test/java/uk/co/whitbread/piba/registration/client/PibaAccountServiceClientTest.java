package uk.co.whitbread.piba.registration.client;

import feign.Request;
import feign.Request.HttpMethod;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.piba.registration.client.PibaAccountServiceClient.PibaAccountServiceClientConfig;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PibaAccountServiceClientTest {

  @Test
  void customErrorDecoder_WhenResponseBodyIsPresent_ThenItIsIncludedInTheExceptionMessage(){
    var responseBody = "{ \"error\":\"some error\"}";
    PibaAccountServiceClientConfig pibaAccountServiceClientConfig = new PibaAccountServiceClientConfig();
    ErrorDecoder errorDecoder = pibaAccountServiceClientConfig.errorDecoder();
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
    PibaAccountServiceClientConfig pibaAccountServiceClientConfig = new PibaAccountServiceClientConfig();
    ErrorDecoder errorDecoder = pibaAccountServiceClientConfig.errorDecoder();
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