package uk.co.whitbread.piba.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.client.WorldlineClient;
import uk.co.whitbread.piba.account.converter.WorldlineTransformer;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.TrustedPartnerCredentials;
import uk.co.whitbread.piba.account.model.WorldLineTcpHeaders;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.WorldlineRestProperties;
import uk.co.whitbread.piba.account.util.LogUtils;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorldLineService {

  @Qualifier("worldlineWebServiceTemplate")
  private final WebServiceTemplate worldlineWebServiceTemplate;
  private final WorldlineTransformer worldlineAccountTransformer;
  private final WorldLineAccountResponseValidator worldLineResponseValidator;
  private final WorldLineProperties worldLineProperties;
  private final WorldLineWebServiceMessageCallback worldLineWebServiceMessageCallback;
  private final WorldlineClient worldlineClient;
  private final PropertiesLoader propertiesLoader;
  private final WorldlineUtils worldlineUtils;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day", value = "WlTetheredUserDetails", key = "#tetheredUserGuid + ':' + #scheme.name()")
  public TetheredUserDetailsResponse getUserDetails(String tetheredUserGuid, Scheme scheme) {
    TetheredUserDetailsGetResponse response = (TetheredUserDetailsGetResponse) dispatchWorldLineRequest(
        worldlineAccountTransformer.toTetheredUserDetailsRequest(tetheredUserGuid, scheme));
    worldLineResponseValidator.validate(response);
    return worldlineAccountTransformer.toTetheredUserDetailsResponse(response);
  }

  private <T> Object dispatchWorldLineRequest(T requestObject) {
    log.info("Sending WorldlineRequest for {} = {} #####",
        requestObject.getClass().getSimpleName(), worldlineUtils.serializeObject(requestObject));
    var response = worldlineWebServiceTemplate.marshalSendAndReceive(
        worldLineProperties.getPiba().getService().getUrl(),
        requestObject,
        worldLineWebServiceMessageCallback
    );
    log.info("Received WorldlineResponse for {} = {} #####",
        response.getClass().getSimpleName(), worldlineUtils.serializeObject(response));
    return response;
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "WlAccountInfo", key = "#tetheredUserGuid + ':' + #scheme.name()", unless = "#result == null")
  public AccountInfoResponse getAccountInfo(Scheme scheme, String clientIpAddress, String tetheredUserGuid) {
    log.debug("Retrieve account info for tethered user GUID {}",
        LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50));
    var propertiesByLocation = propertiesLoader.getPropertiesByLocation(scheme.name());
    var trustedPartnerCredentials = createWorldLineCredentials(propertiesByLocation);

    try {
      AccountInfoResponse accountInfoResponse = worldlineClient.getAccountInfo(new WorldLineTcpHeaders(
          propertiesByLocation.getCompanyNumber(), trustedPartnerCredentials,
          propertiesByLocation.getCultureCode(), clientIpAddress, tetheredUserGuid));

      if (accountInfoResponse == null) {
        log.warn("Worldline account info response is null for tethered user GUID {}",
            LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50));
        return null;
      }
      log.info("Worldline account retrieved with billingFrequency: {} for tetheredUserGuid: {}.",
          accountInfoResponse.getData().getBillingFrequency(),
          LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50));
      return accountInfoResponse;
    } catch (Exception e) {
      log.error("Error retrieving account info for tethered user GUID {}: {}",
          LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50), e.getMessage(), e);
      throw e;
    }
  }

  private TrustedPartnerCredentials createWorldLineCredentials(
      WorldlineRestProperties.PropertiesByLocation propertiesByLocation) {
    return TrustedPartnerCredentials.builder()
        .username(propertiesByLocation.getUsername())
        .password(propertiesByLocation.getPassword())
        .build();
  }
}
