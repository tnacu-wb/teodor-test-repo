package uk.co.whitbread.spending.infrastructure.rest.client.worldline.service;

import feign.Headers;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import uk.co.whitbread.spending.domain.model.in.worldline.WorldLineTcpHeaders;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.infrastructure.rest.utils.WorldlineUtils;

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

  @GetMapping(value = "${worldline.rest.payment-info-endpoint}", produces = {MediaType.APPLICATION_JSON_VALUE})
  @Headers({"Content-Type: " + MediaType.APPLICATION_JSON_VALUE})
  PaymentInfoResponse getPaymentInfo(@RequestHeader Map<String, String> headers,
      @RequestParam Map<String, String> queryParams);
}
