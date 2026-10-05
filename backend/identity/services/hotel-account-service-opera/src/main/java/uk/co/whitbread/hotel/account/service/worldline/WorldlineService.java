package uk.co.whitbread.hotel.account.service.worldline;

import static java.util.Objects.nonNull;

import feign.FeignException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.whitbread.hotel.account.client.pibaAccount.model.PibaAccount;
import uk.co.whitbread.hotel.account.client.worldline.WorldlineClient;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfo;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfoResponse;
import uk.co.whitbread.hotel.account.client.worldline.model.TrustedPartnerCredentials;
import uk.co.whitbread.hotel.account.config.WorldlineProperties;
import uk.co.whitbread.hotel.account.exceptions.WorldlineServiceException;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.service.worldline.model.ContactDetails;
import uk.co.whitbread.hotel.account.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.account.service.worldline.utils.ContactDetailsError;
import uk.co.whitbread.hotel.account.service.worldline.utils.WorldlineUtils;
import uk.co.whitbread.hotel.account.utils.NetworkUtils;

@Slf4j
@Service
@AllArgsConstructor
public class WorldlineService {

  private WorldlineClient worldlineClient;
  private WorldlineProperties worldlineProperties;
  private NetworkUtils networkUtils;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "AccountInfoCache", key = "#tetheredUserId")
  public AccountInfo getAccountInformation(String tetheredUserId, Scheme scheme, String ipAddress) {
    var prop = Scheme.GB.equals(scheme) ? worldlineProperties.getGb()
        : worldlineProperties.getDe();
    var credentials = new TrustedPartnerCredentials(prop.getUsername(), prop.getPassword());
    try {
      return Optional.ofNullable(
              worldlineClient.getAccountInfo(WorldlineUtils.serializeHeader(credentials),
                  tetheredUserId, prop.getCultureCode(), prop.getCompanyNumber(), ipAddress))
          .map(AccountInfoResponse::getData)
          .orElseThrow(() -> new WorldlineServiceException(
              String.format("No account information found for tetheredUserId: %s",
                  tetheredUserId)));
    } catch (FeignException e) {
      log.error("Error getting account info", e);
      throw new WorldlineServiceException(
          String.format("Error getting account information for tetheredUserId: %s", tetheredUserId),
          e);
    }
  }

  public void updateContactDetails(PibaAccount account,
      CustomerRequest contactDetails) {

    var prop = Scheme.GB.equals(account.getScheme()) ? worldlineProperties.getGb()
        : worldlineProperties.getDe();
    var credentials = new TrustedPartnerCredentials(prop.getUsername(), prop.getPassword());
    var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    var httpRequest = Objects.nonNull(requestAttributes) ? requestAttributes.getRequest() : null;
    var ipAddress = Objects.nonNull(httpRequest) ? networkUtils.getClientIp(httpRequest)
        : worldlineProperties.getDefaultIpAddress();
    var contactDetailsPayload = getContactDetailsPayload(contactDetails);
    var tetheredUserGuid = account.getTetheredGuid();
    try {
      worldlineClient
          .updateUserContactDetails(WorldlineUtils.serializeHeader(credentials),
              prop.getCultureCode(), prop.getCompanyNumber(), ipAddress,
              tetheredUserGuid,
              tetheredUserGuid,
              contactDetailsPayload);
    } catch (FeignException e) {
      var errors = extractWorldLineErrors(e.contentUTF8());
      if (!errors.isEmpty()) {
        throw new WorldlineServiceException(
            String.format("WorldLine errors when updating contact details for tetheredUserGuid %s: %s",
                tetheredUserGuid, errors), e);
      }
      log.error("Error updating contact details", e);
      throw new WorldlineServiceException(
          String.format("Error updating contact details for tetheredUserGuid %s, email: %s",
              tetheredUserGuid, contactDetails.getContactDetail().getEmail()), e);
    }
  }

  private ContactDetails getContactDetailsPayload(CustomerRequest contactDetails) {
    var contactDetailsBuilder = ContactDetails.builder();
    if (nonNull(contactDetails.getContactDetail().getTitle())) {
      contactDetailsBuilder.title(contactDetails.getContactDetail().getTitle());
    }
    if (nonNull(contactDetails.getContactDetail().getFirstName())) {
      contactDetailsBuilder.foreName(contactDetails.getContactDetail().getFirstName());
    }
    if (nonNull(contactDetails.getContactDetail().getLastName())) {
      contactDetailsBuilder.lastName(contactDetails.getContactDetail().getLastName());
    }
    if (nonNull(contactDetails.getContactDetail().getEmail())) {
      contactDetailsBuilder.email(contactDetails.getContactDetail().getEmail());
    }
    if (nonNull(contactDetails.getContactDetail().getTelephone())) {
      contactDetailsBuilder.landlineNumber(contactDetails.getContactDetail().getTelephone());
    }
    if (nonNull(contactDetails.getContactDetail().getMobile())) {
      contactDetailsBuilder.mobile(contactDetails.getContactDetail().getMobile());
    }
    return contactDetailsBuilder.build();
  }

  private String extractWorldLineErrors(String message) {
    return EnumSet.allOf(ContactDetailsError.class).stream()
        .filter(error -> message.contains(error.getMessage()))
        .map(error -> String.valueOf(error.getCode()))
        .collect(Collectors.joining(", "));
  }
}
