package uk.co.whitbread.hotel.account.client.worldline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfoResponse;
import uk.co.whitbread.hotel.account.client.worldline.model.ContactDetailsResponse;
import uk.co.whitbread.hotel.account.service.worldline.model.ContactDetails;

@Slf4j
@Component
public class WorldlineClientFallbackFactory implements FallbackFactory<WorldlineClient> {


  @Override
  public WorldlineClient create(Throwable cause) {
    return new WorldlineClient() {
      @Override
      public AccountInfoResponse getAccountInfo(String trustedPartnerCredentials,
          String tetheredUserGuid, String cultureCode, String companyNumber, String ipAddress) {
        log.error("Failed to retrieve account info for tethered user GUID {}", tetheredUserGuid, cause);
        return null;
      }

      @Override
      public ContactDetailsResponse updateUserContactDetails(String trustedPartnerCredentials, String cultureCode,
          String companyNumber, String ipAddress, String tetheredUserGuidHeader, String tetheredUserGuid,
          ContactDetails contactDetails) {
        log.error("Failed to update contact details for tetheredUserGuid {}", tetheredUserGuid, cause);
        return null;
      }
    };
  }
}
