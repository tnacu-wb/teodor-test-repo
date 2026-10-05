package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static io.micrometer.common.util.StringUtils.isNotEmpty;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.RateClassificationMapper.mapRateClassificationsToRateClassificationsMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.ContentClientLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.ContentClientWebFlux;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.mapper.HotelCityTaxMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.ContentClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassificationsList;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentClientLookUpService implements ContentClientLookupPort {

  private final ContentClient contentClient;
  private final ContentClientWebFlux contentClientWebFlux;
  private final HotelCityTaxMapper hotelCityTaxMapper;
  private final CacheLookupService cacheLookupService;

  public Map<String, RateClassification> getRateClassifications(
      final BartBrandHotelCode hotelBrand,
      final String language, final String hotelCode, final String country) {

    Map<String, RateClassification> rateClassificationMap = new HashMap<>();

    RateClassificationsList rateClassificationList;

    rateClassificationList = contentClient
        .getRateClassifications(
            hotelBrand.name().toLowerCase(), language.toLowerCase(), hotelCode);

    if (rateClassificationList != null && CollectionUtils.isNotEmpty(rateClassificationList.getRateClassifications())) {
      rateClassificationMap = mapRateClassificationsToRateClassificationsMap(rateClassificationList);
    }

    return rateClassificationMap;
  }

  @Override
  public HotelsCityTaxInfo getHotelsCityTaxInfo(String country, String language, List<String> hotelCodes) {

    var globalConfig = contentClientWebFlux.getGlobalConfig(isNotEmpty(country) ? country : "gb",
        isNotEmpty(language) ? language : "en");
    Set<String> hotelsWithCityTax = new HashSet<>(globalConfig.getHotelsWithCityTax());
    Set<String> requestedHotels = new HashSet<>(hotelCodes);
    requestedHotels.retainAll(hotelsWithCityTax);

    Map<String, HotelCityTax> hotelsCityTaxes = Map.of();
    if (requestedHotels.size() == 1) {
      var requestedHotel = requestedHotels.iterator().next();
      hotelsCityTaxes = Map.of(requestedHotel, getHotelCityTax(country, language, requestedHotel));
    } else if (requestedHotels.size() > 1) {
      hotelsCityTaxes = getHotelsCityTax(country, language, requestedHotels);
    }
    return HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(globalConfig.getHotelsWithCityTax())
        .hotelsCityTaxes(hotelsCityTaxes)
        .build();
  }

  private Map<String, HotelCityTax> getHotelsCityTax(String country, String language,
      Set<String> hotelIds) {

    List<CompletableFuture<Entry<String, HotelCityTax>>> futures = hotelIds.stream()
        .map(hotelId -> CompletableFuture.supplyAsync(() ->
            Map.entry(hotelId, getHotelCityTax(country, language, hotelId))
        ))
        .toList();

    CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

    return allDone.thenApply(v ->
        futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
    ).join();
  }

  private HotelCityTax getHotelCityTax(String country, String language, String hotelId) {
    var hotelInfoFromCache = cacheLookupService.getHotelInformationFromCache(hotelId, country, language);
    if (hotelInfoFromCache == null || hotelInfoFromCache.getCityTax() == null) {
      var cityTaxInfo = contentClientWebFlux.getHotelInformation(isNotEmpty(country) ? country : "gb",
          isNotEmpty(language) ? language : "en", hotelId).getCityTax();
      return hotelCityTaxMapper.toModel(cityTaxInfo);
    }
    return hotelCityTaxMapper.toModel(hotelInfoFromCache.getCityTax());
  }
}
