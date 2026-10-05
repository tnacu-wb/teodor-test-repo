package uk.co.whitbread.hotel.account.client.pibaAccount;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.hotel.account.client.pibaAccount.model.PibaAccountsResponse;

@FeignClient(value = "${feign.pibaaccount.name:pibaaccount}", url = "${feign.pibaaccount.url}",
    fallbackFactory = PibaAccountClientFallbackFactory.class)
public interface PibaAccountClient {

  @GetMapping(value = "${feign.pibaaccount.get-piba-accounts-endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
  PibaAccountsResponse getPibaAccounts(@RequestHeader("Authorization") String authorization,
      @RequestHeader("ignoreWorldlineDetails") boolean ignoreWorldlineDetails);
}
