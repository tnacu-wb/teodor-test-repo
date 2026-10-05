package uk.co.whitbread.spending.infrastructure.rest.client.worldline;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.in.worldline.TrustedPartnerCredentials;
import uk.co.whitbread.spending.domain.model.in.worldline.WorldLineTcpHeaders;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.domain.ports.secondary.WorldlineOutPort;
import uk.co.whitbread.spending.infrastructure.config.WorldlineProperties.PropertiesByLocation;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.PropertiesLoader;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.WorldlineClient;
import uk.co.whitbread.spending.infrastructure.rest.utils.WorldlineUtils;

@Slf4j
@RequiredArgsConstructor
public class WorldlineOutPortImpl implements WorldlineOutPort {

  private final WorldlineClient worldlineClient;
  private final PropertiesLoader propertiesLoader;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Hour",
      value = "AccountInfoCache", key = "#tetheredUserGuid")
  public AccountInfoResponse getAccountInfo(String location,
      String ipAddress, String tetheredUserGuid) {
    log.info("Retrieve account info for tethered user GUID {}", tetheredUserGuid);
    var propertiesByLocation = propertiesLoader.getPropertiesByLocation(location);
    var trustedPartnerCredentials = createWorldLineCredentials(propertiesByLocation);

    return worldlineClient.getAccountInfo(new WorldLineTcpHeaders(
        propertiesByLocation.getCompanyNumber(), trustedPartnerCredentials,
        propertiesByLocation.getCultureCode(), ipAddress, tetheredUserGuid));
  }

  @Override
  public PaymentInfoResponse getPaymentInfo(String location,
      String tetheredUserGuid, PaymentInfoModel paymentInfoModel) {

    log.info("Retrieve payment info for tethered user GUID {}", tetheredUserGuid);
    var propertiesByLocation = propertiesLoader.getPropertiesByLocation(location);
    var trustedPartnerCredentials = createWorldLineCredentials(propertiesByLocation);
    var paymentInfoHeaders = new WorldLineTcpHeaders(propertiesByLocation.getCompanyNumber(),
        trustedPartnerCredentials, propertiesByLocation.getCultureCode(),
        paymentInfoModel.getIpAddress(),
        tetheredUserGuid);

    var paymentInfoRequestHeaders = buildWorldlineHeaders(paymentInfoHeaders);
    var paymentInfoQueryParams = buildPaymentInfoQueryParams(paymentInfoModel);
    return worldlineClient.getPaymentInfo(paymentInfoRequestHeaders, paymentInfoQueryParams);
  }

  private TrustedPartnerCredentials createWorldLineCredentials(
      PropertiesByLocation propertiesByLocation) {
    return TrustedPartnerCredentials.builder()
        .username(propertiesByLocation.getUsername())
        .password(propertiesByLocation.getPassword())
        .build();
  }

  private Map<String, String> buildWorldlineHeaders(WorldLineTcpHeaders headers) {
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("CompanyNumber", headers.companyNumber());
    headerMap.put("TrustedPartnerCredentials",
        WorldlineUtils.serializeHeader(headers.trustedPartnerCredentials()));
    headerMap.put("CultureCode", headers.cultureCode());
    headerMap.put("IPAddress", headers.ipAddress());
    headerMap.put("TetheredUserGuid", headers.tetheredUserGuid());
    return headerMap;
  }

  private Map<String, String> buildPaymentInfoQueryParams(PaymentInfoModel paymentInfoModel) {
    return Map.of("page", paymentInfoModel.getPage().toString(), "maxDisplayRows",
        paymentInfoModel.getSize().toString(),
        "nonInvoicedOnly", String.valueOf(paymentInfoModel.isNonInvoiceOnly()));
  }

}
