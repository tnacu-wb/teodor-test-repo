package uk.co.whitbread.piba.account.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;
import uk.co.whitbread.piba.account.model.WorldLineTcpHeaders;
import uk.co.whitbread.piba.account.util.WorldlineUtils;

@FeignClient(name = "${worldline.rest.name:worldline}", url = "${worldline.rest.url}",
      fallbackFactory = WorldlineClientFallbackFactory.class)
public interface WorldlineClient {

  @GetMapping(value = "${worldline.rest.account-info-endpoint}", produces = {MediaType.APPLICATION_JSON_VALUE})
  @Headers({"Content-Type: " + MediaType.APPLICATION_JSON_VALUE})
  AccountInfoResponse getAccountInfo(
      @RequestHeader("CompanyNumber") String companyNumber,
      @RequestHeader("TrustedPartnerCredentials") String trustedPartnerCredentials,
      @RequestHeader("CultureCode") String cultureCode,
      @RequestHeader("IPAddress") String ipAddress,
      @RequestHeader("TetheredUserGuid") String tetheredUserGuid);

  default AccountInfoResponse getAccountInfo(WorldLineTcpHeaders headers) {
    String credentials = WorldlineUtils.serializeHeader(headers.trustedPartnerCredentials());
    return getAccountInfo(
        headers.companyNumber(),
        credentials,
        headers.cultureCode(),
        headers.ipAddress(),
        headers.tetheredUserGuid()
    );
  }

}
