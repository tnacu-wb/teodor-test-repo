package uk.co.whitbread.content.infrastructure.rest.controller.pricefinder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.pricefinder.in.PriceFinderGlobalConfigRequest;
import uk.co.whitbread.content.domain.model.pricefinder.out.Destinations;
import uk.co.whitbread.content.domain.model.pricefinder.out.HotelCodes;
import uk.co.whitbread.content.domain.model.pricefinder.out.InfoMessages;
import uk.co.whitbread.content.domain.model.pricefinder.out.Locations;
import uk.co.whitbread.content.domain.model.pricefinder.out.SeoHreflangs;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderConfig;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderGlobalConfig;
import uk.co.whitbread.content.domain.model.pricefinder.out.PriceFinderViews;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.in.PriceFinderGlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.DestinationsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.HotelCodesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.InfoMessagesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.LocationsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderViewsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.SeoHreflangsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.out.PriceFinderGlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.pricefinder.model.mapper.PriceFinderGlobalConfigDtoMapper;


import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class PriceFinderGlobalConfigControllerTest {


  @InjectMocks
  PriceFinderGlobalConfigController priceFinderConfigControllerUnderTest;

  @Mock
  private ContentInPort contentInPort;

  @Mock
  PriceFinderGlobalConfigDtoMapper priceFinderGlobalConfigDtoMapper;


  private static PriceFinderGlobalConfigRequest getPriceFinderGlobalConfigRequest() {
    return PriceFinderGlobalConfigRequest.builder()
        .country("gb").language("en").channelId("DISTR").brand("PI").build();
  }


  @Test
  void getPriceFinderConfig__ShouldReturnConfigData() {
    //Arrange
    PriceFinderGlobalConfigRequestDto priceFinderGlobalConfigRequestDto = getPriceFinderGlobalConfigRequestDto();

    PriceFinderGlobalConfigRequest priceFinderGlobalConfigRequest = getPriceFinderGlobalConfigRequest();

    Mockito.when(priceFinderGlobalConfigDtoMapper.toDomainModel(priceFinderGlobalConfigRequestDto))
            .thenReturn(priceFinderGlobalConfigRequest);
    Mockito.when(contentInPort.getPriceFinderConfig(priceFinderGlobalConfigRequest)).thenReturn(getPriceFinderGlobalConfig());
    Mockito.when(priceFinderGlobalConfigDtoMapper.toDtoModel(getPriceFinderGlobalConfig())).thenReturn(getPriceFinderGlobalConfigDto());

    //Act
    final ResponseEntity<PriceFinderGlobalConfigDto> response =
            priceFinderConfigControllerUnderTest.getPriceFinderGlobalConfig(priceFinderGlobalConfigRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    final PriceFinderGlobalConfigDto priceFinderGlobalConfigDto = response.getBody();
    assertThat(priceFinderGlobalConfigDto, notNullValue());
    final List<PriceFinderViewsDto> priceFinderViewsDto = priceFinderGlobalConfigDto.getPriceFinderConfig()
            .getPriceFinderViews();
    assertThat(priceFinderViewsDto, hasSize(2));
    final PriceFinderViewsDto priceFinderViewsDto1 = priceFinderViewsDto.get(0);
    assertThat(priceFinderViewsDto1.getBannerColour(), is("#FFFFFF"));

  }


  private static PriceFinderGlobalConfigRequestDto getPriceFinderGlobalConfigRequestDto() {
    return PriceFinderGlobalConfigRequestDto.builder()
        .country("gb").language("en").channelId("DISTR").brand("PI").build();
  }



  private PriceFinderGlobalConfig getPriceFinderGlobalConfig() {
    HotelCodes hotelCodes1 = HotelCodes.builder().code("LONHOL").order(1).build();
    HotelCodes hotelCodes2 = HotelCodes.builder().code("FRAMTI").order(2).build();

    InfoMessages infoMessages1 = InfoMessages.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessages infoMessages2 = InfoMessages.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    Locations locations1  = Locations.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationOrder(30)
            .build();
    Locations locations2 = Locations.builder()
            .locationId("ChIJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Manchester")
            .locationOrder(30)
            .build();
    SeoHreflangs seoHreflangs1 = SeoHreflangs.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangs seoHreflangs2 = SeoHreflangs.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    Destinations destinations1 = Destinations.builder()
            .locations(List.of(locations1,locations2)).build();

    PriceFinderViews priceFinderViews1 = PriceFinderViews.builder()
            .path("gb/en/price-finder/campaign/test")
            .bannerImage("/image")
            .bannerColour("#FFFFFF")
            .bannerHeadline("bannerHeadline")
            .bannerSubtext("bannerSubtext")
            .termsLabel("termsLabel")
            .dateRangeStart("31/01/2025")
            .dateRangeEnd("31/12/2025")
            .checkinDate("31/11/2025")
            .highlightedPriceRangeStep(5)
            .highlightedPriceRangeMin(45)
            .highlightedPriceRangeMax(80)
            .highlightedPricePrimaryColour("#AAAAAA")
            .highlightedPriceSecondaryColour("#BBBBBB")
            .filterRoomSelected("DB")
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(30.0)
            .hotelCodes(List.of(hotelCodes1, hotelCodes2))
            .destinations(List.of(destinations1))
            .infoMessages(List.of(infoMessages1, infoMessages2))
            .seoMetaTitle("seoMetaTitle")
            .seoMetaDescription("seoMetaDescription")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots")
            .seoHreflangs(List.of(seoHreflangs1, seoHreflangs2))
            .build();

    HotelCodes hotelCodes3 = HotelCodes.builder().code("LONFOL").order(3).build();
    HotelCodes hotelCodes4 = HotelCodes.builder().code("FTAMTI").order(4).build();
    InfoMessages infoMessages3 = InfoMessages.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessages infoMessages4 = InfoMessages.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    Locations locations3 = Locations.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("LiverPool")
            .locationOrder(3)
            .build();
    Locations locations4 = Locations.builder()
            .locationId("Ch4IJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Edinburgh")
            .locationOrder(4)
            .build();
    SeoHreflangs seoHreflangs3 = SeoHreflangs.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangs seoHreflangs4 = SeoHreflangs.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    Destinations destinations2 = Destinations.builder()
            .locations(List.of(locations3,locations4)).build();

    PriceFinderViews priceFinderViews2 = PriceFinderViews.builder()
            .path("gb/en/price-finder/campaign/test")
            .bannerImage("/image1")
            .bannerColour("#FFFcFF")
            .bannerHeadline("bannerHeadline1")
            .bannerSubtext("bannerSubtext1")
            .termsLabel("termsLabel1")
            .dateRangeStart("3/01/2025")
            .dateRangeEnd("1/12/2025")
            .checkinDate("3/11/2025")
            .highlightedPriceRangeStep(3)
            .highlightedPriceRangeMin(25)
            .highlightedPriceRangeMax(90)
            .highlightedPricePrimaryColour("#AA3AAAA")
            .highlightedPriceSecondaryColour("#BBB3BBB")
            .filterRoomSelected("DB")
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(40.0)
            .hotelCodes(List.of(hotelCodes3, hotelCodes4))
            .destinations(List.of(destinations2))
            .infoMessages(List.of(infoMessages3, infoMessages4))
            .seoMetaTitle("seoMetaTitle3")
            .seoMetaDescription("seoMetaDescription4")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots1")
            .seoHreflangs(List.of(seoHreflangs3, seoHreflangs4))
            .build();
    PriceFinderConfig priceFinderConfig = PriceFinderConfig.builder()
            .priceFinderViews(List.of(priceFinderViews1, priceFinderViews2)).build();
    return PriceFinderGlobalConfig.builder().priceFinderConfig(priceFinderConfig).build();
  }

  private PriceFinderGlobalConfigDto getPriceFinderGlobalConfigDto() {
    HotelCodesDto hotelCodesDto1 = HotelCodesDto.builder().code("LONHOL").order(1).build();
    HotelCodesDto hotelCodesDto2 = HotelCodesDto.builder().code("FRAMTI").order(2).build();

    InfoMessagesDto infoMessagesDto1 = InfoMessagesDto.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessagesDto infoMessagesDto2 = InfoMessagesDto.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    LocationsDto locationsdto1     = LocationsDto.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationOrder(30)
            .build();
    LocationsDto locationsdto2     = LocationsDto.builder()
            .locationId("ChIJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Manchester")
            .locationOrder(30)
            .build();
    SeoHreflangsDto seoHreflangsDto1 = SeoHreflangsDto.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangsDto seoHreflangsDto2 = SeoHreflangsDto.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    DestinationsDto destinationsDto1 = DestinationsDto.builder()
            .locations(List.of(locationsdto1,locationsdto2)).build();

    PriceFinderViewsDto priceFinderViewsDto1 = PriceFinderViewsDto.builder()
            .path("gb/en/price-finder/campaign/test")
            .bannerImage("/image")
            .bannerColour("#FFFFFF")
            .bannerHeadline("bannerHeadline")
            .bannerSubtext("bannerSubtext")
            .termsLabel("termsLabel")
            .dateRangeStart("31/01/2025")
            .dateRangeEnd("31/12/2025")
            .checkinDate("31/11/2025")
            .highlightedPriceRangeStep(5)
            .highlightedPriceRangeMin(45)
            .highlightedPriceRangeMax(80)
            .highlightedPricePrimaryColour("#AAAAAA")
            .highlightedPriceSecondaryColour("#BBBBBB")
            .filterRoomSelected("DB")
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(32.34)
            .hotelCodes(List.of(hotelCodesDto1, hotelCodesDto2))
            .destinations(List.of(destinationsDto1))
            .infoMessages(List.of(infoMessagesDto1, infoMessagesDto2))
            .seoMetaTitle("seoMetaTitle")
            .seoMetaDescription("seoMetaDescription")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots")
            .seoHreflangs(List.of(seoHreflangsDto1, seoHreflangsDto2))
            .build();

    HotelCodesDto hotelCodesDto3 = HotelCodesDto.builder().code("LONFOL").order(3).build();
    HotelCodesDto hotelCodesDto4 = HotelCodesDto.builder().code("FTAMTI").order(4).build();
    InfoMessagesDto infoMessagesDto3 = InfoMessagesDto.builder()
            .messageTitle("messageTitle")
            .messageSubtitle("messageSubtitle")
            .messageType("messageType")
            .messageOrder(1).build();
    InfoMessagesDto infoMessagesDto4 = InfoMessagesDto.builder()
            .messageTitle("messageTitle2")
            .messageSubtitle("messageSubtitle2")
            .messageType("messageType2")
            .messageOrder(2).build();
    LocationsDto locationsdto3     = LocationsDto.builder()
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("LiverPool")
            .locationOrder(3)
            .build();
    LocationsDto locationsdto4     = LocationsDto.builder()
            .locationId("Ch4IJ2_UmUkxNekgRqmv-BDgUvtk")
            .locationName("Edinburgh")
            .locationOrder(4)
            .build();
    SeoHreflangsDto seoHreflangsDto3 = SeoHreflangsDto.builder()
            .hreflang("en-gb")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test")
            .build();
    SeoHreflangsDto seoHreflangsDto4 = SeoHreflangsDto.builder()
            .hreflang("en-gb1")
            .href("https://www.hotel.com/gb/en/price-finder/campaign/test1")
            .build();
    DestinationsDto destinationsDto2 = DestinationsDto.builder()
            .locations(List.of(locationsdto3,locationsdto4)).build();

    PriceFinderViewsDto priceFinderViewsDto2 = PriceFinderViewsDto.builder()
            .path("gb/en/price-finder/campaign/test")
            .bannerImage("/image1")
            .bannerColour("#FFFcFF")
            .bannerHeadline("bannerHeadline1")
            .bannerSubtext("bannerSubtext1")
            .termsLabel("termsLabel1")
            .dateRangeStart("3/01/2025")
            .dateRangeEnd("1/12/2025")
            .checkinDate("3/11/2025")
            .highlightedPriceRangeStep(3)
            .highlightedPriceRangeMin(25)
            .highlightedPriceRangeMax(90)
            .highlightedPricePrimaryColour("#AA3AAAA")
            .highlightedPriceSecondaryColour("#BBB3BBB")
            .filterRoomSelected("DB")
            .locationId("ChIJdd4hrwug2EcRmSrV3Vo6llI")
            .locationName("London")
            .locationRadius(31.15)
            .hotelCodes(List.of(hotelCodesDto3, hotelCodesDto4))
            .destinations(List.of(destinationsDto2))
            .infoMessages(List.of(infoMessagesDto3, infoMessagesDto4))
            .seoMetaTitle("seoMetaTitle3")
            .seoMetaDescription("seoMetaDescription4")
            .seoCardImageUrl("seoCardImageUrl")
            .seoRobots("seoRobots1")
            .seoHreflangs(List.of(seoHreflangsDto3, seoHreflangsDto4))
            .build();
    PriceFinderConfigDto priceFinderConfigDto = PriceFinderConfigDto.builder()
            .priceFinderViews(List.of(priceFinderViewsDto1, priceFinderViewsDto2)).build();
    return PriceFinderGlobalConfigDto.builder().priceFinderConfig(priceFinderConfigDto).build();
  }

}
