package uk.co.whitbread.reservation.infrastructure.rest.client.reservation;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.domain.model.out.aem.LocationsResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.ZonalUuidResponse;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelData;
import uk.co.whitbread.reservation.domain.model.out.getlabel.LabelDataListResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.AemOutPort;
import uk.co.whitbread.reservation.infrastructure.config.AemConfigurationProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.exception.ResponseParsingException;

@Slf4j
@Service
public class AemOutPortImpl implements AemOutPort {

  public static final String RESTAURANT_PREMIERINN = "restaurant-premierinn";

  private final ObjectMapper objectMapper;

  private final RestClient restClient;

  private final AemConfigurationProperties aemConfigurationProperties;

  @Autowired
  public AemOutPortImpl(ObjectMapper objectMapper,
      @Qualifier("aemRestClient") RestClient restClient,
      AemConfigurationProperties aemConfigurationProperties) {
    this.objectMapper = objectMapper;
    this.restClient = restClient;
    this.aemConfigurationProperties = aemConfigurationProperties;
  }

  @Override
  public AemHeaderResponse getHeaders(String restaurant) {
    UriComponents headersUriComponent = buildUriWithQueryParam(restaurant,
        aemConfigurationProperties.getHeaderUri());
    String responseBody = restClient.get()
        .uri(headersUriComponent.toUriString())
        .retrieve()
        .body(String.class);
    return parseResponse(responseBody, AemHeaderResponse.class, "Error Parsing HeaderResponse");
  }

  @Override
  public AemFooterResponse getFooters(String restaurant) {
    UriComponents footersUriComponent = buildUriWithQueryParam(restaurant,
        aemConfigurationProperties.getFooterUri());
    String responseBody = restClient.get()
        .uri(footersUriComponent.toUriString())
        .retrieve()
        .body(String.class);
    return parseResponse(responseBody, AemFooterResponse.class, "Error Parsing FooterResponse");
  }

  @Override
  public BookPage getBookPageContent(String restaurant, String location, String subLocation) {
    UriComponents pageContentUriComponent = buildBookPageUriWithQueryParam(restaurant,
        location, subLocation);
    String responseBody = restClient.get()
        .uri(pageContentUriComponent.toUriString())
        .retrieve()
        .body(String.class);
    return parseResponse(responseBody, BookPage.class, "Error Parsing BookPageResponse");
  }

  @Override
  public AemCookieContentResponse getCookieContent() {
    return restClient.get()
        .uri(aemConfigurationProperties.getCookieConsentUri())
        .retrieve()
        .body(AemCookieContentResponse.class);
  }

  @Override
  @Cacheable(value = "locationsCache")
  public LocationsResponse locations(String restaurant, String location, String subLocation) {
    UriComponents zonalUuidUriComponent = getLocationsUri(restaurant);
    String responseJson = restClient.get()
        .uri(zonalUuidUriComponent.toUriString())
        .retrieve()
        .body(String.class);
    List<ZonalUuidResponse> zonalMappingResponse;
    try {
      zonalMappingResponse =
          objectMapper.readValue(responseJson, new TypeReference<List<ZonalUuidResponse>>() {
          });
    } catch (Exception e) {
      throw new ResponseParsingException("Error parsing zonalResponse", e);
    }

    List<ZonalUuidResponse> zonalUuidResponseList = getFilteredLocations(zonalMappingResponse, location, subLocation);
    List<ZonalUuidResponse> result = CollectionUtils.isEmpty(zonalUuidResponseList)
        ? zonalMappingResponse : zonalUuidResponseList;
    return LocationsResponse.builder().locations(result).build();
  }

  private UriComponents getLocationsUri(String restaurant) {
    if (RESTAURANT_PREMIERINN.equals(restaurant)) {
      return UriComponentsBuilder.fromUriString(
          aemConfigurationProperties.getUnbrandedRestaurantsUri()).build();
    } else {
      return buildUriWithQueryParam(restaurant, aemConfigurationProperties.getZonalUuidUri());
    }
  }

  public List<ZonalUuidResponse> getFilteredLocations(List<ZonalUuidResponse> rawLocationDetails,
      String location, String subLocation) {
    List<ZonalUuidResponse> zonalMappingFilteredResponse = new ArrayList<>();
    for (ZonalUuidResponse responseObj : rawLocationDetails) {
      String path = responseObj.getPath();
      if (path.endsWith((isNotBlank(location) ? location : "") + "/" + subLocation)) {
        zonalMappingFilteredResponse.add(responseObj);
      }
    }
    return zonalMappingFilteredResponse;
  }

  @Override
  @Cacheable(value = "getLabelCache")
  public LabelDataListResponse getLabel() {
    Map<String, String> labelMap = restClient.get()
        .uri(aemConfigurationProperties.getLabelUri())
        .retrieve()
        .body(new ParameterizedTypeReference<Map<String, String>>() {
        });

    List<LabelData> labelDataList = new ArrayList<>();
    if (labelMap != null) {
      for (Map.Entry<String, String> entry : labelMap.entrySet()) {
        LabelData labelData = new LabelData();
        labelData.setKey(entry.getKey());
        labelData.setValue(entry.getValue());
        labelDataList.add(labelData);
      }
    }
    return LabelDataListResponse.builder().labels(labelDataList).build();
  }

  private UriComponents buildUriWithQueryParam(String restaurant, String path) {
    return UriComponentsBuilder.fromUriString(
            aemConfigurationProperties.getBaseUri()
                .replace("restaurantname", restaurant.replace("-", "")) + path)
        .build();
  }

  private UriComponents buildBookPageUriWithQueryParam(String restaurant, String location,
      String subLocation) {
    StringBuilder bookPageUri = new StringBuilder("/locations/");
    if (isNotBlank(location)) {
      bookPageUri.append(location).append("/");
    }
    if (isNotBlank(subLocation)) {
      bookPageUri.append(subLocation).append("/");
    }
    bookPageUri.append("bookpagedata.json");
    return UriComponentsBuilder.fromUriString(
            aemConfigurationProperties.getBaseUri()
                .replace("restaurantname", restaurant.replace("-", "")) + bookPageUri)
        .build();
  }

  private <T> T parseResponse(String responseJson, Class<T> responseType, String errorMessage) {
    try {
      return objectMapper.readValue(responseJson, responseType);
    } catch (Exception e) {
      throw new ResponseParsingException(errorMessage, e);
    }
  }

}
