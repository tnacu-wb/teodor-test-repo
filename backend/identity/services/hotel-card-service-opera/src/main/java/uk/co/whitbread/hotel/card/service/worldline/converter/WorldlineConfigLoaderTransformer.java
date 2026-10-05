package uk.co.whitbread.hotel.card.service.worldline.converter;

import static java.util.Objects.isNull;
import static uk.co.whitbread.hotel.card.utils.SanitizingUtils.sanitize;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.model.WorldlineHeadersData;
import uk.co.whitbread.hotel.card.model.adapter.WorldlineRequestTypeAdapter;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.utils.WorldlineUtils;
import uk.co.whitbread.piba.api.converter.WorldlineRequestTransformer;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

@Slf4j
@Component
public sealed class WorldlineConfigLoaderTransformer extends WorldlineRequestTransformer
    permits WorldlineSoapTransformer {

  @Value("${worldline.supportedCountries:gb,de}")
  private Set<String> supportedCountries;

  private final WorldlineUtils worldlineUtils;

  private final ConcurrentMap<Scheme, WorldlineHeadersData> worldlineConfigs;

  public WorldlineConfigLoaderTransformer(WorldLineProperties worldLineProperties, final WorldlineUtils worldlineUtils) {
    super(worldLineProperties);
    this.worldlineConfigs = new ConcurrentHashMap<>();
    this.worldlineUtils = worldlineUtils;
  }

  @PostConstruct
  public void loadWorldlineHeaderData() {

    supportedCountries.forEach(languageCode -> {
      var scheme = extractScheme(languageCode);
      var worldLineRequestHeader = getWorldLineRequestHeader(scheme);
      var worldLineCredentialsType = getWorldLineCredentialsType(scheme);
      var worldlineHeadersData = new WorldlineHeadersData(scheme,
          worldLineRequestHeader, worldLineCredentialsType);
      worldlineConfigs.put(scheme, worldlineHeadersData);
    });
  }

  protected Scheme extractScheme(String languageCode) {
    var scheme = EnumUtils.getEnum(Scheme.class, parseCountryCode(languageCode));
    log.info("Scheme in extractScheme - {}", LogFormatUtils.formatValue(scheme, true));
    return isNull(scheme) ? Scheme.GB : scheme;
  }

  protected String parseCountryCode(String countryCode) {
    var returnStr = (countryCode.length() > 2
        ? countryCode.substring(countryCode.length() - 2)
        : countryCode).toUpperCase();
    log.info("returnStr {} from parse countryCode - {}",
        sanitize(returnStr), sanitize(countryCode));
    return returnStr;
  }

  protected HeaderType getWorldLineRequestHeader(Scheme scheme) {
    var header = Scheme.DE.equals(scheme) ? getDeHeader() : getGbHeader();
    header.setClientMessageId(String.valueOf(UUID.randomUUID()));
    log.debug("Adding ClientMessageId={}", header.getClientMessageId());
    return header;
  }

  protected TrustedPartnerCredentialsType getWorldLineCredentialsType(Scheme scheme) {
    return Scheme.DE.equals(scheme) ? getDeCredentials() : getGbCredentials();
  }

  protected <T> void populateRequestHeaders(WorldlineRequestTypeAdapter<T> requestTypeAdapter,
      String accountId, String countryCode) {

    var worldlineHeadersData = worldlineConfigs.get(extractScheme(countryCode));
    log.info("user Name {} and password - {}", worldlineHeadersData.trustedPartnerCredentialsType().getUsername(),
        worldlineHeadersData.trustedPartnerCredentialsType().getPassword());
    requestTypeAdapter.populateRequestHeaders(worldlineHeadersData, worldlineUtils.formatId(accountId));
  }

  protected WorldlineUtils getWorldlineUtils() {
    return worldlineUtils;
  }

}
