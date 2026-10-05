package uk.co.whitbread.hotel.account.client.worldline;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfoResponse;
import uk.co.whitbread.hotel.account.client.worldline.model.ContactDetailsResponse;
import uk.co.whitbread.hotel.account.service.worldline.model.ContactDetails;

/**
 * Feign Client for accessing Worldline Rest endpoints
 */
@FeignClient(value = "${feign.worldline.name:worldline}", url = "${feign.worldline.url}",
    fallbackFactory = WorldlineClientFallbackFactory.class)
public interface WorldlineClient {

  @GetMapping(value = "${feign.worldline.account-info-endpoint}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  AccountInfoResponse getAccountInfo(
      @RequestHeader("TrustedPartnerCredentials") String trustedPartnerCredentials,
      @RequestHeader("TetheredUserGuid") String tetheredUserGuid,
      @RequestHeader("CultureCode") String cultureCode,
      @RequestHeader("CompanyNumber") String companyNumber,
      @RequestHeader("IPAddress") String ipAddress
  );

  @PutMapping(value = "${feign.worldline.user-contact-details-endpoint}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  ContactDetailsResponse updateUserContactDetails(
      @RequestHeader("TrustedPartnerCredentials") String trustedPartnerCredentials,
      @RequestHeader("CultureCode") String cultureCode,
      @RequestHeader("CompanyNumber") String companyNumber,
      @RequestHeader("IPAddress") String ipAddress,
      @RequestHeader("tetheredUserGuid") String tetheredUserGuidHeader,
      @PathVariable("UserTetheredUserGuidToUpdateTheContactDetails") String tetheredUserGuid,
      @RequestBody ContactDetails contactDetails);
}
