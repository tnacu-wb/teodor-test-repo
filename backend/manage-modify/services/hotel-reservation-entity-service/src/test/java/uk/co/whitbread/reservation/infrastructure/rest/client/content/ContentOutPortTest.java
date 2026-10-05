package uk.co.whitbread.reservation.infrastructure.rest.client.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelInformationExtendedDto;
import uk.co.whitbread.content.entity.service.generated.models.content.HotelPaymentInformationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.IndexHeaderDataDto;
import uk.co.whitbread.content.entity.service.generated.models.content.SearchRulesDto;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.AcceptedCreditCard;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.BookingSearch;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.Config;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.Cookie;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.DashboardRedirect;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.reservation.domain.ports.secondary.CacheOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.HotelInformationMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.HotelPaymentInformationMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.IndexHeaderResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.mapper.SearchRulesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.service.ContentClient;

@ExtendWith(MockitoExtension.class)
class ContentOutPortTest {

  public static final String LONEUS = "LONEUS";
  public static final String PI = "PI";
  public static final String COUNTRY = "gb";
  public static final String LANGUAGE = "en";
  @InjectMocks
  private ContentOutPortImpl contentOutPort;

  @Mock
  private ContentClient contentClient;

  @Mock
  private IndexHeaderResponseMapper indexHeaderResponseMapper;

  @Mock
  private HotelPaymentInformationMapper hotelPaymentInformationMapper;

  @Mock
  private HotelInformationMapper hotelInformationMapper;

  @Mock
  private SearchRulesResponseMapper searchRulesResponseMapper;

  @Mock
  private CacheOutPort cacheOutPort;

  @Test
  void testIndexHeader_success() {
    //Arrange
    when(contentClient.getIndexHeaderData(COUNTRY, LANGUAGE)).thenReturn(new IndexHeaderDataDto());
    when(indexHeaderResponseMapper.toModel(any(IndexHeaderDataDto.class))).thenReturn(
        getIndexHeaderData());

    //Act
    var response = contentOutPort.getIndexHeaderData(COUNTRY, LANGUAGE);

    //Assert
    assertNotNull(response);
    assertEquals(
        response.getConfig().getBookingSearch().getDashboardRedirect().getCookie().getName(),
        "vanillaBiscuit");
  }

  @Test
  void getHotelPaymentInformation_success() {
    //Arrange
    when(contentClient.getHotelPaymentInformation(anyString(), anyString(), anyString()))
        .thenReturn(new HotelPaymentInformationDto());
    when(hotelPaymentInformationMapper.toModel(any(HotelPaymentInformationDto.class))).thenReturn((
        getPaymentData()
    ));

    //Act
    var response = contentOutPort.getHotelPaymentInformation("HOTELTEST", COUNTRY, LANGUAGE);

    //Assert
    assertNotNull(response);
  }

  @Test
  void getHotelInformation_success() {
    //Arrange
    when(contentClient.getHotelInformation(anyString(), anyString(), anyString()))
        .thenReturn(new HotelInformationExtendedDto());
    when(hotelInformationMapper.toModel(any(HotelInformationExtendedDto.class))).thenReturn((
        new HotelInfoResponse(LONEUS, PI, PI, null)
    ));
    when(cacheOutPort.getHotelInformationFromCache(LONEUS, COUNTRY, LANGUAGE))
        .thenReturn(null);

    //Act
    var response = contentOutPort.getHotelInformation(LONEUS, COUNTRY, LANGUAGE);

    //Assert
    assertNotNull(response);
    assertEquals(PI, response.getBrand());
  }

  @Test
  void getHotelInformation_fromCache_success() {
    // Arrange
    var cachedDto = new HotelInformationExtendedDto();
    var expectedResponse = new HotelInfoResponse(LONEUS, PI, PI, null);

    when(cacheOutPort.getHotelInformationFromCache(LONEUS, COUNTRY, LANGUAGE))
        .thenReturn(cachedDto);
    when(hotelInformationMapper.toModel(cachedDto)).thenReturn(expectedResponse);

    // Act
    HotelInfoResponse response = contentOutPort.getHotelInformation(LONEUS, COUNTRY, LANGUAGE);

    // Assert
    assertNotNull(response);
    assertEquals(expectedResponse, response);
  }

  @Test
  void getSearchRules_success() {
    //Arrange
    when(contentClient.getSearchRules(anyString(), any())).thenReturn(new SearchRulesDto());
    when(searchRulesResponseMapper.toModel(any(SearchRulesDto.class))).thenReturn((
        new SearchRules(364, 4, 9, 2, null)
    ));

    //Act
    var response = contentOutPort.getSearchRules("DISTR", Optional.of(PI));

    //Assert
    assertNotNull(response);
    assertEquals(364, response.getMaxArrivalDate());
    assertEquals(4, response.getMaxNights());
    assertEquals(9, response.getMaxRooms());
    assertEquals(2, response.getMaxRoomsAmend());
  }

  private IndexHeaderData getIndexHeaderData() {
    return IndexHeaderData.builder().config(
        Config.builder().bookingSearch(
            BookingSearch.builder().dashboardRedirect(DashboardRedirect.builder().cookie(
                Cookie.builder().name("vanillaBiscuit").build()).build()).build()).build()).build();
  }

  private HotelPaymentInformation getPaymentData() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(
            List.of(AcceptedCreditCard.builder().codeOpera("BU").codeOperaCardType("ZZ").build()))
        .build();
  }
}