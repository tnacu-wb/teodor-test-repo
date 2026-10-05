package uk.co.whitbread.spending.infrastructure.rest.client.pibaaccountservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.CustomerAccountsResponse;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.TetheredUserDetailsResponse;
import uk.co.whitbread.spending.infrastructure.rest.client.config.FeignErrorDecoderConfig;

@FeignClient(
     value = "${config.service.piba-account-service.name:pibaaccountserviceclient}",
     url = "${config.service.piba-account-service.host}",
     configuration = FeignErrorDecoderConfig.class)
public interface PibaAccountServiceClient {
  @GetMapping(value = "${config.service.piba-account-service.tetheredUserDetailsEndpoint}",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  TetheredUserDetailsResponse getTetheredUserDetails(@RequestHeader String authorization,
      @PathVariable String tetheredUserGuid,
      @PathVariable String scheme);

  @GetMapping(value = "${config.service.piba-account-service.accountsEndpoint}",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  CustomerAccountsResponse getAccounts(@RequestHeader String authorization);
}
