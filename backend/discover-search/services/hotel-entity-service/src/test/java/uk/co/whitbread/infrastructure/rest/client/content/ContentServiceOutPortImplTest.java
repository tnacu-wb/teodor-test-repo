package uk.co.whitbread.infrastructure.rest.client.content;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.searchrules.out.RoomOccupancy;
import uk.co.whitbread.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.hotel.content.generated.models.AcceptedRoomTypesDto;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.content.generated.models.SearchRulesDto;
import uk.co.whitbread.infrastructure.rest.client.cache.exceptions.ContentServiceException;
import uk.co.whitbread.infrastructure.rest.client.content.mapper.SearchRulesResponseMapper;
import wiremock.org.hamcrest.MatcherAssert;

@ExtendWith(MockitoExtension.class)
class ContentServiceOutPortImplTest {

  public static final String CHANNEL_PI = "PI";
  public static final String BRAND_HUB = "HUB";
  private static final String CONTENT_ENTITY_ERROR = "An error was returned by Content Entity";
  private static final String COUNTRY = "UK";
  private static final String LANGUAGE = "EN";
  private static final String HOTEL_ID = "EDIPAR";
  @Mock
  private ContentServiceClient contentServiceClient;
  @Mock
  private CacheSearchOutPort cacheSearchOutPort;
  @Mock
  private SearchRulesResponseMapper searchRulesResponseMapper;

  @InjectMocks
  private ContentServiceOutPortImpl contentServiceOutPort;

  @Test
  void triggerHotelFacilityFilterUpdate__ShouldReturnOK() {
    //Arrange

    //Act
    contentServiceOutPort.triggerHotelFacilityFilterUpdate();

    //Assert
    verify(contentServiceClient, times(1)).triggerHotelFacilitiesFilterUpdate();
    assertDoesNotThrow(() -> contentServiceOutPort.triggerHotelFacilityFilterUpdate());
  }

  @Test
  void triggerHotelsOpeningSoonCacheUpdate__ShouldReturnOK() {
    //Arrange

    //Act
    contentServiceOutPort.triggerHotelsOpeningSoonCacheUpdate();

    //Assert
    verify(contentServiceClient, times(1)).triggerHotelsOpeningSoonCacheUpdate();
    assertDoesNotThrow(() -> contentServiceOutPort.triggerHotelsOpeningSoonCacheUpdate());
  }


  @Test
  void getCheckInResponse__ShouldReturnOK() {
    //Arrange
    when(contentServiceOutPort.getHotelBrand(anyString())).thenReturn("PI");

    //Act
    var hotelBrand = contentServiceOutPort.getHotelBrand(HOTEL_ID);

    //Assert
    assertThat(hotelBrand, notNullValue());
  }

  @Test
  void getSearchRules__ShouldReturnOK() {
    //Arrange
    when(contentServiceClient.getSearchRules(any(), any())).thenReturn(createSearchRulesResponseDto());
    when(searchRulesResponseMapper.toModel(any(SearchRulesDto.class))).thenReturn(createSearchRulesResponse());
    //Act
    var searchRules = contentServiceOutPort.getSearchRules(CHANNEL_PI, Optional.of(BRAND_HUB));

    //Assert
    verify(contentServiceClient, times(1)).getSearchRules(CHANNEL_PI, Optional.of(BRAND_HUB));
    assertDoesNotThrow(() -> contentServiceOutPort.getSearchRules(CHANNEL_PI, Optional.of(BRAND_HUB)));
    MatcherAssert.assertThat(searchRules.getMaxRooms(), is(4));
  }

  @Test
  void getSearchRules__ShouldThrowException() {
    //Arrange
    when(contentServiceClient.getSearchRules(any(), any()))
        .thenThrow(new ContentServiceException("message", CONTENT_ENTITY_ERROR, new Exception(), 1));

    //Act
    ContentServiceException exception = Assertions.assertThrows(ContentServiceException.class,
        () -> contentServiceOutPort.getSearchRules(CHANNEL_PI, Optional.of(BRAND_HUB)));

    // Assert
    assertEquals(CONTENT_ENTITY_ERROR, exception.getMessage());
  }

  @Test
  void getGlobalConfig__ShouldReturnOK() {
    // Arrange
    when(contentServiceClient.getGlobalConfig(COUNTRY, LANGUAGE)).thenReturn(new GlobalConfigDto());

    // Act
    var globalConfig = contentServiceOutPort.getGlobalConfig(COUNTRY, LANGUAGE);

    // Assert
    verify(contentServiceClient).getGlobalConfig(COUNTRY, LANGUAGE);
    assertThat(globalConfig, notNullValue());
  }

  @Test
  void getHotelInformation__ShouldReturnOK() {
    // Arrange
    var mockHotelInfo = new HotelInformationExtendedDto();
    mockHotelInfo.setHotelId(HOTEL_ID);
    when(contentServiceClient.getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID)).thenReturn(mockHotelInfo);
    when(cacheSearchOutPort.getHotelInformationFromCache(any(), any(), any())).thenReturn(null);

    // Act
    var hotelInfo = contentServiceOutPort.getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID);

    // Assert
    assertThat(hotelInfo, notNullValue());
    assertEquals(HOTEL_ID, hotelInfo.getHotelId());
    verify(contentServiceClient).getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID);
  }

  @Test
  void getHotelInformation__ReturnsFromCache_WhenCacheHit() {
    // Arrange
    var cachedDto = new HotelInformationExtendedDto();
    cachedDto.setHotelId(HOTEL_ID);

    when(cacheSearchOutPort.getHotelInformationFromCache(COUNTRY, LANGUAGE, HOTEL_ID)).thenReturn(cachedDto);

    // Act
    var result = contentServiceOutPort.getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID);

    // Assert
    assertThat(result, notNullValue());
    assertEquals(HOTEL_ID, result.getHotelId());
    verify(cacheSearchOutPort).getHotelInformationFromCache(COUNTRY, LANGUAGE, HOTEL_ID);
    verify(contentServiceClient, times(0)).getHotelInformation(any(), any(), any());
  }

  @Test
  void getHotelInformation__ReturnsFromRemote_WhenCacheMiss() {
    // Arrange
    when(cacheSearchOutPort.getHotelInformationFromCache(COUNTRY, LANGUAGE, HOTEL_ID)).thenReturn(null);

    var remoteDto = new HotelInformationExtendedDto();
    remoteDto.setHotelId(HOTEL_ID);
    when(contentServiceClient.getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID)).thenReturn(remoteDto);

    // Act
    var result = contentServiceOutPort.getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID);

    // Assert
    assertThat(result, notNullValue());
    assertEquals(HOTEL_ID, result.getHotelId());
    verify(cacheSearchOutPort).getHotelInformationFromCache(COUNTRY, LANGUAGE, HOTEL_ID);
    verify(contentServiceClient).getHotelInformation(COUNTRY, LANGUAGE, HOTEL_ID);
  }

  private SearchRulesDto createSearchRulesResponseDto() {
    var searchRulesDto = new SearchRulesDto();
    searchRulesDto.setMaxRooms(4);
    searchRulesDto.setMaxNights(9);
    searchRulesDto.setMaxArrivalDate(364);

    var roomOccupancyDto = new AcceptedRoomTypesDto();
    roomOccupancyDto.acceptedRoomTypes(List.of("FAM"));
    roomOccupancyDto.adultsNumber(1);
    roomOccupancyDto.childrenNumber(1);
    searchRulesDto.setRoomOccupancies(List.of(roomOccupancyDto));

    return searchRulesDto;
  }

  private SearchRules createSearchRulesResponse() {
    return SearchRules.builder()
        .maxRooms(4)
        .maxNights(9)
        .maxArrivalDate(364)
        .roomOccupancies(List.of(RoomOccupancy
            .builder()
            .acceptedRoomTypes(List.of("FAM"))
            .adultsNumber(1)
            .childrenNumber(1)
            .build()))
        .build();
  }
}
