package uk.co.whitbread.content.infrastructure.rest.controller.hotel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.hotel.in.HotelInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelShortInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.in.HotelsInformationRequest;
import uk.co.whitbread.content.domain.model.hotel.out.AcceptedCreditCard;
import uk.co.whitbread.content.domain.model.hotel.out.AccessibilityInfo;
import uk.co.whitbread.content.domain.model.hotel.out.Address;
import uk.co.whitbread.content.domain.model.hotel.out.Breadcrumb;
import uk.co.whitbread.content.domain.model.hotel.out.FactItem;
import uk.co.whitbread.content.domain.model.hotel.out.Facts;
import uk.co.whitbread.content.domain.model.hotel.out.GalleryImage;
import uk.co.whitbread.content.domain.model.hotel.out.HotelFacility;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformationExtended;
import uk.co.whitbread.content.domain.model.hotel.out.HotelPaymentInformation;
import uk.co.whitbread.content.domain.model.hotel.out.ImportantInfo;
import uk.co.whitbread.content.domain.model.hotel.out.InfoItem;
import uk.co.whitbread.content.domain.model.hotel.out.MessagingFlag;
import uk.co.whitbread.content.domain.model.hotel.out.PaymentMethod;
import uk.co.whitbread.content.domain.model.hotel.out.PaymentProvider;
import uk.co.whitbread.content.domain.model.hotel.out.Restaurant;
import uk.co.whitbread.content.domain.model.hotel.out.RoomConfiguration;
import uk.co.whitbread.content.domain.model.hotel.out.TabItem;
import uk.co.whitbread.content.domain.model.hotel.out.ThumbnailImage;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper.HotelInformationDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper.HotelInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper.HotelsInformationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelShortInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.HotelsInformationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in.SlugRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.AcceptedCreditCardDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.AccessibilityInfoDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.AddressDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.FlagDetailDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.GalleryImageDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelFacilityDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelFlagsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelInformationExtendedDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelPaymentInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.ImportantInfoDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.InfoItemDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.MessagingFlagDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.PaymentMethodDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.PaymentProviderDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.MenuDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.RestaurantDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.RoomClassConfigurationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.RoomConfigurationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TabItemDto;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.ThumbnailImageDto;


@ExtendWith(MockitoExtension.class)
class HotelInformationControllerTest {

  private static final String HOTEL_ID_EDIPAR = "EDIPAR";
  private static final String HOTEL_ID_LONEUS = "LONEUS";
  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final Double LONGITUDE = 33.2;
  private static final Double LATITUDE = 12.5;
  private static final String SLUG_LONDON_KINGS_CROSS =
      "england/greater-london/london/hub-london-kings-cross";

  @InjectMocks
  private HotelInformationController hotelInformationControllerUnderTest;
  @Mock
  private ContentInPort contentInPort;
  @Mock
  private CacheManager cacheManager1Hour;
  @Mock
  private HotelInformationRequestDtoMapper hotelInformationRequestDtoMapper;
  @Mock
  private HotelsInformationRequestDtoMapper hotelsInformationRequestDtoMapper;
  @Mock
  private HotelInformationDtoMapper hotelInformationDtoMapper;

  @Test
  void getHotelInformation__ShouldReturnOK() {
    //Arrange
    var hotelInformationRequestDto = getHotelInformationRequestDto();
    hotelInformationRequestDto.setCountry(COUNTRY_GB);
    hotelInformationRequestDto.setLanguage(LANGUAGE_EN);
    var hotelInformationRequest = getHotelInformationRequest();
    Mockito.when(
            hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, hotelInformationRequestDto))
        .thenReturn(hotelInformationRequest);
    Mockito.when(contentInPort.getHotelInformation(hotelInformationRequest))
        .thenReturn(getHotelInformationExtended());
    Mockito.when(hotelInformationDtoMapper.toDto(getHotelInformationExtended()))
        .thenReturn(getBookingInformationExtendedDto());

    //act
    var request = hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR,
        hotelInformationRequestDto);
    var hotelInformationDto = hotelInformationDtoMapper.toDto(
        contentInPort.getHotelInformation(request));
    final ResponseEntity<HotelInformationExtendedDto> response =
        hotelInformationControllerUnderTest.getHotelInformation(
            HOTEL_ID_EDIPAR, hotelInformationRequestDto, null, null);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), hotelInformationDto.getGalleryImages().get(0).getImageSrc(),
        response.getBody().getGalleryImages().get(0).getImageSrc());
    assertEquals(response.toString(),
        hotelInformationDto.getGalleryImages().get(0).getThumbnailSrc(),
        response.getBody().getGalleryImages().get(0).getThumbnailSrc());
    assertEquals(response.toString(), hotelInformationDto.getHotelFacilities().get(0).getCode(),
        response.getBody().getHotelFacilities().get(0).getCode());
    assertEquals(response.toString(), hotelInformationDto.getHotelFacilities().get(0).getName(),
        response.getBody().getHotelFacilities().get(0).getName());
    assertEquals(response.toString(), hotelInformationDto.getHotelFacilities().get(0).getIcon(),
        response.getBody().getHotelFacilities().get(0).getIcon());
    assertEquals(response.toString(), hotelInformationDto.getTransportInformation().get(0),
        response.getBody().getTransportInformation().get(0));
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getRoomType(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getRoomType());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getRoomName(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getRoomName());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getRoomDescription(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getRoomDescription());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getFacilities().get(0)
            .getName(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getFacilities().get(0)
            .getName());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getImages().get(0)
            .getImageSrc(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getImages().get(0)
            .getImageSrc());
    assertEquals(response.toString(),
        hotelInformationDto.getImportantInfo().getTitle(),
        response.getBody().getImportantInfo().getTitle());
    assertEquals(response.toString(),
        hotelInformationDto.getImportantInfo().getInfoItems().get(0).getText(),
        response.getBody().getImportantInfo().getInfoItems().get(0).getText());
    assertEquals(response.toString(),
        hotelInformationDto.getImportantInfo().getInfoItems().get(0).getPriority(),
        response.getBody().getImportantInfo().getInfoItems().get(0).getPriority());
    assertEquals(response.toString(),
        hotelInformationDto.getImportantInfo().getInfoItems().get(0).getStartDate(),
        response.getBody().getImportantInfo().getInfoItems().get(0).getStartDate());
    assertEquals(response.toString(),
        hotelInformationDto.getImportantInfo().getInfoItems().get(0).getEndDate(),
        response.getBody().getImportantInfo().getInfoItems().get(0).getEndDate());
    assertEquals(response.toString(), hotelInformationDto.getAccessibilityInfo().getHeader(),
        "headerTest");
    assertEquals(response.toString(), hotelInformationDto.getAccessibilityInfo().getText(),
        "All accessible rooms at this hotel are double rooms.");
    assertEquals(response.toString(), hotelInformationDto.getAccessibilityInfo().getLinkText(),
        "linkTextTest");
    assertEquals(response.toString(), hotelInformationDto.getAccessibilityInfo().getPhoneNumber(),
        "3527934510");
    assertEquals(response.toString(), "test-county", hotelInformationDto.getCounty());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomClassConfiguration().get(0).getCode(),
        response.getBody().getRoomClassConfiguration().get(0).getCode());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomClassConfiguration().get(0).getTitle(),
        response.getBody().getRoomClassConfiguration().get(0).getTitle());
  }

  @Test
  void getHotelInformation_cacheHit_returnsCachedDto() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();
    var cachedDto = getBookingInformationExtendedDto();
    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);

    // Mock static utility
    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest,
              HOTEL_ID_EDIPAR))
          .thenReturn(Optional.of(cachedDto));

      // Act
      ResponseEntity<HotelInformationExtendedDto> response =
          hotelInformationControllerUnderTest.getHotelInformation(HOTEL_ID_EDIPAR, requestDto, null, null);

      // Assert
      Assertions.assertEquals(200, response.getStatusCode().value());
      assertSame(cachedDto, response.getBody());
      // Domain port and mapper should not be called
      verify(contentInPort, never()).getHotelInformation(any());
      verify(hotelInformationDtoMapper, never()).toDto(any(HotelInformationExtended.class));
      cacheUtilsMock.verify(() -> HotelInformationCacheUtils.storeValueInCache(any(), any(), any(), any()), never());
    }
  }

  @Test
  void getHotelInformation_cacheMiss_returnsMappedDtoAndStoresInCache() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();
    var domainModel = getHotelInformationExtended();
    var mappedDto = getBookingInformationExtendedDto();
    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);

    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(domainModel);
    when(hotelInformationDtoMapper.toDto(domainModel)).thenReturn(mappedDto);

    // Mock static utility
    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest,
              HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.storeValueInCache(cacheManager1Hour, domainRequest,
              HOTEL_ID_EDIPAR, mappedDto))
          .thenCallRealMethod();

      // Act
      ResponseEntity<HotelInformationExtendedDto> response =
          hotelInformationControllerUnderTest.getHotelInformation(HOTEL_ID_EDIPAR, requestDto, null, null);

      // Assert
      Assertions.assertEquals(200, response.getStatusCode().value());
      assertSame(mappedDto, response.getBody());
      verify(contentInPort).getHotelInformation(domainRequest);
      verify(hotelInformationDtoMapper).toDto(domainModel);
      cacheUtilsMock.verify(() -> HotelInformationCacheUtils.storeValueInCache(cacheManager1Hour, domainRequest,
          HOTEL_ID_EDIPAR, mappedDto));
    }
  }

  @Test
  void getHotelsInformation__ShouldReturnOK() {
    //Arrange
    var hotelsInformationRequestDto = HotelsInformationRequestDto.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    HotelsInformationRequest hotelsInformationRequest = HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    Mockito.when(
            hotelsInformationRequestDtoMapper.toDomainModel(hotelsInformationRequestDto))
        .thenReturn(hotelsInformationRequest);
    Mockito.when(contentInPort.getHotelsInformation(hotelsInformationRequest))
        .thenReturn(getHotelsInformation());
    Mockito.when(hotelInformationDtoMapper.toListDto(getHotelsInformation()))
        .thenReturn(getHotelsInformationDtoList());

    //act
    var request = hotelsInformationRequestDtoMapper.toDomainModel(hotelsInformationRequestDto);
    var hotelsInformationDtoList = hotelInformationDtoMapper.toListDto(
        contentInPort.getHotelsInformation(request));
    final ResponseEntity<List<HotelInformationDto>> response =
        hotelInformationControllerUnderTest.getHotelsInformation(hotelsInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody(), hasSize(2));
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getRoomConfiguration().getTabItems().get(0).getRoomType(),
        response.getBody().get(0).getRoomConfiguration().getTabItems().get(0).getRoomType());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getRoomConfiguration().getTabItems().get(0).getRoomName(),
        response.getBody().get(0).getRoomConfiguration().getTabItems().get(0).getRoomName());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getRoomConfiguration().getTabItems().get(0)
            .getRoomDescription(),
        response.getBody().get(0).getRoomConfiguration().getTabItems().get(0).getRoomDescription());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getRoomConfiguration().getTabItems().get(0).getFacilities()
            .get(0)
            .getName(),
        response.getBody().get(0).getRoomConfiguration().getTabItems().get(0).getFacilities().get(0)
            .getName());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getRoomConfiguration().getTabItems().get(0).getImages()
            .get(0)
            .getImageSrc(),
        response.getBody().get(0).getRoomConfiguration().getTabItems().get(0).getImages().get(0)
            .getImageSrc());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getHotelOpeningDate(),
        response.getBody().get(0).getHotelOpeningDate());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getMessagingFlag().getColor(),
        response.getBody().get(0).getMessagingFlag().getColor());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getMessagingFlag().getText(),
        response.getBody().get(0).getMessagingFlag().getText());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getThumbnailImages().size(),
        response.getBody().get(0).getThumbnailImages().size());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getImportantInfo().getTitle(),
        response.getBody().get(0).getImportantInfo().getTitle());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getImportantInfo().getInfoItems().get(0).getText(),
        response.getBody().get(0).getImportantInfo().getInfoItems().get(0).getText());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getImportantInfo().getInfoItems().get(0).getPriority(),
        response.getBody().get(0).getImportantInfo().getInfoItems().get(0).getPriority());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getImportantInfo().getInfoItems().get(0).getStartDate(),
        response.getBody().get(0).getImportantInfo().getInfoItems().get(0).getStartDate());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getImportantInfo().getInfoItems().get(0).getEndDate(),
        response.getBody().get(0).getImportantInfo().getInfoItems().get(0).getEndDate());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getAccessibilityInfo().getHeader(),
        "headerTest");
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getAccessibilityInfo().getText(),
        "All accessible rooms at this hotel are double rooms.");
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getAccessibilityInfo().getLinkText(),
        "linkTextTest");
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getAccessibilityInfo().getPhoneNumber(),
        "3527934510");
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getHotelFlags().getIsEnabled(),
        response.getBody().get(0).getHotelFlags().getIsEnabled());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getHotelFlags().getFlagOverlay().getText(),
        response.getBody().get(0).getHotelFlags().getFlagOverlay().getText());
    assertEquals(response.toString(),
        hotelsInformationDtoList.get(0).getHotelFlags().getFlagBanner().getText(),
        response.getBody().get(0).getHotelFlags().getFlagBanner().getText());
  }

  @Test
  void getHotelsInformation_calculateDistanceFromReference__ShouldReturnOK() {
    //Arrange
    var hotelsInformationRequestDto = HotelsInformationRequestDto.builder()
            .hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .latitudeRef(LATITUDE)
            .longitudeRef(LONGITUDE)
            .build();
    HotelsInformationRequest hotelsInformationRequest = HotelsInformationRequest.builder()
            .hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .latitudeRef(LATITUDE)
            .longitudeRef(LONGITUDE)
            .build();
    Mockito.when(hotelsInformationRequestDtoMapper.toDomainModel(hotelsInformationRequestDto))
            .thenReturn(hotelsInformationRequest);
    Mockito.when(contentInPort.getHotelsInformation(hotelsInformationRequest))
            .thenReturn(getHotelsInformation());
    Mockito.when(hotelInformationDtoMapper.toListDto(getHotelsInformation()))
            .thenReturn(getHotelsInformationDtoList());

    //act
    var request = hotelsInformationRequestDtoMapper.toDomainModel(hotelsInformationRequestDto);
    var hotelsInformationDtoList = hotelInformationDtoMapper.toListDto(
            contentInPort.getHotelsInformation(request));
    final ResponseEntity<List<HotelInformationDto>> response =
            hotelInformationControllerUnderTest.getHotelsInformation(hotelsInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody(), hasSize(2));
    assertEquals(response.toString(), hotelsInformationDtoList.get(0).getDistanceFromReference(), 34.2);
  }

  @Test
  void getHotelPaymentInformation__shouldReturnOK() {
    //Arrange
    HotelInformationRequestDto hotelInformationRequestDto = getHotelInformationRequestDto();
    HotelInformationRequest hotelInformationRequest = getHotelInformationRequest();
    Mockito.when(
            hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, hotelInformationRequestDto))
        .thenReturn(hotelInformationRequest);
    Mockito.when(contentInPort.getHotelPaymentInformation(hotelInformationRequest))
        .thenReturn(getHotelPaymentInformation());
    Mockito.when(hotelInformationDtoMapper.toHotelPaymentMethodDto(getHotelPaymentInformation()))
        .thenReturn(getHotelPaymentInformationDto());

    //act
    var request = hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR,
        hotelInformationRequestDto);
    var paymentInformation = contentInPort.getHotelPaymentInformation(request);
    var paymentInformationDto = hotelInformationDtoMapper.toHotelPaymentMethodDto(
        paymentInformation);
    final ResponseEntity<HotelPaymentInformationDto> response =
        hotelInformationControllerUnderTest.getHotelPaymentInformation(
            HOTEL_ID_EDIPAR, hotelInformationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), paymentInformationDto.getAddress().getAddressLine1(),
        response.getBody().getAddress().getAddressLine1());
    assertEquals(response.toString(), paymentInformationDto.getAddress().getAddressLine2(),
        response.getBody().getAddress().getAddressLine2());
    assertEquals(response.toString(), paymentInformationDto.getAddress().getAddressLine3(),
        response.getBody().getAddress().getAddressLine3());
    assertEquals(response.toString(), paymentInformationDto.getAddress().getCountry(),
        response.getBody().getAddress().getCountry());
    assertEquals(response.toString(), paymentInformationDto.getAddress().getPostalCode(),
        response.getBody().getAddress().getPostalCode());
    assertEquals(response.toString(),
        paymentInformationDto.getAcceptedCreditCards().get(0).getCode(),
        response.getBody().getAcceptedCreditCards().get(0).getCode());
    assertEquals(response.toString(),
        paymentInformationDto.getAcceptedCreditCards().get(0).getListOrder(),
        response.getBody().getAcceptedCreditCards().get(0).getListOrder());
    assertEquals(response.toString(),
        paymentInformationDto.getAcceptedCreditCards().get(0).getName(),
        response.getBody().getAcceptedCreditCards().get(0).getName());
    assertEquals(response.toString(),
        paymentInformationDto.getAcceptedCreditCards().get(0).getPaymentOnly(),
        response.getBody().getAcceptedCreditCards().get(0).getPaymentOnly());
    assertEquals(response.toString(),
        paymentInformationDto.getPaymentProviders().get(0).getProviderId(),
        response.getBody().getPaymentProviders().get(0).getProviderId());
  }

  @Test
  void triggerUpdateHotelsFacilitiesCache__ShouldReturnOk() {
    //Act
    var response = hotelInformationControllerUnderTest.triggerUpdateHotelsFacilitiesCache();

    //Assert
    verify(contentInPort, times(1)).updateHotelsFacilitiesCache();
  }

  @Test
  void triggerGetAllHotelsShortInfo() {
    var hotelShortInformationRequestDto = HotelShortInformationRequestDto.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    //Act
    Mockito.when(
        hotelInformationRequestDtoMapper.toDomainModel(hotelShortInformationRequestDto)).thenReturn(
        HotelShortInformationRequest.builder().country(COUNTRY_GB).language(LANGUAGE_EN).build()
    );
    var response = hotelInformationControllerUnderTest.getAllHotelsShortInformation(hotelShortInformationRequestDto);

    //Assert
    verify(contentInPort, times(1)).getAllHotelsShortInformation(COUNTRY_GB, LANGUAGE_EN);
  }

  @Test
  void triggerUpdateHotelsOpeningSoonCache() {
    //Act
    var response = hotelInformationControllerUnderTest.triggerUpdateHotelsOpeningSoonCache();

    //Assert
    verify(contentInPort, times(1)).updateHotelsOpeningSoonCache();
  }

  @Test
  void getHotelInformationBySlug__shouldFindInformationBySlug() {
    //Arrange
    HotelInformationRequestDto hotelInformationRequestDto = getHotelInformationRequestDto();
    hotelInformationRequestDto.setCountry(COUNTRY_GB);
    hotelInformationRequestDto.setLanguage(LANGUAGE_EN);
    HotelInformationRequest hotelInformationRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    var slugRequestDto = SlugRequestDto.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).build();

    Mockito.when(
            hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, hotelInformationRequestDto, null, null))
        .thenReturn(hotelInformationRequest);
    Mockito.when(contentInPort.getHotelInformationBySlug(hotelInformationRequest))
        .thenReturn(getHotelInformation());
    Mockito.when(hotelInformationDtoMapper.toDto(getHotelInformation()))
        .thenReturn(getBookingInformationDto());

    //act
    final var request = hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto,
        hotelInformationRequestDto, null, null);
    var hotelInformationDto = hotelInformationDtoMapper.toDto(
        contentInPort.getHotelInformationBySlug(hotelInformationRequest));
    final ResponseEntity<HotelInformationDto> response =
        hotelInformationControllerUnderTest.getHotelInformationBySlug(
            slugRequestDto, hotelInformationRequestDto, null, null);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), hotelInformationDto.getGalleryImages().get(0).getImageSrc(),
        response.getBody().getGalleryImages().get(0).getImageSrc());
    assertEquals(response.toString(),
        hotelInformationDto.getGalleryImages().get(0).getThumbnailSrc(),
        response.getBody().getGalleryImages().get(0).getThumbnailSrc());
    assertEquals(response.toString(), hotelInformationDto.getHotelFacilities().get(0).getCode(),
        response.getBody().getHotelFacilities().get(0).getCode());
    assertEquals(response.toString(), hotelInformationDto.getHotelFacilities().get(0).getName(),
        response.getBody().getHotelFacilities().get(0).getName());
    assertEquals(response.toString(), hotelInformationDto.getHotelFacilities().get(0).getIcon(),
        response.getBody().getHotelFacilities().get(0).getIcon());
    assertEquals(response.toString(), hotelInformationDto.getTransportInformation().get(0),
        response.getBody().getTransportInformation().get(0));
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getRoomType(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getRoomType());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getRoomName(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getRoomName());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getRoomDescription(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getRoomDescription());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getFacilities().get(0)
            .getName(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getFacilities().get(0)
            .getName());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomConfiguration().getTabItems().get(0).getImages().get(0)
            .getImageSrc(),
        response.getBody().getRoomConfiguration().getTabItems().get(0).getImages().get(0)
            .getImageSrc());
    assertEquals(response.toString(),
        hotelInformationDto.getFacts().getFactItems().get(0).getTitle(),
        response.getBody().getFacts().getFactItems().get(0).getTitle());
    assertEquals(response.toString(),
        hotelInformationDto.getFacts().getFactItems().get(0).getDescription(),
        response.getBody().getFacts().getFactItems().get(0).getDescription());
    assertEquals(response.toString(),
        hotelInformationDto.getBreadcrumb().get(0).getTitle(),
        response.getBody().getBreadcrumb().get(0).getTitle());
    assertEquals(response.toString(),
        hotelInformationDto.getBreadcrumb().get(0).getLink(),
        response.getBody().getBreadcrumb().get(0).getLink());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomClassConfiguration().get(0).getCode(),
        response.getBody().getRoomClassConfiguration().get(0).getCode());
    assertEquals(response.toString(),
        hotelInformationDto.getRoomClassConfiguration().get(0).getTitle(),
        response.getBody().getRoomClassConfiguration().get(0).getTitle());
  }

  private HotelInformationExtended getHotelInformationExtended() {
    HotelInformationExtended hotelInformation = new HotelInformationExtended();
    hotelInformation.setGalleryImages(getGalleryImage());
    hotelInformation.setHotelFacilities(getHotelFacility());
    hotelInformation.setTransportInformation(getTransportInformation());
    hotelInformation.setRoomConfiguration(getRoomConfiguration());
    hotelInformation.setBrand("PI");
    hotelInformation.setName("Edinburgh Park (Airport)");
    hotelInformation.setHeadline(
        "Just a 10-minute drive from Edinburgh Airport with free on-site parking. Close to trams directly to Edinburgh Airport");
    hotelInformation.setImportantInfo(getImportantInfo());
    hotelInformation.setAccessibilityInfo(getAccessibilityInfo());
    hotelInformation.setCounty("test-county");
    hotelInformation.setRoomClassConfiguration(createMockRoomClassConfigResponse());
    hotelInformation.setHotelFlags(mockDomainHotelFlags());
    return hotelInformation;
  }

  private HotelInformation getHotelInformation() {
    HotelInformation hotelInformation = new HotelInformation();
    hotelInformation.setFacts(getFactItem());
    hotelInformation.setBreadcrumb(getBreadcrumb());
    hotelInformation.setGalleryImages(getGalleryImage());
    hotelInformation.setHotelFacilities(getHotelFacility());
    hotelInformation.setTransportInformation(getTransportInformation());
    hotelInformation.setRoomConfiguration(getRoomConfiguration());
    hotelInformation.setBrand("PI");
    hotelInformation.setName("Edinburgh Park (Airport)");
    hotelInformation.setCountryCodeISO("GB");
    hotelInformation.setHeadline(
        "Just a 10-minute drive from Edinburgh Airport with free on-site parking. Close to trams directly to Edinburgh Airport");
    hotelInformation.setImportantInfo(getImportantInfo());
    hotelInformation.setAccessibilityInfo(getAccessibilityInfo());
    hotelInformation.setRoomClassConfiguration(createMockRoomClassConfigResponse());
    return hotelInformation;
  }

  private List<uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration> createMockRoomClassConfigResponse() {
    var roomClassConfig1 = new uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration();
    roomClassConfig1.setTitle("Standard room with city view");
    roomClassConfig1.setCode("SV");

    var roomClassConfig2 = new uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration();
    roomClassConfig2.setTitle("Premier plus room with city view");
    roomClassConfig2.setCode("PV");

    var configList = new ArrayList<uk.co.whitbread.content.domain.model.hotel.out.RoomClassConfiguration>();
    configList.add(roomClassConfig1);
    configList.add(roomClassConfig2);

    return configList;
  }

  private List<HotelInformation> getHotelsInformation() {
    HotelInformation hotelInformation1 = new HotelInformation();
    hotelInformation1.setName("TestName");
    hotelInformation1.setHotelId(HOTEL_ID_EDIPAR);
    hotelInformation1.setBrand("PI");

    hotelInformation1.setCountryCodeISO("GB");
    hotelInformation1.setRestaurant(Restaurant.builder()
        .logoSrc("logo-url/test.jpg")
        .name("THYME")
        .build());
    hotelInformation1.setRoomConfiguration(getRoomConfiguration());
    hotelInformation1.setMessagingFlag(mockDomainMessagingFlag());
    hotelInformation1.setHotelFlags(mockDomainHotelFlags());
    hotelInformation1.setThumbnailImages(List.of(mockThumbnailImage()));
    hotelInformation1.setHotelOpeningDate("20-02-2022");
    hotelInformation1.setImportantInfo(getImportantInfo());
    hotelInformation1.setAccessibilityInfo(getAccessibilityInfo());
    hotelInformation1.setDistanceFromReference(34.2);

    HotelInformation hotelInformation2 = new HotelInformation();
    hotelInformation2.setName("TestName");
    hotelInformation2.setHotelId(HOTEL_ID_LONEUS);
    hotelInformation2.setBrand("PI");
    hotelInformation2.setCountryCodeISO("GB");
    hotelInformation2.setRestaurant(Restaurant.builder()
        .logoSrc("logo-url/test.jpg")
        .name("THYME")
        .build());
    hotelInformation2.setRoomConfiguration(getRoomConfiguration());
    hotelInformation2.setMessagingFlag(mockDomainMessagingFlag());
    hotelInformation2.setHotelFlags(mockDomainHotelFlags());
    hotelInformation2.setThumbnailImages(List.of(mockThumbnailImage()));
    hotelInformation2.setHotelOpeningDate("20-02-2022");
    hotelInformation2.setImportantInfo(getImportantInfo());
    hotelInformation2.setAccessibilityInfo(getAccessibilityInfo());
    hotelInformation2.setDistanceFromReference(34.2);

    return List.of(hotelInformation1, hotelInformation2);
  }

  private ThumbnailImage mockThumbnailImage() {
    return ThumbnailImage.builder()
        .imageSrc("/content/dam/pi/websites/hotelimages/gb/en/L/LONEUS/LONEUS 1.jpg")
        .tags(List.of("surrounding-area"))
        .build();
  }

  private MessagingFlag mockDomainMessagingFlag() {
    return MessagingFlag.builder()
        .text("surrounding-area")
        .description("surrounding-area")
        .color("blue")
        .build();
  }

  private List<HotelInformationDto> getHotelsInformationDtoList() {
    HotelInformationDto hotelInformation1 = new HotelInformationDto();
    hotelInformation1.setName("TestName");
    hotelInformation1.setHotelId(HOTEL_ID_EDIPAR);
    hotelInformation1.setBrand("PI");
    hotelInformation1.setRestaurant(RestaurantDto.builder()
        .logoSrc("logo-url/test.jpg")
        .name("THYME")
        .build());
    hotelInformation1.setRoomConfiguration(getRoomConfigurationDto());
    hotelInformation1.setMessagingFlag(mockMessagingFlag());
    hotelInformation1.setHotelFlags(mockHotelFlagsDto());
    hotelInformation1.setThumbnailImages(List.of(mockThumbnailImageDto()));
    hotelInformation1.setHotelOpeningDate("20-02-2022");
    hotelInformation1.setImportantInfo(getImportantInfoDto());
    hotelInformation1.setAccessibilityInfo(getAccessibilityInfoDto());
    hotelInformation1.setDistanceFromReference(34.2);

    HotelInformationDto hotelInformation2 = new HotelInformationDto();
    hotelInformation2.setName("TestName");
    hotelInformation2.setHotelId(HOTEL_ID_LONEUS);
    hotelInformation2.setBrand("PI");
    hotelInformation2.setRestaurant(RestaurantDto.builder()
        .logoSrc("logo-url/test.jpg")
        .name("THYME")
        .build());
    hotelInformation2.setRoomConfiguration(getRoomConfigurationDto());
    hotelInformation2.setMessagingFlag(mockMessagingFlag());
    hotelInformation2.setHotelFlags(mockHotelFlagsDto());
    hotelInformation2.setThumbnailImages(List.of(mockThumbnailImageDto()));
    hotelInformation2.setHotelOpeningDate("20-02-2022");
    hotelInformation2.setImportantInfo(getImportantInfoDto());
    hotelInformation2.setAccessibilityInfo(getAccessibilityInfoDto());
    hotelInformation1.setDistanceFromReference(34.2);

    return List.of(hotelInformation1, hotelInformation2);
  }

  private ThumbnailImageDto mockThumbnailImageDto() {
    return ThumbnailImageDto.builder()
        .imageSrc("/content/dam/pi/websites/hotelimages/gb/en/L/LONEUS/LONEUS 1.jpg")
        .tags(List.of("surrounding-area"))
        .build();
  }

  private MessagingFlagDto mockMessagingFlag() {
    return MessagingFlagDto.builder()
        .text("surrounding-area")
        .description("surrounding-area")
        .color("blue")
        .build();
  }

  private uk.co.whitbread.content.domain.model.hotel.out.HotelFlags mockDomainHotelFlags() {
    return uk.co.whitbread.content.domain.model.hotel.out.HotelFlags.builder()
        .isEnabled(true)
        .flagOverlay(uk.co.whitbread.content.domain.model.hotel.out.FlagDetail.builder()
            .text("New Premier Plus rooms")
            .textColour("#D7C3FF")
            .backgroundColour("#0007")
            .backgroundImage("/content/dam/icons/resources/icon-overlay.png")
            .build())
        .flagBanner(uk.co.whitbread.content.domain.model.hotel.out.FlagDetail.builder()
            .text("New restaurant now open")
            .textColour("#FFFFFF")
            .backgroundColour("#511E62")
            .backgroundImage("/content/dam/icons/resources/icon-banner.png")
            .build())
        .build();
  }

  private HotelFlagsDto mockHotelFlagsDto() {
    return HotelFlagsDto.builder()
        .isEnabled(true)
        .flagOverlay(FlagDetailDto.builder()
            .text("New Premier Plus rooms")
            .textColour("#D7C3FF")
            .backgroundColour("#0007")
            .backgroundImage("/content/dam/icons/resources/icon-overlay.png")
            .build())
        .flagBanner(FlagDetailDto.builder()
            .text("New restaurant now open")
            .textColour("#FFFFFF")
            .backgroundColour("#511E62")
            .backgroundImage("/content/dam/icons/resources/icon-banner.png")
            .build())
        .build();
  }

  private List<GalleryImage> getGalleryImage() {
    List<GalleryImage> galleryImages = new ArrayList<>();
    GalleryImage galleryImage = new GalleryImage();
    galleryImage.setImageSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/E/EDIPAR/EdinburghPark-01.jpg");
    galleryImage.setThumbnailSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/E/EDIPAR/EdinburghPark-01.jpg");
    galleryImage.setAlt("");
    galleryImage.setCaption("");
    galleryImage.setIconSrc("");
    galleryImages.add(galleryImage);
    return galleryImages;
  }

  private Facts getFactItem(){
    Facts facts= new Facts();
    List<FactItem> factItems= new ArrayList<>();
    FactItem factItem=new FactItem();
    factItem.setTitle("Title");
    factItem.setDescription("Description");
    factItems.add(factItem);
    facts.setFactItems(factItems);
    return facts;
  }

  private List<Breadcrumb> getBreadcrumb(){
    List<Breadcrumb> breadcrumbs= new ArrayList<>();
    Breadcrumb breadcrumb= new Breadcrumb();
    breadcrumb.setTitle("Title");
    breadcrumb.setLink("Link");
    breadcrumbs.add(breadcrumb);
    return breadcrumbs;
  }

  private List<HotelFacility> getHotelFacility() {
    List<HotelFacility> hotelFacilities = new ArrayList<>();
    HotelFacility hotelFacility = new HotelFacility();
    hotelFacility.setCode("PBI");
    hotelFacility.setName("Public Transport Info");
    hotelFacility.setDescription("");
    hotelFacility.setWeight(0);
    hotelFacility.setIcon("/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PBI.svg");
    hotelFacility.setIsVisible(true);
    hotelFacilities.add(hotelFacility);
    return hotelFacilities;
  }

  private List<String> getTransportInformation() {
    List<String> transportInformation = new ArrayList<>();
    String s1 = "Edinburgh Airport 3 Miles";
    String s2 = "Edinburgh City Centre 6 Miles";
    String s3 = "Highland Centre 1 Mile";
    String s4 = "Murrayfield Stadium 4.5 Miles";
    transportInformation.add(s1);
    transportInformation.add(s2);
    transportInformation.add(s3);
    transportInformation.add(s4);
    return transportInformation;
  }

  private RoomConfiguration getRoomConfiguration() {
    RoomConfiguration roomConfiguration = new RoomConfiguration();
    TabItem tabItem = new TabItem();
    tabItem.setRoomType("double");
    tabItem.setRoomName("Standard double");
    tabItem.setRoomDescription(
        "A super-comfy Hypnos bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.");

    HotelFacility hotelFacility = new HotelFacility();
    hotelFacility.setName("Double or kingsize Hypnos bed");
    hotelFacility.setDescription("");
    hotelFacility.setWeight(1);
    hotelFacility.setIcon(
        "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/102.svg");
    hotelFacility.setIsVisible(true);
    List<HotelFacility> hotelFacilities = new ArrayList<>();
    hotelFacilities.add(hotelFacility);

    GalleryImage galleryImage = new GalleryImage();
    galleryImage.setImageSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg");
    galleryImage.setThumbnailSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg");
    galleryImage.setAlt("");
    galleryImage.setCaption("");
    galleryImage.setIconSrc("");
    List<GalleryImage> galleryImages = new ArrayList<>();
    galleryImages.add(galleryImage);

    tabItem.setFacilities(hotelFacilities);
    tabItem.setImages(galleryImages);
    List<TabItem> tabItems = new ArrayList<>();
    tabItems.add(tabItem);
    roomConfiguration.setTabItems(tabItems);
    return roomConfiguration;
  }

  private HotelInformationExtendedDto getBookingInformationExtendedDto() {
    HotelInformationExtendedDto hotelInformationDto = new HotelInformationExtendedDto();
    hotelInformationDto.setGalleryImages(getGalleryImageDto());
    hotelInformationDto.setHotelFacilities(getHotelFacilityDto());
    hotelInformationDto.setTransportInformation(getTransportInformationDto());
    hotelInformationDto.setRoomConfiguration(getRoomConfigurationDto());
    hotelInformationDto.setBrand("PI");
    hotelInformationDto.setName("Edinburgh Park (Airport)");
    hotelInformationDto.setHeadline(
        "Just a 10-minute drive from Edinburgh Airport with free on-site parking. Close to trams directly to Edinburgh Airport");
    hotelInformationDto.setImportantInfo(getImportantInfoDto());
    hotelInformationDto.setAccessibilityInfo(getAccessibilityInfoDto());
    hotelInformationDto.setCounty("test-county");
    hotelInformationDto.setRoomClassConfiguration(getRoomClassConfigDto());
    hotelInformationDto.setHotelFlags(mockHotelFlagsDto());
    return hotelInformationDto;
  }

  private HotelInformationDto getBookingInformationDto() {
    HotelInformationDto hotelInformationDto = new HotelInformationDto();
    hotelInformationDto.setFacts((Facts) getFactItem());
    hotelInformationDto.setBreadcrumb(getBreadcrumb());
    hotelInformationDto.setGalleryImages(getGalleryImageDto());
    hotelInformationDto.setHotelFacilities(getHotelFacilityDto());
    hotelInformationDto.setTransportInformation(getTransportInformationDto());
    hotelInformationDto.setRoomConfiguration(getRoomConfigurationDto());
    hotelInformationDto.setBrand("PI");
    hotelInformationDto.setName("Edinburgh Park (Airport)");
    hotelInformationDto.setHeadline(
        "Just a 10-minute drive from Edinburgh Airport with free on-site parking. Close to trams directly to Edinburgh Airport");
    hotelInformationDto.setImportantInfo(getImportantInfoDto());
    hotelInformationDto.setAccessibilityInfo(getAccessibilityInfoDto());
    hotelInformationDto.setRoomClassConfiguration(getRoomClassConfigDto());
    return hotelInformationDto;
  }

  private List<RoomClassConfigurationDto> getRoomClassConfigDto() {
    var roomClassConfig1 = new RoomClassConfigurationDto();
    roomClassConfig1.setTitle("Standard room with city view");
    roomClassConfig1.setCode("SV");

    var roomClassConfig2 = new RoomClassConfigurationDto();
    roomClassConfig2.setTitle("Premier plus room with city view");
    roomClassConfig2.setCode("PV");

    var configList = new ArrayList<RoomClassConfigurationDto>();
    configList.add(roomClassConfig1);
    configList.add(roomClassConfig2);

    return configList;
  }

  private List<GalleryImageDto> getGalleryImageDto() {
    List<GalleryImageDto> galleryImagesDto = new ArrayList<>();
    GalleryImageDto galleryImageDto = new GalleryImageDto();
    galleryImageDto.setImageSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/E/EDIPAR/EdinburghPark-01.jpg");
    galleryImageDto.setThumbnailSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/E/EDIPAR/EdinburghPark-01.jpg");
    galleryImageDto.setAlt("");
    galleryImageDto.setCaption("");
    galleryImageDto.setIconSrc("");
    galleryImagesDto.add(galleryImageDto);
    return galleryImagesDto;
  }

  private List<HotelFacilityDto> getHotelFacilityDto() {
    List<HotelFacilityDto> hotelFacilitiesDto = new ArrayList<>();
    HotelFacilityDto hotelFacilityDto = new HotelFacilityDto();
    hotelFacilityDto.setCode("PBI");
    hotelFacilityDto.setName("Public Transport Info");
    hotelFacilityDto.setDescription("");
    hotelFacilityDto.setWeight(0);
    hotelFacilityDto.setIcon(
        "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PBI.svg");
    hotelFacilityDto.setIsVisible(true);
    hotelFacilitiesDto.add(hotelFacilityDto);
    return hotelFacilitiesDto;
  }

  private List<String> getTransportInformationDto() {
    List<String> transportInformationDto = new ArrayList<>();
    String s1 = "Edinburgh Airport 3 Miles";
    String s2 = "Edinburgh City Centre 6 Miles";
    String s3 = "Highland Centre 1 Mile";
    String s4 = "Murrayfield Stadium 4.5 Miles";

    transportInformationDto.add(s1);
    transportInformationDto.add(s2);
    transportInformationDto.add(s3);
    transportInformationDto.add(s4);
    return transportInformationDto;
  }

  private RoomConfigurationDto getRoomConfigurationDto() {
    RoomConfigurationDto roomConfigurationDto = new RoomConfigurationDto();
    TabItemDto tabItemDto = new TabItemDto();
    tabItemDto.setRoomType("double");
    tabItemDto.setRoomName("Standard double");
    tabItemDto.setRoomDescription(
        "A super-comfy Hypnos bed, a power shower and free Wi-Fi, our double rooms have everything you'll need for a great night's sleep.");

    HotelFacilityDto hotelFacilityDto = new HotelFacilityDto();
    hotelFacilityDto.setName("Double or kingsize Hypnos bed");
    hotelFacilityDto.setDescription("");
    hotelFacilityDto.setWeight(1);
    hotelFacilityDto.setIcon(
        "/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/102.svg");
    hotelFacilityDto.setIsVisible(true);
    List<HotelFacilityDto> hotelFacilitiesDto = new ArrayList<>();
    hotelFacilitiesDto.add(hotelFacilityDto);

    GalleryImageDto galleryImageDto = new GalleryImageDto();
    galleryImageDto.setImageSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg");
    galleryImageDto.setThumbnailSrc(
        "/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room1.jpg");
    galleryImageDto.setAlt("");
    galleryImageDto.setCaption("");
    galleryImageDto.setIconSrc("");
    List<GalleryImageDto> galleryImagesDto = new ArrayList<>();
    galleryImagesDto.add(galleryImageDto);

    tabItemDto.setFacilities(hotelFacilitiesDto);
    tabItemDto.setImages(galleryImagesDto);
    List<TabItemDto> tabItemsDto = new ArrayList<>();
    tabItemsDto.add(tabItemDto);
    roomConfigurationDto.setTabItems(tabItemsDto);
    return roomConfigurationDto;
  }

  private HotelPaymentInformation getHotelPaymentInformation() {
    HotelPaymentInformation hotelPaymentInformation = new HotelPaymentInformation();
    hotelPaymentInformation.setAddress(getAddress());
    hotelPaymentInformation.setAcceptedCreditCards(getAcceptedCreditCard());
    hotelPaymentInformation.setPaymentProviders(getPaymentProvider());
    return hotelPaymentInformation;
  }

  private Address getAddress() {
    Address address = new Address();
    address.setAddressLine1("1 Lochside Court");
    address.setAddressLine2("Edinburgh Park");
    address.setAddressLine3("Edinburgh");
    address.setCountry("United Kingdom (the)");
    address.setPostalCode("EH12 9FX");
    return address;
  }

  private List<AcceptedCreditCard> getAcceptedCreditCard() {
    AcceptedCreditCard acceptedCreditCard = new AcceptedCreditCard();
    acceptedCreditCard.setCode("AC");
    acceptedCreditCard.setCode3cp("MC");
    acceptedCreditCard.setListOrder("MC");
    acceptedCreditCard.setFeeAmount("");
    acceptedCreditCard.setFeeCurrency("");
    acceptedCreditCard.setListOrder("3");
    acceptedCreditCard.setName("Mastercard Credit");
    acceptedCreditCard.setPaymentOnly(false);
    acceptedCreditCard.setSchemeLogo("/content/dam/global/booking/Mastercard.jpg");
    List<AcceptedCreditCard> acceptedCreditCards = new ArrayList<>();
    acceptedCreditCards.add(acceptedCreditCard);
    return acceptedCreditCards;
  }

  private List<PaymentProvider> getPaymentProvider() {
    PaymentProvider paymentProvider = new PaymentProvider();
    PaymentMethod paymentMethod = new PaymentMethod();
    List<PaymentMethod> paymentMethods = new ArrayList<>();
    List<PaymentProvider> paymentProviders = new ArrayList<>();
    paymentProvider.setProviderId("3CP");
    paymentMethod.setCode("MC");
    paymentMethod.setFeeAmount("");
    paymentMethod.setFeeCurrency("");
    paymentMethod.setListOrder("3");
    paymentMethod.setName("Mastercard Credit");
    paymentMethod.setPaymentOnly(false);
    paymentMethod.setSchemeLogo("/content/dam/global/booking/Mastercard.jpg");
    paymentMethods.add(paymentMethod);
    paymentProvider.setPaymentMethods(paymentMethods);
    paymentProviders.add(paymentProvider);
    return paymentProviders;
  }

  private HotelPaymentInformationDto getHotelPaymentInformationDto() {
    HotelPaymentInformationDto hotelPaymentInformationDto = new HotelPaymentInformationDto();
    hotelPaymentInformationDto.setAddress(getAddressDto());
    hotelPaymentInformationDto.setAcceptedCreditCards(getPaymentMethodsDto());
    hotelPaymentInformationDto.setPaymentProviders(getPaymentProviderDto());
    return hotelPaymentInformationDto;
  }

  private AddressDto getAddressDto() {
    AddressDto addressDto = new AddressDto();
    addressDto.setAddressLine1("1 Lochside Court");
    addressDto.setAddressLine2("Edinburgh Park");
    addressDto.setAddressLine3("Edinburgh");
    addressDto.setCountry("United Kingdom (the)");
    addressDto.setPostalCode("EH12 9FX");
    return addressDto;
  }

  private List<AcceptedCreditCardDto> getPaymentMethodsDto() {
    AcceptedCreditCardDto acceptedCreditCardDto = new AcceptedCreditCardDto();
    acceptedCreditCardDto.setCode("AC");
    acceptedCreditCardDto.setCode3cp("MC");
    acceptedCreditCardDto.setCodeOpera("MC");
    acceptedCreditCardDto.setFeeAmount("");
    acceptedCreditCardDto.setFeeCurrency("");
    acceptedCreditCardDto.setListOrder("3");
    acceptedCreditCardDto.setName("Mastercard Credit");
    acceptedCreditCardDto.setPaymentOnly(false);
    acceptedCreditCardDto.setSchemeLogo("/content/dam/global/booking/Mastercard.jpg");
    List<AcceptedCreditCardDto> acceptedCreditCardsDto = new ArrayList<>();
    acceptedCreditCardsDto.add(acceptedCreditCardDto);
    return acceptedCreditCardsDto;
  }

  private List<PaymentProviderDto> getPaymentProviderDto() {
    PaymentProviderDto paymentProviderDto = new PaymentProviderDto();
    PaymentMethodDto paymentMethodDto = new PaymentMethodDto();
    List<PaymentMethodDto> paymentMethodsDto = new ArrayList<>();
    List<PaymentProviderDto> paymentProvidersDto = new ArrayList<>();
    paymentProviderDto.setProviderId("3CP");
    paymentMethodDto.setCode("MC");
    paymentMethodDto.setFeeAmount("");
    paymentMethodDto.setFeeCurrency("");
    paymentMethodDto.setListOrder("3");
    paymentMethodDto.setName("Mastercard Credit");
    paymentMethodDto.setPaymentOnly(false);
    paymentMethodDto.setSchemeLogo("/content/dam/global/booking/Mastercard.jpg");
    paymentMethodsDto.add(paymentMethodDto);
    paymentProviderDto.setPaymentMethods(paymentMethodsDto);
    paymentProvidersDto.add(paymentProviderDto);
    return paymentProvidersDto;
  }

  private ImportantInfo getImportantInfo() {
    ImportantInfo importantInfo = new ImportantInfo();
    importantInfo.setTitle("Important Information");

    InfoItem infoItem = new InfoItem();
    infoItem.setText("There is no air conditioning at this hotel.");
    infoItem.setHtmlText("There is no air conditioning at this hotel. <a href=\"/gb/en/faq/our-rooms.html\">Learn more about our rooms</a>");
    infoItem.setPriority("1");
    infoItem.setStartDate("17/09/2022");
    infoItem.setEndDate("20/10/2022");
    infoItem.setHideOnHdp(true);
    infoItem.setHideOnBookingFlow(false);

    List<InfoItem> infoItems = List.of(infoItem);
    importantInfo.setInfoItems(infoItems);

    return importantInfo;

  }

  private ImportantInfoDto getImportantInfoDto() {
    ImportantInfoDto importantInfo = new ImportantInfoDto();
    importantInfo.setTitle("Important Information");

    InfoItemDto infoItem = new InfoItemDto();
    infoItem.setText("There is no air conditioning at this hotel.");
    infoItem.setHtmlText("There is no air conditioning at this hotel. <a href=\"/gb/en/faq/our-rooms.html\">Learn more about our rooms</a>");
    infoItem.setPriority("1");
    infoItem.setStartDate("17/09/2022");
    infoItem.setEndDate("20/10/2022");
    infoItem.setHideOnHdp(true);
    infoItem.setHideOnBookingFlow(false);

    List<InfoItemDto> infoItems = List.of(infoItem);
    importantInfo.setInfoItems(infoItems);

    return importantInfo;

  }

  private AccessibilityInfo getAccessibilityInfo() {
    return AccessibilityInfo.builder()
        .header("headerTest")
        .text("All accessible rooms at this hotel are double rooms.")
        .linkText("linkTextTest")
        .phoneNumber("3527934510")
        .build();
  }

  private AccessibilityInfoDto getAccessibilityInfoDto() {
    return AccessibilityInfoDto.builder()
        .header("headerTest")
        .text("All accessible rooms at this hotel are double rooms.")
        .linkText("linkTextTest")
        .phoneNumber("3527934510")
        .build();
  }

  private static HotelInformationRequestDto getHotelInformationRequestDto() {
    return HotelInformationRequestDto.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
  }

  private static HotelInformationRequest getHotelInformationRequest() {
    return HotelInformationRequest.builder()
        .hotelId(HOTEL_ID_EDIPAR)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
  }

  // -------------------------------------------------------------------------
  // Stay date filtering tests
  // -------------------------------------------------------------------------

  @Test
  void getHotelInformation_withStayDates_noRestaurant_returnsUnchangedDto() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();
    var mappedDto = getBookingInformationExtendedDto(); // restaurant is null by default
    mappedDto.setRestaurant(null);
    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(getHotelInformationExtended());
    when(hotelInformationDtoMapper.toDto(getHotelInformationExtended())).thenReturn(mappedDto);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());

      // Act
      var response = hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, LocalDate.of(2025, Month.JULY, 1), LocalDate.of(2025, Month.JULY, 3));

      // Assert – no restaurant, so dto is returned as-is
      Assertions.assertEquals(200, response.getStatusCode().value());
      assertSame(mappedDto, response.getBody());
    }
  }

  @Test
  void getHotelInformation_withStayDates_scheduledMenuOverlaps_returnsScheduledMenu() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();

    var scheduledMenu = MenuDto.builder()
        .name("Breakfast")
        .menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1))
        .stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Breakfast")
        .menuSrc("/menus/standard-breakfast")
        .build();

    var restaurant = RestaurantDto.builder()
        .name("The Hub")
        .menus(List.of(scheduledMenu, fallbackMenu))
        .build();
    var mappedDto = getBookingInformationExtendedDto();
    mappedDto.setRestaurant(restaurant);

    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(getHotelInformationExtended());
    when(hotelInformationDtoMapper.toDto(getHotelInformationExtended())).thenReturn(mappedDto);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());

      // Act – stay from July 10 to July 12 overlaps the scheduled menu (Jul 1–31)
      var response = hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

      // Assert – only the scheduled menu should be returned
      Assertions.assertEquals(200, response.getStatusCode().value());
      var menus = response.getBody().getRestaurant().getMenus();
      Assertions.assertEquals(1, menus.size());
      Assertions.assertEquals("/menus/summer-breakfast", menus.get(0).getMenuSrc());
    }
  }

  @Test
  void getHotelInformation_withStayDates_scheduledMenuDoesNotOverlap_returnsFallbackMenu() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();

    var scheduledMenu = MenuDto.builder()
        .name("Dinner")
        .menuSrc("/menus/christmas-dinner")
        .stayStartDate(LocalDate.of(2025, Month.DECEMBER, 20))
        .stayEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Dinner")
        .menuSrc("/menus/standard-dinner")
        .build();

    var restaurant = RestaurantDto.builder()
        .name("The Hub")
        .menus(List.of(scheduledMenu, fallbackMenu))
        .build();
    var mappedDto = getBookingInformationExtendedDto();
    mappedDto.setRestaurant(restaurant);

    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(getHotelInformationExtended());
    when(hotelInformationDtoMapper.toDto(getHotelInformationExtended())).thenReturn(mappedDto);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());

      // Act – stay in July, scheduled menu is in December → no overlap → fallback
      var response = hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

      // Assert – only the fallback menu should be returned
      Assertions.assertEquals(200, response.getStatusCode().value());
      var menus = response.getBody().getRestaurant().getMenus();
      Assertions.assertEquals(1, menus.size());
      Assertions.assertEquals("/menus/standard-dinner", menus.get(0).getMenuSrc());
    }
  }

  @Test
  void getHotelInformation_withStayDates_noOverlapAndNoFallback_tabHidden() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();

    var scheduledMenu = MenuDto.builder()
        .name("Lunch")
        .menuSrc("/menus/christmas-lunch")
        .stayStartDate(LocalDate.of(2025, Month.DECEMBER, 20))
        .stayEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
        .build();

    var restaurant = RestaurantDto.builder()
        .name("The Hub")
        .menus(List.of(scheduledMenu))
        .build();
    var mappedDto = getBookingInformationExtendedDto();
    mappedDto.setRestaurant(restaurant);

    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(getHotelInformationExtended());
    when(hotelInformationDtoMapper.toDto(getHotelInformationExtended())).thenReturn(mappedDto);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());

      // Act – stay in July, scheduled menu is in December → no overlap, no fallback → hidden
      var response = hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

      // Assert – menu tab should be hidden (empty list)
      Assertions.assertEquals(200, response.getStatusCode().value());
      var menus = response.getBody().getRestaurant().getMenus();
      Assertions.assertTrue(menus.isEmpty());
    }
  }

  @Test
  void getHotelInformation_withStayDates_doesNotMutateCachedDto() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();

    var scheduledMenu = MenuDto.builder()
        .name("Breakfast")
        .menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1))
        .stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Breakfast")
        .menuSrc("/menus/standard-breakfast")
        .build();

    var restaurant = RestaurantDto.builder()
        .name("The Hub")
        .menus(new ArrayList<>(List.of(scheduledMenu, fallbackMenu)))
        .build();
    var cachedDto = getBookingInformationExtendedDto();
    cachedDto.setRestaurant(restaurant);

    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.of(cachedDto));

      // Act
      hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

      // Assert – cached dto should not have been mutated
      Assertions.assertEquals(2, cachedDto.getRestaurant().getMenus().size(),
          "The cached DTO's menus list should remain unmodified after filtering");
    }
  }

  @Test
  void getHotelInformation_withStayDates_multipleMenuTabs_eachTabFilteredIndependently() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();

    // Tab "Breakfast": scheduled overlaps stay → scheduled returned
    var breakfastScheduled = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1)).stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var breakfastFallback = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/standard-breakfast")
        .build();

    // Tab "Dinner": scheduled does NOT overlap stay → fallback returned
    var dinnerScheduled = MenuDto.builder()
        .name("Dinner").menuSrc("/menus/christmas-dinner")
        .stayStartDate(LocalDate.of(2025, Month.DECEMBER, 20)).stayEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
        .build();
    var dinnerFallback = MenuDto.builder()
        .name("Dinner").menuSrc("/menus/standard-dinner")
        .build();

    var restaurant = RestaurantDto.builder()
        .name("The Hub")
        .menus(List.of(breakfastScheduled, breakfastFallback, dinnerScheduled, dinnerFallback))
        .build();
    var mappedDto = getBookingInformationExtendedDto();
    mappedDto.setRestaurant(restaurant);

    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(getHotelInformationExtended());
    when(hotelInformationDtoMapper.toDto(getHotelInformationExtended())).thenReturn(mappedDto);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());

      // Act
      var response = hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

      // Assert
      Assertions.assertEquals(200, response.getStatusCode().value());
      var menus = response.getBody().getRestaurant().getMenus();
      Assertions.assertEquals(2, menus.size());
      Assertions.assertEquals("/menus/summer-breakfast", menus.get(0).getMenuSrc());
      Assertions.assertEquals("/menus/standard-dinner", menus.get(1).getMenuSrc());
    }
  }

  @Test
  void getHotelInformation_withNullStayDates_returnsOnlyUnscheduledMenus() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var domainRequest = getHotelInformationRequest();

    var scheduledMenu = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1)).stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/standard-breakfast")
        .build();

    var restaurant = RestaurantDto.builder().name("The Hub")
        .menus(List.of(scheduledMenu, fallbackMenu)).build();
    var mappedDto = getBookingInformationExtendedDto();
    mappedDto.setRestaurant(restaurant);

    when(hotelInformationRequestDtoMapper.toDomainModel(HOTEL_ID_EDIPAR, requestDto)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformation(domainRequest)).thenReturn(getHotelInformationExtended());
    when(hotelInformationDtoMapper.toDto(getHotelInformationExtended())).thenReturn(mappedDto);

    try (MockedStatic<HotelInformationCacheUtils> cacheUtilsMock = mockStatic(HotelInformationCacheUtils.class)) {
      cacheUtilsMock.when(() -> HotelInformationCacheUtils.getValueFromCache(cacheManager1Hour, domainRequest, HOTEL_ID_EDIPAR))
          .thenReturn(Optional.empty());

      // Act – no stay dates provided
      var response = hotelInformationControllerUnderTest.getHotelInformation(
          HOTEL_ID_EDIPAR, requestDto, null, null);

      // Assert – only unscheduled menus returned when no stay dates provided
      Assertions.assertEquals(200, response.getStatusCode().value());
      var menus = response.getBody().getRestaurant().getMenus();
      Assertions.assertEquals(1, menus.size());
      Assertions.assertEquals("/menus/standard-breakfast", menus.get(0).getMenuSrc());
    }
  }

  // Stay date filtering tests for getHotelInformationBySlug
  // -------------------------------------------------------------------------

  @Test
  void getHotelInformationBySlug_withStayDates_noRestaurant_returnsUnchangedDto() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var slugRequestDto = SlugRequestDto.builder().slug(SLUG_LONDON_KINGS_CROSS).build();
    var domainRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).country(COUNTRY_GB).language(LANGUAGE_EN).build();
    var mappedDto = getBookingInformationDto();
    mappedDto.setRestaurant(null);

    when(hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, requestDto,
        LocalDate.of(2025, Month.JULY, 1), LocalDate.of(2025, Month.JULY, 3))).thenReturn(domainRequest);
    when(contentInPort.getHotelInformationBySlug(domainRequest)).thenReturn(getHotelInformation());
    when(hotelInformationDtoMapper.toDto(getHotelInformation())).thenReturn(mappedDto);

    // Act
    var response = hotelInformationControllerUnderTest.getHotelInformationBySlug(
        slugRequestDto, requestDto, LocalDate.of(2025, Month.JULY, 1), LocalDate.of(2025, Month.JULY, 3));

    // Assert – no restaurant, dto returned as-is
    Assertions.assertEquals(200, response.getStatusCode().value());
    assertSame(mappedDto, response.getBody());
  }

  @Test
  void getHotelInformationBySlug_withStayDates_scheduledMenuOverlaps_returnsScheduledMenu() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var slugRequestDto = SlugRequestDto.builder().slug(SLUG_LONDON_KINGS_CROSS).build();
    var domainRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).country(COUNTRY_GB).language(LANGUAGE_EN).build();

    var scheduledMenu = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1))
        .stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/standard-breakfast")
        .build();

    var mappedDto = getBookingInformationDto();
    mappedDto.setRestaurant(RestaurantDto.builder().name("The Hub")
        .menus(List.of(scheduledMenu, fallbackMenu)).build());

    when(hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, requestDto,
        LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12))).thenReturn(domainRequest);
    when(contentInPort.getHotelInformationBySlug(domainRequest)).thenReturn(getHotelInformation());
    when(hotelInformationDtoMapper.toDto(getHotelInformation())).thenReturn(mappedDto);

    // Act – stay from July 10–12 overlaps the scheduled menu (Jul 1–31)
    var response = hotelInformationControllerUnderTest.getHotelInformationBySlug(
        slugRequestDto, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

    // Assert – only the scheduled menu returned
    Assertions.assertEquals(200, response.getStatusCode().value());
    var menus = response.getBody().getRestaurant().getMenus();
    Assertions.assertEquals(1, menus.size());
    Assertions.assertEquals("/menus/summer-breakfast", menus.get(0).getMenuSrc());
  }

  @Test
  void getHotelInformationBySlug_withStayDates_scheduledMenuDoesNotOverlap_returnsFallbackMenu() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var slugRequestDto = SlugRequestDto.builder().slug(SLUG_LONDON_KINGS_CROSS).build();
    var domainRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).country(COUNTRY_GB).language(LANGUAGE_EN).build();

    var scheduledMenu = MenuDto.builder()
        .name("Dinner").menuSrc("/menus/christmas-dinner")
        .stayStartDate(LocalDate.of(2025, Month.DECEMBER, 20))
        .stayEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Dinner").menuSrc("/menus/standard-dinner")
        .build();

    var mappedDto = getBookingInformationDto();
    mappedDto.setRestaurant(RestaurantDto.builder().name("The Hub")
        .menus(List.of(scheduledMenu, fallbackMenu)).build());

    when(hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, requestDto,
        LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12))).thenReturn(domainRequest);
    when(contentInPort.getHotelInformationBySlug(domainRequest)).thenReturn(getHotelInformation());
    when(hotelInformationDtoMapper.toDto(getHotelInformation())).thenReturn(mappedDto);

    // Act – stay in July, scheduled menu is December → no overlap → fallback
    var response = hotelInformationControllerUnderTest.getHotelInformationBySlug(
        slugRequestDto, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

    // Assert – only the fallback menu returned
    Assertions.assertEquals(200, response.getStatusCode().value());
    var menus = response.getBody().getRestaurant().getMenus();
    Assertions.assertEquals(1, menus.size());
    Assertions.assertEquals("/menus/standard-dinner", menus.get(0).getMenuSrc());
  }

  @Test
  void getHotelInformationBySlug_withStayDates_noOverlapAndNoFallback_tabHidden() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var slugRequestDto = SlugRequestDto.builder().slug(SLUG_LONDON_KINGS_CROSS).build();
    var domainRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).country(COUNTRY_GB).language(LANGUAGE_EN).build();

    var scheduledMenu = MenuDto.builder()
        .name("Lunch").menuSrc("/menus/christmas-lunch")
        .stayStartDate(LocalDate.of(2025, Month.DECEMBER, 20))
        .stayEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
        .build();

    var mappedDto = getBookingInformationDto();
    mappedDto.setRestaurant(RestaurantDto.builder().name("The Hub")
        .menus(List.of(scheduledMenu)).build());

    when(hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, requestDto,
        LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12))).thenReturn(domainRequest);
    when(contentInPort.getHotelInformationBySlug(domainRequest)).thenReturn(getHotelInformation());
    when(hotelInformationDtoMapper.toDto(getHotelInformation())).thenReturn(mappedDto);

    // Act – stay in July, scheduled menu is December → no overlap, no fallback → hidden
    var response = hotelInformationControllerUnderTest.getHotelInformationBySlug(
        slugRequestDto, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

    // Assert – menu list is empty
    Assertions.assertEquals(200, response.getStatusCode().value());
    Assertions.assertTrue(response.getBody().getRestaurant().getMenus().isEmpty());
  }

  @Test
  void getHotelInformationBySlug_withStayDates_multipleMenuTabs_eachTabFilteredIndependently() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var slugRequestDto = SlugRequestDto.builder().slug(SLUG_LONDON_KINGS_CROSS).build();
    var domainRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).country(COUNTRY_GB).language(LANGUAGE_EN).build();

    // Tab "Breakfast": scheduled overlaps → scheduled returned
    var breakfastScheduled = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1)).stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var breakfastFallback = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/standard-breakfast")
        .build();

    // Tab "Dinner": scheduled does NOT overlap → fallback returned
    var dinnerScheduled = MenuDto.builder()
        .name("Dinner").menuSrc("/menus/christmas-dinner")
        .stayStartDate(LocalDate.of(2025, Month.DECEMBER, 20)).stayEndDate(LocalDate.of(2025, Month.DECEMBER, 31))
        .build();
    var dinnerFallback = MenuDto.builder()
        .name("Dinner").menuSrc("/menus/standard-dinner")
        .build();

    var mappedDto = getBookingInformationDto();
    mappedDto.setRestaurant(RestaurantDto.builder().name("The Hub")
        .menus(List.of(breakfastScheduled, breakfastFallback, dinnerScheduled, dinnerFallback)).build());

    when(hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, requestDto,
        LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12))).thenReturn(domainRequest);
    when(contentInPort.getHotelInformationBySlug(domainRequest)).thenReturn(getHotelInformation());
    when(hotelInformationDtoMapper.toDto(getHotelInformation())).thenReturn(mappedDto);

    // Act
    var response = hotelInformationControllerUnderTest.getHotelInformationBySlug(
        slugRequestDto, requestDto, LocalDate.of(2025, Month.JULY, 10), LocalDate.of(2025, Month.JULY, 12));

    // Assert
    Assertions.assertEquals(200, response.getStatusCode().value());
    var menus = response.getBody().getRestaurant().getMenus();
    Assertions.assertEquals(2, menus.size());
    Assertions.assertEquals("/menus/summer-breakfast", menus.get(0).getMenuSrc());
    Assertions.assertEquals("/menus/standard-dinner", menus.get(1).getMenuSrc());
  }

  @Test
  void getHotelInformationBySlug_withNullStayDates_returnsOnlyUnscheduledMenus() {
    // Arrange
    var requestDto = getHotelInformationRequestDto();
    var slugRequestDto = SlugRequestDto.builder().slug(SLUG_LONDON_KINGS_CROSS).build();
    var domainRequest = HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS).country(COUNTRY_GB).language(LANGUAGE_EN).build();

    var scheduledMenu = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/summer-breakfast")
        .stayStartDate(LocalDate.of(2025, Month.JULY, 1)).stayEndDate(LocalDate.of(2025, Month.JULY, 31))
        .build();
    var fallbackMenu = MenuDto.builder()
        .name("Breakfast").menuSrc("/menus/standard-breakfast")
        .build();

    var mappedDto = getBookingInformationDto();
    mappedDto.setRestaurant(RestaurantDto.builder().name("The Hub")
        .menus(List.of(scheduledMenu, fallbackMenu)).build());

    when(hotelInformationRequestDtoMapper.toDomainModel(slugRequestDto, requestDto, null, null)).thenReturn(domainRequest);
    when(contentInPort.getHotelInformationBySlug(domainRequest)).thenReturn(getHotelInformation());
    when(hotelInformationDtoMapper.toDto(getHotelInformation())).thenReturn(mappedDto);

    // Act – no stay dates provided
    var response = hotelInformationControllerUnderTest.getHotelInformationBySlug(
        slugRequestDto, requestDto, null, null);

    // Assert – only unscheduled menus returned
    Assertions.assertEquals(200, response.getStatusCode().value());
    var menus = response.getBody().getRestaurant().getMenus();
    Assertions.assertEquals(1, menus.size());
    Assertions.assertEquals("/menus/standard-breakfast", menus.get(0).getMenuSrc());
  }

}
