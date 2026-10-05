package uk.co.whitbread.hotel.card.client.piba;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.hotel.card.client.piba.model.TetheredUserRequest;

@FeignClient(value = "${feign.pibaaccount.name}", url = "${feign.pibaaccount.url}",
    fallbackFactory = PibaAccountClientFallbackFactory.class)
public interface PibaAccountClient {

  @PostMapping(value = "/piba/account/register/tetheredUser",
  produces = {MediaType.APPLICATION_JSON_VALUE})
  Void registerTetheredUser(@RequestHeader("Authorization") String authorization,
      @RequestBody TetheredUserRequest tetheredUserRequest);

}
