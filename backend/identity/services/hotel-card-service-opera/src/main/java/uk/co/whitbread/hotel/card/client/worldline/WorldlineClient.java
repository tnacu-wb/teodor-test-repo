package uk.co.whitbread.hotel.card.client.worldline;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCostCentreDetailsResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardResponse;

/**
 * Feign client for accessing worldline service.
 */
@FeignClient(value = "${feign.worldline.name:worldline}", url = "${feign.worldline.url}",
  fallbackFactory = WorldlineFallbackFactory.class)
public interface WorldlineClient {

  /**
   * Create cardholder user.
   *
   * @param trustedPartnerCredentials the trusted partner credentials
   * @param tetheredUserGuid the tethered user guid
   * @param cultureCode the culture code
   * @param companyNumber the company number
   * @param ipAddress the ip address
   * @param worldlineCardHolderUserRequest the worldline cardholder user request
   * @return the worldline cardholder user response
   */
  @PostMapping(value = "api/v1/account/cardHolder", consumes = {MediaType.APPLICATION_JSON_VALUE})
  WorldlineCardHolderUserResponse createCardHolderUser(
      @RequestHeader("TrustedPartnerCredentials") String trustedPartnerCredentials,
      @RequestHeader("TetheredUserGuid") String tetheredUserGuid,
      @RequestHeader("CultureCode") String cultureCode,
      @RequestHeader("CompanyNumber") String companyNumber,
      @RequestHeader("IPAddress") String ipAddress,
      @Valid @RequestBody WorldlineCardHolderUserRequest worldlineCardHolderUserRequest);

  /**
   * Get a list with cost centre details.
   *
   * @param trustedPartnerCredentials the trusted partner credentials
   * @param tetheredUserGuid the tethered user guid
   * @param cultureCode the culture code
   * @param companyNumber the company number
   * @param ipAddress the ip address
   * @return the worldline cost centre details response
   */
  @GetMapping(value = "api/v1/costCentre", consumes = {MediaType.APPLICATION_JSON_VALUE})
  WorldlineCostCentreDetailsResponse costCentreDetails(
      @RequestHeader("TrustedPartnerCredentials") String trustedPartnerCredentials,
      @RequestHeader("TetheredUserGuid") String tetheredUserGuid,
      @RequestHeader("CultureCode") String cultureCode,
      @RequestHeader("CompanyNumber") String companyNumber,
      @RequestHeader("IPAddress") String ipAddress);

  /**
   * Replace or cancel card.
   *
   * @param trustedPartnerCredentials the trusted partner credentials
   * @param tetheredUserGuid the tethered user guid
   * @param cultureCode the culture code
   * @param companyNumber the company number
   * @param ipAddress the ip address
   * @param worldlineReplaceCardRequest the worldline replace or cancel card request
   * @return the worldline replace or cancel card response
   */
  @PutMapping(value = "api/v1/card/replaceOrCancel", consumes = {MediaType.APPLICATION_JSON_VALUE})
  WorldlineReplaceCardResponse replaceCard(
      @RequestHeader("TrustedPartnerCredentials") String trustedPartnerCredentials,
      @RequestHeader("TetheredUserGuid") String tetheredUserGuid,
      @RequestHeader("CultureCode") String cultureCode,
      @RequestHeader("CompanyNumber") String companyNumber,
      @RequestHeader("IPAddress") String ipAddress,
      @Valid @RequestBody WorldlineReplaceCardRequest worldlineReplaceCardRequest);
}
