package uk.co.whitbread.piba.registration.client;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.piba.registration.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.registration.model.TetheredUserRequest;

import java.nio.charset.StandardCharsets;

@FeignClient(
     value = "${config.service.piba-account-service.name:pibaaccountserviceclient}",
     url = "${config.service.piba-account-service.host}",
     configuration = PibaAccountServiceClient.PibaAccountServiceClientConfig.class)
public interface PibaAccountServiceClient {
  @PostMapping(value = "${config.service.piba-account-service.registerTetheredUserEndpoint}",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  void registerTetheredUser(@RequestHeader("Authorization") String authorization,
      @RequestBody TetheredUserRequest tetheredUserRequest);

  @GetMapping(value = "/piba/account/tethereduserdetail/{tetheredGuid}",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  TetheredUserDetailsResponse getTetheredUserDetails(@RequestHeader("Authorization") String authorization,
      @PathVariable("tetheredGuid") String tetheredGuid);

  @GetMapping(value = "/piba/account/tethereduserdetail/{tetheredGuid}/{scheme}",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  TetheredUserDetailsResponse getTetheredUserDetailsWithScheme(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("tetheredGuid") String tetheredGuid, @PathVariable("scheme") String scheme);

  @Slf4j
  @Configuration
  class PibaAccountServiceClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
      return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {

      private final ErrorDecoder defaultErrorDecoder = new Default();

      @Override
      public Exception decode(String methodKey, Response response) {
        try {
          String responseBody = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
          log.error("Error occurred while calling {}: status code {}, reason {}, response: {}.",
                methodKey, response.status(), response.reason(), responseBody);
        } catch (Exception e) {
          log.error("Error occurred while calling {}: status code {}, reason {}",
                methodKey, response.status(), response.reason());
        }

        var result = defaultErrorDecoder.decode(methodKey, response);
        log.error(result.getMessage(), result);

        return result;
      }
    }
  }
}
