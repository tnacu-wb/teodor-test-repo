package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.availability.out.DailyPrice;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.availability.out.RoomRateInfoV2;
import uk.co.whitbread.domain.model.availability.out.RoomRateV2;
import uk.co.whitbread.domain.model.availability.out.RoomStay;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.availability.out.RoomTypeV2;
import uk.co.whitbread.domain.model.basket.out.Basket;
import uk.co.whitbread.domain.model.basket.out.BasketItem;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.hotel.content.generated.models.GlobalConfigDto;
import uk.co.whitbread.hotel.content.generated.models.HotelCityTaxDto;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.LightweightReservationByIdDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationLightweightResponseDto;

@ExtendWith(MockitoExtension.class)
class CityTaxUtilsTest {

  private static final String HOTEL_123 = "HOTEL_123";
  private static final String OTHER_HOTEL = "OTHER_HOTEL";
  private static final String START_DATE_STRING = "2025-10-05";
  private static final String START_DATE_STRING_V2 = "2026-07-24";
  private static final String END_DATE_STRING_V2 = "2026-07-28";
  private static final String CHANNEL_PI = "PI";
  private static final String CHANNEL_DISTR = "DISTR";
  private static final String TEST_BASKET = "TEST_BASKET";
  private static final String RESERVATION_123 = "RESERVATION_123";
  private static final String NTBUS = "NTBUS";
  private static final String LEISURE = "LEISURE";
  @Mock
  private ContentServiceOutPort contentServiceOutPort;
  @Mock
  private BasketServiceOutPort basketServiceOutPort;
  @Mock
  private HotelAvailabilityOutPort availabilityOhipPort;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  private HotelAvailability availability;
  private HotelAvailabilityResultV2 availabilityV2;
  private HotelAvailabilityResultV2 availabilityV2SixDays;

  @BeforeEach
  void setUp() {
    availability = createMockHotelAvailability();
    availabilityV2 = createMockHotelAvailabilityForV2();
  }

  @Test
  void shouldNotApplyCityTaxWhenFeatureFlagDisabled() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, CHANNEL_PI, START_DATE_STRING,
        contentServiceOutPort, unleashWrapper);

    // Assert
    verifyNoInteractions(contentServiceOutPort);
  }

  @Test
  void shouldSkipCityTaxProcessingWhenFeatureFlagDisabledForV2() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2, CHANNEL_DISTR,
        LocalDate.parse(START_DATE_STRING_V2), LocalDate.parse(END_DATE_STRING_V2),
        contentServiceOutPort, unleashWrapper);

    // Assert
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(840),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));
  }

  @Test
  void shouldNotApplyCityTaxForNonApplicableChannel() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, "NON_APPLICABLE_CHANNEL",
        START_DATE_STRING,
        contentServiceOutPort, unleashWrapper);

    // Assert
    verifyNoInteractions(contentServiceOutPort);
  }

  @Test
  void shouldSkipCityTaxProcessingForNonApplicableChannelForV2() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2, "NON_APPLICABLE_CHANNEL",
        LocalDate.parse(START_DATE_STRING_V2), LocalDate.parse(END_DATE_STRING_V2),
        contentServiceOutPort, unleashWrapper);

    // Assert
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(840),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));
  }

  @Test
  void shouldNotApplyCityTaxForNonEligibleHotel() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(OTHER_HOTEL));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, CHANNEL_PI, START_DATE_STRING,
        contentServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getGlobalConfig(null, null);
    verifyNoMoreInteractions(contentServiceOutPort);
  }

  @Test
  void shouldSkipCityTaxProcessingForNonEligibleHotelForV2() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(OTHER_HOTEL));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2, CHANNEL_DISTR,
        LocalDate.parse(START_DATE_STRING_V2), LocalDate.parse(END_DATE_STRING_V2),
        contentServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getGlobalConfig(null, null);
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
        .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(840),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));
  }

  @Test
  void shouldNotApplyCityTaxForIncompleteCityTaxConfig() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(null);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, CHANNEL_PI, START_DATE_STRING,
        contentServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getHotelInformation(null, null, HOTEL_123);
  }



  @Test
  void shouldSkipCityTaxProcessingForIncompleteCityTaxConfigForV2() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(null);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2, CHANNEL_DISTR,
        LocalDate.parse(START_DATE_STRING_V2), LocalDate.parse(END_DATE_STRING_V2),
        contentServiceOutPort, unleashWrapper);

    // Assert
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(840),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));
  }

  @Test
  void shouldNotApplyCityTaxForNonEligibleBookingDate() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(createMockHotelInformationExtendedDto("2025-10-15"));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, CHANNEL_PI, START_DATE_STRING,
        contentServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getHotelInformation(null, null, HOTEL_123);
  }

  @Test
  void shouldSkipCityTaxProcessingForNonEligibleBookingDateForV2() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(createMockHotelInformationExtendedDto("2025-09-15"));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2, CHANNEL_DISTR,
        LocalDate.parse(START_DATE_STRING_V2), LocalDate.parse(END_DATE_STRING_V2),
        contentServiceOutPort, unleashWrapper);

    // Assert
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(840),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));
  }

  @Test
  void shouldApplyCityTaxAndReplacePrices() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
   when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
       .thenReturn(createMockHotelInformationExtendedDto("2025-10-01"));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicable(availability, CHANNEL_PI, START_DATE_STRING,
        contentServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getHotelInformation(null, null, HOTEL_123);

  }

  @Test
  void shouldApplyCityTaxAndReplacePricesForV2() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(createMockHotelInformationExtendedDto("2025-10-01"));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2, CHANNEL_DISTR,
        LocalDate.parse(START_DATE_STRING_V2).minusYears(1), LocalDate.parse(END_DATE_STRING_V2).minusYears(1),
        contentServiceOutPort, unleashWrapper);

    // Assert
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));

  }

  @Test
  void shouldNotSkipCityTaxProcessingForNonEligibleBookingDateForV2SixDays() {
    // Arrange
    availabilityV2SixDays = createMockHotelAvailabilityForV2ForSixDays();
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(createMockHotelInformationExtendedDto("2025-09-01"));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxIfApplicableForV2(availabilityV2SixDays, CHANNEL_DISTR,
        LocalDate.parse(START_DATE_STRING_V2), LocalDate.parse(END_DATE_STRING_V2).plusDays(2),
        contentServiceOutPort, unleashWrapper);

    // Assert
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2SixDays.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(840),
        Matchers.comparesEqualTo(availabilityV2SixDays.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(0).getAmountAfterTax()));
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2SixDays.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(5).getAmountBeforeTax()));
    assertThat(BigDecimal.valueOf(700),
        Matchers.comparesEqualTo(availabilityV2SixDays.getRoomStays().get(0).getRoomTypes().get(0).getRoomRates()
            .get(0).getRoomRateInfo().getPriceInfo().get(5).getAmountAfterTax()));
  }

  @Test
  void updateHasCityTaxFlags_shouldNotUpdateWhenCityTaxConfigIsNull() {
    // Arrange
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(null);
    var packagesResponse = mockPackagesResponse();

    // Act
   CityTaxUtils.updateHasCityTaxFlags(HOTEL_123, START_DATE_STRING, packagesResponse, contentServiceOutPort);

    // Assert
    assertThat(packagesResponse.getHotelHasCityTaxForLeisure(), Matchers.is(Boolean.TRUE));
    assertThat(packagesResponse.getHotelHasCityTaxForBusiness(), Matchers.is(Boolean.TRUE));
    verify(contentServiceOutPort).getHotelInformation(null, null, HOTEL_123);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2025-10-01", ""})
  void updateHasCityTaxFlags_shouldNotUpdateWhenCityTaxApplies(String effectiveFromDate) {
    // Arrange
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(createMockHotelInformationExtendedDto(effectiveFromDate));
    var packagesResponse = mockPackagesResponse();

    // Act
    CityTaxUtils.updateHasCityTaxFlags(HOTEL_123, START_DATE_STRING, packagesResponse, contentServiceOutPort);

    // Assert
    assertThat(packagesResponse.getHotelHasCityTaxForLeisure(), Matchers.is(Boolean.TRUE));
    assertThat(packagesResponse.getHotelHasCityTaxForBusiness(), Matchers.is(Boolean.TRUE));
    verify(contentServiceOutPort).getHotelInformation(null, null, HOTEL_123);
  }

  @Test
  void updateHasCityTaxFlags_shouldUpdateWhenCityTaxDoesNotApply() {
    // Arrange
    when(contentServiceOutPort.getHotelInformation(null, null, HOTEL_123))
        .thenReturn(createMockHotelInformationExtendedDto("2026-10-01"));
    var packagesResponse = mockPackagesResponse();

    // Act
    CityTaxUtils.updateHasCityTaxFlags(HOTEL_123, START_DATE_STRING, packagesResponse, contentServiceOutPort);

    // Assert
    assertThat(packagesResponse.getHotelHasCityTaxForLeisure(), Matchers.is(Boolean.FALSE));
    assertThat(packagesResponse.getHotelHasCityTaxForBusiness(), Matchers.is(Boolean.FALSE));
    verify(contentServiceOutPort).getHotelInformation(null, null, HOTEL_123);
  }

  @Test
  void setPriceWithoutCityTaxForAmendIfApplicable_shouldExitWhenFeatureFlagDisabled() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    // Act
    CityTaxUtils.setPriceWithoutCityTaxForAmendIfApplicable(availability, TEST_BASKET,
        contentServiceOutPort, availabilityOhipPort, basketServiceOutPort, unleashWrapper);

    // Assert
    verifyNoInteractions(contentServiceOutPort, availabilityOhipPort, basketServiceOutPort);
  }

  @Test
  void setPriceWithoutCityTaxForAmendIfApplicable_shouldExitWhenHotelNotEligible() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(OTHER_HOTEL));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxForAmendIfApplicable(availability, TEST_BASKET,
        contentServiceOutPort, availabilityOhipPort, basketServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getGlobalConfig(null, null);
    verifyNoInteractions(availabilityOhipPort, basketServiceOutPort);
  }

  @Test
  void setPriceWithoutCityTaxForAmendIfApplicable_shouldExitWhenNoReservationIdFound() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(basketServiceOutPort.getBasket(TEST_BASKET)).thenReturn(new Basket());

    // Act
    CityTaxUtils.setPriceWithoutCityTaxForAmendIfApplicable(availability, TEST_BASKET,
        contentServiceOutPort, availabilityOhipPort, basketServiceOutPort, unleashWrapper);

    // Assert
    verify(basketServiceOutPort).getBasket(TEST_BASKET);
    verifyNoInteractions(availabilityOhipPort);
  }

  @Test
  void setPriceWithoutCityTaxForAmendIfApplicable_shouldExitWhenCityTaxApplicableForReasonOfStay() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(basketServiceOutPort.getBasket(TEST_BASKET))
        .thenReturn(createMockBasketWithStayItem(RESERVATION_123));
    when(availabilityOhipPort.getLightweightReservations(HOTEL_123, Set.of(RESERVATION_123)))
        .thenReturn(createMockLightweightReservationWithReasonOfStay(LEISURE));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxForAmendIfApplicable(availability, TEST_BASKET,
        contentServiceOutPort, availabilityOhipPort, basketServiceOutPort, unleashWrapper);

    // Assert
    verify(availabilityOhipPort).getLightweightReservations(HOTEL_123, Set.of(RESERVATION_123));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getTotalNetAmount(),
        Matchers.comparesEqualTo(new BigDecimal("700.00")));
  }

  @Test
  void setPriceWithoutCityTaxForAmendIfApplicable_shouldReplaceNetAmountWithEffectiveRate() {
    // Arrange
    when(unleashWrapper.featureFlag()).thenReturn(new FeatureFlag());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(contentServiceOutPort.getGlobalConfig(null, null))
        .thenReturn(createMockGlobalConfigDto(HOTEL_123));
    when(basketServiceOutPort.getBasket(TEST_BASKET))
        .thenReturn(createMockBasketWithStayItem(RESERVATION_123));
    when(availabilityOhipPort.getLightweightReservations(HOTEL_123, Set.of(RESERVATION_123)))
        .thenReturn(createMockLightweightReservationWithReasonOfStay(NTBUS));

    // Act
    CityTaxUtils.setPriceWithoutCityTaxForAmendIfApplicable(availability, TEST_BASKET,
        contentServiceOutPort, availabilityOhipPort, basketServiceOutPort, unleashWrapper);

    // Assert
    verify(contentServiceOutPort).getGlobalConfig(null, null);
    verify(basketServiceOutPort).getBasket(TEST_BASKET);
    verify(availabilityOhipPort).getLightweightReservations(HOTEL_123, Set.of(RESERVATION_123));
    assertThat(availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
        .getRoomPriceBreakdown().getTotalNetAmount(),
        Matchers.comparesEqualTo(new BigDecimal("665.00")));
  }

  private GlobalConfigDto createMockGlobalConfigDto(String hotelId) {
    GlobalConfigDto globalConfigDto = new GlobalConfigDto();
    globalConfigDto.setHotelsWithCityTax(List.of(hotelId));
    return globalConfigDto;
  }

  private HotelInformationExtendedDto createMockHotelInformationExtendedDto(String effectiveFrom) {
    var hotelCityTaxDto = new HotelCityTaxDto();
    hotelCityTaxDto.setBookingDateFrom("2025-10-01");
    hotelCityTaxDto.setEffectiveFrom(effectiveFrom);
    hotelCityTaxDto.isCityTaxHotel(Boolean.TRUE);

    var hotelInformationExtendedDto = new HotelInformationExtendedDto();
    hotelInformationExtendedDto.setCityTax(hotelCityTaxDto);

    return hotelInformationExtendedDto;
  }

  private HotelAvailability createMockHotelAvailability() {

    var dailyPrice = DailyPrice.builder()
        .netPrice(new BigDecimal("100.00"))
        .grossPrice(new BigDecimal("120.00"))
        .effectiveRate(new BigDecimal("95.00"))
        .build();

    var roomPriceBreakdown = RoomPriceBreakdown.builder()
        .totalNetAmount(new BigDecimal("700.00"))
        .totalGrossAmount(new BigDecimal("840.00"))
        .totalTaxAmount(new BigDecimal("140.00"))
        .effectiveRateAmount(new BigDecimal("665.00"))
        .dailyPrice(dailyPrice)
        .build();

    var room = Room.builder()
        .pmsRoomType("DELUXE")
        .roomClass("CLASS_A")
        .roomPriceBreakdown(roomPriceBreakdown)
        .numberOfRoomsAvailable(5)
        .build();

    var roomTypeInfo = RoomTypeInfo.builder()
        .roomType("DELUXE")
        .adults(2)
        .children(1)
        .room(room)
        .build();

    var roomRate = RoomRate.builder()
        .roomType(roomTypeInfo)
        .build();

    return HotelAvailability.builder()
        .hotelId("HOTEL_123")
        .startDate("2025-10-05")
        .endDate("2025-10-06")
        .available(true)
        .roomRate(roomRate)
        .build();
  }



  private HotelAvailabilityResultV2 createMockHotelAvailabilityForV2() {
    return HotelAvailabilityResultV2.builder()
        .hotelId("HOTEL_123")
        .roomStays(List.of(
            RoomStay.builder()
                .roomTypes(List.of(
                    RoomTypeV2.builder()
                        .roomType("DELUXE")
                        .roomRates(List.of(
                            RoomRateV2.builder()
                                .roomRateInfo(
                                    RoomRateInfoV2.builder()
                                        .priceInfo(List.of(
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.of(2025, 10, 5))
                                                .build()
                                            )
                                        )
                                        .build()
                                )
                                .build()
                        ))
                        .build()
                ))
                .build()
        ))
        .build();
  }

  private HotelAvailabilityResultV2 createMockHotelAvailabilityForV2ForSixDays() {
    return HotelAvailabilityResultV2.builder()
        .hotelId("HOTEL_123")
        .roomStays(List.of(
            RoomStay.builder()
                .roomTypes(List.of(
                    RoomTypeV2.builder()
                        .roomType("DELUXE")
                        .roomRates(List.of(
                            RoomRateV2.builder()
                                .roomRateInfo(
                                    RoomRateInfoV2.builder()
                                        .priceInfo(List.of(
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.parse(START_DATE_STRING_V2))
                                                .build(),
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.parse(START_DATE_STRING_V2).plusDays(1))
                                                .build(),
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.parse(START_DATE_STRING_V2).plusDays(2))
                                                .build(),
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.parse(START_DATE_STRING_V2).plusDays(3))
                                                .build(),
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.parse(START_DATE_STRING_V2).plusDays(4))
                                                .build(),
                                            PriceInfo.builder()
                                                .amountBeforeTax(new BigDecimal("700.00"))
                                                .amountAfterTax(new BigDecimal("840.00"))
                                                .stayDate(LocalDate.parse(START_DATE_STRING_V2).plusDays(5))
                                                .build()
                                            )
                                        )
                                        .build()
                                )
                                .build()
                        ))
                        .build()
                ))
                .build()
        ))
        .build();
  }

  private PackagesResponse mockPackagesResponse() {
    return PackagesResponse.builder()
        .hotelHasCityTaxForLeisure(Boolean.TRUE)
        .hotelHasCityTaxForBusiness(Boolean.TRUE)
        .build();
  }

  private Basket createMockBasketWithStayItem(String reservationId) {
    var basket = new Basket();
    var stayItem = new BasketItem(null, false, reservationId, "STAY");
    basket.setItems(List.of(stayItem));
    return basket;
  }

  private ReservationLightweightResponseDto createMockLightweightReservationWithReasonOfStay(String reasonOfStay) {
    var reservation = new LightweightReservationByIdDto();
    reservation.setPurposeOfStay(reasonOfStay);

    var lightweightReservation = new ReservationLightweightResponseDto();
    lightweightReservation.setReservationByIdList(List.of(reservation));
    return lightweightReservation;
  }

}
