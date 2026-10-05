package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.ContentClientLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.ContentClientWebFlux;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.mapper.HotelCityTaxMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.ContentClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassificationsList;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalConfigDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelCityTaxDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;

@ExtendWith(MockitoExtension.class)
class ContentClientLookupSvcTest {

  private static final BartBrandHotelCode hotelBrand = BartBrandHotelCode.PI;
  private static final String LANGUAGE_CODE = "en";
  private static final String COUNTRY_NULL = null;


  @Mock
  private ContentClient contentClient;

  @Mock
  private ContentClientWebFlux contentClientWebFlux;

  @Mock
  private HotelCityTaxMapper hotelCityTaxMapper;

  @Mock
  private CacheLookupService cacheLookupService;

  private ContentClientLookupPort contentClientLookupSvc;

  @BeforeEach
  public void setup() {
    contentClientLookupSvc = new ContentClientLookUpService(contentClient, contentClientWebFlux,
        hotelCityTaxMapper, cacheLookupService);
  }

  @Test
  void testGetHotelsCityTaxInfo_singleHotelWithCityTax() {
    String country = "GB";
    String language = "en";
    List<String> hotelCodes = List.of("HOTEL1");

    GlobalConfigDto globalConfig = mock(GlobalConfigDto.class);
    when(globalConfig.getHotelsWithCityTax()).thenReturn(List.of("HOTEL1"));
    when(contentClientWebFlux.getGlobalConfig(country, language)).thenReturn(globalConfig);

    HotelInformationExtendedDto hotelInfo = mock(HotelInformationExtendedDto.class);
    when(contentClientWebFlux.getHotelInformation(country, language, "HOTEL1")).thenReturn(hotelInfo);

    HotelCityTax cityTax = mock(HotelCityTax.class);
    when(hotelCityTaxMapper.toModel(hotelInfo.getCityTax())).thenReturn(cityTax);

    HotelsCityTaxInfo result = contentClientLookupSvc.getHotelsCityTaxInfo(country, language, hotelCodes);

    assertThat(result.getHotelsWithCityTax()).containsExactly("HOTEL1");
    assertThat(result.getHotelsCityTaxes()).containsEntry("HOTEL1", cityTax);
  }

  @Test
  void testGetHotelsCityTaxInfo_multipleHotelsWithCityTax() {
    String country = "GB";
    String language = "en";
    List<String> hotelCodes = List.of("HOTEL1", "HOTEL2", "HOTEL3");

    GlobalConfigDto globalConfig = mock(GlobalConfigDto.class);
    when(globalConfig.getHotelsWithCityTax()).thenReturn(List.of("HOTEL1", "HOTEL2"));
    when(contentClientWebFlux.getGlobalConfig(country, language)).thenReturn(globalConfig);

    HotelInformationExtendedDto hotelInfo1 = mock(HotelInformationExtendedDto.class);
    when(hotelInfo1.getCityTax()).thenReturn(mock(HotelCityTaxDto.class));
    HotelInformationExtendedDto hotelInfo2 = mock(HotelInformationExtendedDto.class);
    when(hotelInfo2.getCityTax()).thenReturn(mock(HotelCityTaxDto.class));
    when(contentClientWebFlux.getHotelInformation(country, language, "HOTEL1")).thenReturn(hotelInfo1);
    when(contentClientWebFlux.getHotelInformation(country, language, "HOTEL2")).thenReturn(hotelInfo2);

    HotelCityTax cityTax1 = mock(HotelCityTax.class);
    HotelCityTax cityTax2 = mock(HotelCityTax.class);
    when(hotelCityTaxMapper.toModel(hotelInfo1.getCityTax())).thenReturn(cityTax1);
    when(hotelCityTaxMapper.toModel(hotelInfo2.getCityTax())).thenReturn(cityTax2);

    HotelsCityTaxInfo result = contentClientLookupSvc.getHotelsCityTaxInfo(country, language, hotelCodes);

    assertThat(result.getHotelsWithCityTax()).containsExactly("HOTEL1", "HOTEL2");
    assertThat(result.getHotelsCityTaxes())
        .containsEntry("HOTEL1", cityTax1)
        .containsEntry("HOTEL2", cityTax2);
  }

  @Test
  void testGetHotelsCityTaxInfo_noHotelsWithCityTax() {
    String country = "GB";
    String language = "en";
    List<String> hotelCodes = List.of("HOTEL1", "HOTEL2");

    GlobalConfigDto globalConfig = mock(GlobalConfigDto.class);
    when(globalConfig.getHotelsWithCityTax()).thenReturn(List.of());
    when(contentClientWebFlux.getGlobalConfig(country, language)).thenReturn(globalConfig);

    HotelsCityTaxInfo result = contentClientLookupSvc.getHotelsCityTaxInfo(country, language, hotelCodes);

    assertThat(result.getHotelsWithCityTax()).isEmpty();
    assertThat(result.getHotelsCityTaxes()).isEmpty();
  }

  @Test
  void testGetHotelCityTax_cacheHit() {
    // Arrange
    String country = "GB";
    String language = "en";
    String hotelId = "HOTEL1";

    var cityTaxDto = mock(HotelCityTaxDto.class);
    var cityTax = mock(HotelCityTax.class);

    var hotelInfoFromCache = mock(HotelInformationExtendedDto.class);
    when(hotelInfoFromCache.getCityTax()).thenReturn(cityTaxDto);

    when(cacheLookupService.getHotelInformationFromCache(hotelId, country, language))
        .thenReturn(hotelInfoFromCache);
    when(hotelCityTaxMapper.toModel(cityTaxDto)).thenReturn(cityTax);

    var globalConfig = mock(GlobalConfigDto.class);
    when(globalConfig.getHotelsWithCityTax()).thenReturn(List.of(hotelId));
    when(contentClientWebFlux.getGlobalConfig(country, language)).thenReturn(globalConfig);

    // Act
    var result = contentClientLookupSvc.getHotelsCityTaxInfo(country, language, List.of(hotelId));

    // Assert
    assertThat(result).isNotNull();
    verify(contentClientWebFlux, never()).getHotelInformation(any(), any(), any());
  }

  @Test
  void getRateClassificationsTestForNullRateClassifications() {

    when(contentClient.getRateClassifications("pi", "en", null))
        .thenReturn(null);

    Map<String, RateClassification> map = contentClientLookupSvc
        .getRateClassifications(hotelBrand, LANGUAGE_CODE, null, COUNTRY_NULL);

    assertThat(map).isNullOrEmpty();
  }

  @Test
  void getRateClassificationsTestForEmptyResultsFromContentClient() {

    when(contentClient.getRateClassifications("pi", "en", null))
        .thenReturn(getEmptyRateClassificationsList());

    Map<String, RateClassification> map = contentClientLookupSvc
        .getRateClassifications(hotelBrand, LANGUAGE_CODE, null, COUNTRY_NULL);

    assertThat(map).isNullOrEmpty();
  }

  @Test
  void getRateClassificationsTest_SuccessfulMapping() {
    when(contentClient.getRateClassifications("pi", "en", null))
        .thenReturn(getRateClassifications());

    Map<String, RateClassification> expectedMap = new HashMap<>();
    expectedMap.put("A", getRateClassification("A", "5",
        "Flex", "Pay on arrival. Fully refundable up to 1pm on arrival day."));
    expectedMap.put("S", getRateClassification("S", "2", "Standard",
        "Pay on arrival. Change arrival date. Non-refundable"));
    expectedMap.put("R", getRateClassification("R", "1", "non-Flex",
        "Pay on arrival. No change"));

    Map<String, RateClassification> map = contentClientLookupSvc
        .getRateClassifications(hotelBrand, LANGUAGE_CODE, null, COUNTRY_NULL);

    Set<String> mapKeySet = map.keySet();
    Set<String> expectedMapKeySet = expectedMap.keySet();
    assertThat(mapKeySet).isEqualTo(expectedMapKeySet);

  }

  private RateClassificationsList getEmptyRateClassificationsList() {

    return RateClassificationsList.builder().rateClassifications(Collections.emptyList()).build();
  }

  private RateClassificationsList getRateClassifications() {

    return RateClassificationsList.builder().rateClassifications(new ArrayList<>(
        Arrays.asList(getRateClassification("A", "5", "Flex",
                "Pay on arrival. Fully refundable up to 1pm on arrival day."),
            getRateClassification("S", "2", "Standard",
                "Pay on arrival. Change arrival date. Non-refundable"),
            getRateClassification("R", "1", "non-Flex",
                "Pay on arrival. No change"))
    )).build();
  }

  private RateClassification getRateClassification(String classification, String order, String name,
      String description) {
    return RateClassification.builder()
        .classification(classification)
        .description(description)
        .name(name)
        .order(order)
        .build();
  }

}
