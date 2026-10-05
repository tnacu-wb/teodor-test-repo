package uk.co.whitbread.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.Room;
import uk.co.whitbread.domain.model.availability.out.RoomPriceBreakdown;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.availability.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.availability.out.SoftBundles;
import uk.co.whitbread.domain.model.availability.out.SoftBundleContent;
import uk.co.whitbread.domain.model.availability.out.DailyPrice;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackageCode;
import uk.co.whitbread.domain.model.packages.out.Packages;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.model.packages.out.Restaurant;
import uk.co.whitbread.domain.model.packages.out.SoftBundle;
import uk.co.whitbread.domain.model.packages.out.UpsellItems;
import uk.co.whitbread.hotel.content.generated.models.ExtrasLabelDto;
import uk.co.whitbread.infrastructure.config.SoftBundlesProperties;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SoftBundlesAndRatesLogicTest {

  @InjectMocks
  private SoftBundlesAndRatesLogic softBundlesAndRatesLogic;

  @Mock
  private SoftBundlesProperties softBundlesProperties;

  private static final String HOTEL_ID = "HEAPTI";

  private final ExtrasLabelDto ancillariesContent;

  public SoftBundlesAndRatesLogicTest() {
    // Initialize with empty extras labels to avoid NullPointerException
    this.ancillariesContent = new ExtrasLabelDto();
    this.ancillariesContent.setExtrasLabels(List.of());
  }

  @ParameterizedTest
  @ValueSource(strings = {"FLEXRATE", "STANDARD"})
  void attachSoftBundlesForRate__ShouldAttachBundlesToMatchingRates(String ratePlanCode) {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");

    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE", "STANDARD"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true); // restaurant open
    MealsInfoResponse upsell = getSoftBundlesResponse_FullBundleForRate(ratePlanCode);
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    // Assert
    RoomRate targetRate = availability.getRoomRates().stream()
            .filter(roomRate -> roomRate.getRatePlanCode().equals(ratePlanCode))
            .findFirst()
            .orElseThrow();

    Room room = targetRate.getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    assertThat(softBundles.getSoftBundleContent(), hasSize(greaterThanOrEqualTo(3)));
    assertThat(
            softBundles.getSoftBundleContent().stream().map(SoftBundleContent::getId).toList(),
            hasItems("BFADBF", "FI24HR", "HSCKIN")
    );
    assertFalse(softBundles.getIsOptional());
  }

  @ParameterizedTest
  @ValueSource(strings = {"ST", "DB"})
  void attachSoftBundlesForRoomClass__ShouldAttachBundlesToMatchingRooms(String roomClass) {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("roomClass");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), roomClass);
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_ForRoomClass(roomClass);
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    assertThat(softBundles.getSoftBundleContent(), hasSize(greaterThanOrEqualTo(1)));
    assertThat(
            softBundles.getSoftBundleContent().stream().map(SoftBundleContent::getId).toList(),
            hasItem("BFADBF"));
    assertEquals("/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme.pdf",
            softBundles.getSoftBundleContent().get(0).getAttachments().get(0).getPath());
    assertEquals("/content/dam/global/restaurants/meals/premier-inn-breakfast.png",
            softBundles.getSoftBundleContent().get(0).getImageSrc());
    assertEquals("Breakfast", softBundles.getSoftBundleContent().get(0).getDescription());
    assertEquals("Premier Inn Breakfast", softBundles.getSoftBundleContent().get(0).getName());
  }

  @Test
  void updateAvailabilityResponseWithSoftBundles__RateBundleSize2AndNoEciLco_ShouldAddBundleWithZeroPrice() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Extras: wifi only, no ECI/LCO available in packages response
    PackagesResponse packagesResponse = getPackagesResponseWithExtrasOnlyWifi();
    MealsInfoResponse upsell = getSoftBundlesResponse_EciPlusOtherForRate("FLEXRATE");

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    // Assert - ECI should be added with data from PackageCode and price 0
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    assertNotNull(room.getSoftBundles());
    assertEquals(1, room.getSoftBundles().getSoftBundleContent().size());
    SoftBundleContent eciBundle = room.getSoftBundles().getSoftBundleContent().get(0);
    assertEquals("HSCKIN", eciBundle.getId());
    assertEquals("Early check-in", eciBundle.getName());
    assertEquals("Early check-in", eciBundle.getDescription());
    assertEquals(BigDecimal.ZERO, eciBundle.getPrice());
    assertEquals("checkin.jpg", eciBundle.getImageSrc());
  }

  @ParameterizedTest
  @ValueSource(strings = {"PP", "PV"})
  void updateAvailabilityResponseWithSoftBundles__WifiShouldBeRemovedForPremierPlusOrView(String roomClass) {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), roomClass);
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_FullBundleForRate("FLEXRATE");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    List<String> ids = softBundles.getSoftBundleContent().stream().map(SoftBundleContent::getId).toList();

    // Wifi should be removed for PP/PV
    assertThat(ids, not(hasItem("FI24HR")));
    // Meal and ECI should remain
    assertThat(ids, hasItem("BFADBF"));
    assertThat(ids, hasItem("HSCKIN"));
  }

  @Test
  void updateAvailabilityResponseWithSoftBundles__OptionalFlag_ShouldBePropagated() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_FullBundleForRate("FLEXRATE");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));
    upsell.getSoftBundles().get(0).setOptional(true);

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    assertTrue(softBundles.getIsOptional());
  }

  @Test
  void updateAvailabilityResponseWithSoftBundles__UnknownParam_ShouldNotAttachBundles() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("somethingElse");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_FullBundleForRate("FLEXRATE");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    // Assert
    availability.getRoomRates().forEach(rr ->
            rr.getRoomTypes().forEach(rt ->
                    rt.getRooms().forEach(room ->
                            assertNull(room.getSoftBundles())
                    )
            )
    );
  }

  @Test
  void updateAvailabilityResponseWithSoftBundles__NullPackageCodes_ShouldNotThrowAndSkipBundle() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(null)
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    assertNull(room.getSoftBundles());
  }

  @Test
  void priceCalculation__MealPricing_ShouldApplyPriceTimesAdultsTimesNights() {
    //Arrange
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    availability.getRoomRates().get(0).getRoomTypes().get(0).setAdults(2);
    availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0)
            .getRoomPriceBreakdown().getDailyPrices().get(0).setEffectiveRate(null);

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    PackagesResponse packagesResponse = getPackagesResponse(true);
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("BFADBF")
                .name("Breakfast")
                .description("Breakfast")
                .images(List.of("breakfast.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    // Assert
    // Meal BFADBF = 10 → 2 adults × 2 nights = 40
    assertEquals(BigDecimal.valueOf(140), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(70), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(70), breakdown.getDailyPrices().get(1).getNetPrice());
    assertEquals(BigDecimal.valueOf(20), breakdown.getDailyPrices().get(0).getEffectiveRate());
    assertEquals(BigDecimal.valueOf(65), breakdown.getDailyPrices().get(1).getEffectiveRate());
    assertNull(breakdown.getTotalRoomNetAmount());
    assertNull(breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertNull(breakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  @Test
  void priceCalculation__WifiPricing_ShouldApplyPriceTimesNights() {
    // Arrange
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("FI24HR")
                .name("Ultimate Wi-Fi")
                .description("Ultimate Wi-Fi")
                .images(List.of("wifi.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));
    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    //Assert
    // Wifi = 5 × 2 nights = 10
    assertEquals(BigDecimal.valueOf(110), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(55), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(55), breakdown.getDailyPrices().get(1).getNetPrice());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(0).getEffectiveRate());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(1).getEffectiveRate());
    // room-only fields should NOT be modified by soft bundle logic
    assertNull(breakdown.getTotalRoomNetAmount());
    assertNull(breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertNull(breakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  @Test
  void priceCalculation__EciPricing_ShouldApplyPriceOnlyToFirstNight() {
    //Arrange
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("HSCKIN")
                .name("Early check-in")
                .description("Early check-in")
                .images(List.of("checkin.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));
    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    // Assert
    // ECI = 10 → only first night
    assertEquals(BigDecimal.valueOf(110), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(60), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(1).getNetPrice());
    assertEquals(BigDecimal.valueOf(55), breakdown.getDailyPrices().get(0).getEffectiveRate());
    assertEquals(BigDecimal.valueOf(45), breakdown.getDailyPrices().get(1).getEffectiveRate());
    // room-only fields should NOT be modified by soft bundle logic
    assertNull(breakdown.getTotalRoomNetAmount());
    assertNull(breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertNull(breakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  @Test
  void priceCalculation__LcoPricing_ShouldApplyPriceOnlyToLastNight() {
    // Arrange

    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("HSCOU2")
                .name("Late check-out")
                .description("Late check-out")
                .images(List.of("checkout.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));
    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    // Assert
    // LCO = 12 → only last night
    assertEquals(BigDecimal.valueOf(112), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(62), breakdown.getDailyPrices().get(1).getNetPrice());
    assertEquals(BigDecimal.valueOf(45), breakdown.getDailyPrices().get(0).getEffectiveRate());
    assertEquals(BigDecimal.valueOf(57), breakdown.getDailyPrices().get(1).getEffectiveRate());
    // room-only fields should NOT be modified by soft bundle logic
    assertNull(breakdown.getTotalRoomNetAmount());
    assertNull(breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertNull(breakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  @Test
  void priceCalculation__LcoPricingStrikeThrough_ShouldNotApplyPrice() {
    // Arrange

    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponseWithNoExtrasAvailability(true);

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("HSCOU2")
                .name("Late check-out")
                .description("Late check-out")
                .images(List.of("checkout.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));
    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();
    // Assert
    assertEquals(BigDecimal.valueOf(100), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(1).getNetPrice());
    assertEquals(BigDecimal.valueOf(45), breakdown.getDailyPrices().get(0).getEffectiveRate());
    assertEquals(BigDecimal.valueOf(45), breakdown.getDailyPrices().get(1).getEffectiveRate());
    // room-only fields should NOT be modified by soft bundle logic
    assertNull(breakdown.getTotalRoomNetAmount());
    assertNull(breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertNull(breakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  @Test
  void priceCalculation__OptionalBundle_ShouldNotApplyAnyPrice() {
    // Arrange

    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("BFADBF")
                .name("Breakfast")
                .description("Breakfast")
                .images(List.of("breakfast.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(true)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));
    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown breakdown = room.getRoomPriceBreakdown();

    // Assert
    assertEquals(BigDecimal.valueOf(100), breakdown.getTotalNetAmount());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(0).getNetPrice());
    assertEquals(BigDecimal.valueOf(50), breakdown.getDailyPrices().get(1).getNetPrice());
    assertEquals(BigDecimal.valueOf(45), breakdown.getDailyPrices().get(0).getEffectiveRate());
    assertEquals(BigDecimal.valueOf(45), breakdown.getDailyPrices().get(1).getEffectiveRate());
    // room-only fields should NOT be modified by soft bundle logic
    assertNull(breakdown.getTotalRoomNetAmount());
    assertNull(breakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertNull(breakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  @Test
  void priceCalculation__RoomNetAmounts_ShouldNotBeModifiedWhenPreSet() {
    // Arrange: pre-populate room-only fields (simulating they were already set by initializeRoomNetAmounts)
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    RoomPriceBreakdown breakdown = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0).getRoomPriceBreakdown();
    breakdown.setTotalRoomNetAmount(BigDecimal.valueOf(100));
    breakdown.getDailyPrices().get(0).setRoomNetPrice(BigDecimal.valueOf(50));
    breakdown.getDailyPrices().get(1).setRoomNetPrice(BigDecimal.valueOf(50));

    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(PackageCode.builder()
                .id("FI24HR")
                .name("Ultimate Wi-Fi")
                .description("Ultimate Wi-Fi")
                .images(List.of("wifi.jpg"))
                .build()))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    MealsInfoResponse upsell = MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of())
            .build();

    PackagesResponse packagesResponse = getPackagesResponse(true);
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(request, availability, packagesResponse, upsell,
        ancillariesContent);

    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    RoomPriceBreakdown updatedBreakdown = room.getRoomPriceBreakdown();

    // Assert: totalNetAmount and netPrice are incremented by wifi bundle cost
    assertEquals(BigDecimal.valueOf(110), updatedBreakdown.getTotalNetAmount());  // 100 + (5 × 2 nights)
    assertEquals(BigDecimal.valueOf(55), updatedBreakdown.getDailyPrices().get(0).getNetPrice());  // 50 + 5
    assertEquals(BigDecimal.valueOf(55), updatedBreakdown.getDailyPrices().get(1).getNetPrice());  // 50 + 5

    // room-only fields must remain unchanged (room price without soft bundles)
    assertEquals(BigDecimal.valueOf(100), updatedBreakdown.getTotalRoomNetAmount());
    assertEquals(BigDecimal.valueOf(50), updatedBreakdown.getDailyPrices().get(0).getRoomNetPrice());
    assertEquals(BigDecimal.valueOf(50), updatedBreakdown.getDailyPrices().get(1).getRoomNetPrice());
  }

  private HotelAvailabilityRequest buildSoftBundlesRequest(String softBundleParam) {
    return HotelAvailabilityRequest.builder()
            .hotelId(HOTEL_ID)
            .channel("PI")
            .subchannel("WEB")
            .language("en")
            .country("gb")
            .arrivalDate(getArrivalDate())
            .departureDate(getDepartureDate())
            .roomTypes(List.of("DB"))
            .adultsNumber(List.of(2))
            .childrenNumber(List.of(0))
            .cotsRequired(List.of(false))
            .softBundle(softBundleParam)
            .build();
  }

  private HotelAvailability getHotelAvailabilityWithRates(List<String> ratePlanCodes, String roomClass) {
    Room room = Room.builder()
            .roomClass(roomClass)
            .roomPriceBreakdown(RoomPriceBreakdown.builder()
                    .totalNetAmount(BigDecimal.valueOf(100))
                    .effectiveRateAmount(BigDecimal.valueOf(100))
                    .dailyPrices(List.of(
                            DailyPrice.builder()
                                    .date(getArrivalDate())
                                    .netPrice(BigDecimal.valueOf(50))
                                    .effectiveRate(BigDecimal.valueOf(45))
                                    .build(),
                            DailyPrice.builder()
                                    .date(getDepartureDate())
                                    .netPrice(BigDecimal.valueOf(50))
                                    .effectiveRate(BigDecimal.valueOf(45))
                                    .build()
                    ))
                    .build())
            .build();

    RoomTypeInfo typeInfo = RoomTypeInfo.builder()
            .roomType("DB")
            .adults(2)
            .rooms(List.of(room))
            .build();

    List<RoomRate> roomRates = ratePlanCodes.stream()
            .map(code -> RoomRate.builder()
                    .ratePlanCode(code)
                    .roomTypes(List.of(typeInfo))
                    .build())
            .toList();

    return HotelAvailability.builder()
            .hotelId(HOTEL_ID)
            .startDate(getArrivalDate())
            .endDate(getDepartureDate())
            .available(true)
            .roomRates(roomRates)
            .build();
  }

  private PackagesResponse getPackagesResponse(boolean restaurantOpen) {
    Restaurant restaurant = Restaurant.builder()
            .restaurantNotFound(!restaurantOpen)
            .noMealsFound(!restaurantOpen)
            .build();

    return PackagesResponse.builder()
            .packages(Packages.builder()
                    .meals(getMeals())
                    .extrasItems(getExtrasDto())
                    .build())
            .restaurant(restaurant)
            .build();
  }

  private PackagesResponse getPackagesResponseWithNoExtrasAvailability(boolean restaurantOpen) {
    Restaurant restaurant = Restaurant.builder()
            .restaurantNotFound(!restaurantOpen)
            .noMealsFound(!restaurantOpen)
            .build();

    return PackagesResponse.builder()
            .packages(Packages.builder()
                    .meals(getMeals())
                    .extrasItems(getExtrasWithoutAvailability())
                    .build())
            .restaurant(restaurant)
            .build();
  }

  private PackagesResponse getPackagesResponseWithExtrasOnlyWifi() {
    Restaurant restaurant = Restaurant.builder()
            .restaurantNotFound(false)
            .noMealsFound(false)
            .build();

    return PackagesResponse.builder()
            .packages(Packages.builder()
                    .meals(getMeals())
                    .extrasItems(getExtrasDtoOnlyWifi())
                    .build())
            .restaurant(restaurant)
            .build();
  }

  private List<Meal> getMeals() {
    return List.of(
            Meal.builder()
                    .id("BFADBF")
                    .name("Premier Inn Breakfast")
                    .price(BigDecimal.valueOf(10))
                    .currency("GBP")
                    .build(),
            Meal.builder()
                    .id("GMDP")
                    .name("Group Meal Deal")
                    .price(BigDecimal.valueOf(15))
                    .currency("GBP")
                    .build(),
            Meal.builder()
                    .id("DINNER01")
                    .name("Dinner")
                    .price(BigDecimal.valueOf(20))
                    .currency("GBP")
                    .build()
    );
  }

  private List<ExtrasDto> getExtrasDto() {
    ExtrasDto wifi = ExtrasDto.builder()
            .id("FI24HR")
            .name("Ultimate Wi-Fi")
            .description("Ultimate Wi-Fi description")
            .price(BigDecimal.valueOf(10))
            .currency("GBP")
            .available(100)
            .imageSrc("/content/dam/global/extras/ultimate-wifi.png")
            .build();

    ExtrasDto earlyCheckIn = ExtrasDto.builder()
            .id("HSCKIN")
            .name("Early check-in")
            .description("Check in from 11am")
            .price(BigDecimal.valueOf(10))
            .currency("GBP")
            .available(50)
            .imageSrc("/content/dam/global/extras/early-check-in.png")
            .build();

    ExtrasDto lateCheckOut = ExtrasDto.builder()
            .id("HSCOU2")
            .name("Late check-out")
            .description("Check out up to 2pm")
            .price(BigDecimal.valueOf(12))
            .currency("GBP")
            .available(30)
            .imageSrc("/content/dam/global/extras/late-check-out.png")
            .build();

    return List.of(wifi, earlyCheckIn, lateCheckOut);
  }

  private List<ExtrasDto> getExtrasDtoOnlyWifi() {
    ExtrasDto wifi = ExtrasDto.builder()
            .id("FI24HR")
            .name("Ultimate Wi-Fi")
            .description("Ultimate Wi-Fi description")
            .price(BigDecimal.valueOf(10))
            .currency("GBP")
            .available(100)
            .imageSrc("/content/dam/global/extras/ultimate-wifi.png")
            .build();

    return List.of(wifi);
  }

  private List<ExtrasDto> getExtrasWithoutAvailability() {
    ExtrasDto wifi = ExtrasDto.builder()
            .id("FI24HR")
            .name("Ultimate Wi-Fi")
            .description("Ultimate Wi-Fi description")
            .price(BigDecimal.valueOf(10))
            .currency("GBP")
            .available(100)
            .imageSrc("/content/dam/global/extras/ultimate-wifi.png")
            .build();

    ExtrasDto earlyCheckIn = ExtrasDto.builder()
            .id("HSCKIN")
            .name("Early check-in")
            .description("Check in from 11am")
            .price(BigDecimal.valueOf(10))
            .currency("GBP")
            .available(0)
            .imageSrc("/content/dam/global/extras/early-check-in.png")
            .build();

    ExtrasDto lateCheckOut = ExtrasDto.builder()
            .id("HSCOU2")
            .name("Late check-out")
            .description("Check out up to 2pm")
            .price(BigDecimal.valueOf(12))
            .currency("GBP")
            .available(0)
            .imageSrc("/content/dam/global/extras/late-check-out.png")
            .build();

    return List.of(wifi, earlyCheckIn, lateCheckOut);
  }

  private MealsInfoResponse getSoftBundlesResponse_FullBundleForRate(String ratePlanCode) {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                        .id("BFADBF")
                        .name("Breakfast")
                        .description("Breakfast")
                        .images(List.of("breakfast.jpg"))
                        .build(),
                    PackageCode.builder()
                        .id("FI24HR")
                        .name("Ultimate Wi-Fi")
                        .description("Ultimate Wi-Fi")
                        .images(List.of("wifi.jpg"))
                        .build(),
                    PackageCode.builder()
                        .id("HSCKIN")
                        .name("Early check-in")
                        .description("Early check-in")
                        .images(List.of("checkin.jpg"))
                        .build()
            ))
            .rate(List.of(ratePlanCode))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    UpsellItems upsellItem = new UpsellItems();
    upsellItem.setShow(true);
    upsellItem.setCode("FI24HR");

    return MealsInfoResponse.builder()
            .upsellItems(List.of(upsellItem))
            .softBundles(List.of(softBundle))
            .build();
  }

  private MealsInfoResponse getSoftBundlesResponse_ForRoomClass(String roomClass) {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                            .id("BFADBF")
                            .name("Premier Inn Breakfast")
                            .description("Breakfast")
                            .attachments(List.of(uk.co.whitbread.domain.model.packages.out.Attachments.builder()
                                    .path("/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme.pdf")
                                    .label("Allergy and nutrition information")
                                    .type("").build()))
                            .images(List.of("/content/dam/global/restaurants/meals/premier-inn-breakfast.png"))
                            .build()
            ))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of(roomClass))
            .optional(false)
            .build();

    UpsellItems upsellItem = new UpsellItems();
    upsellItem.setShow(true);
    upsellItem.setCode("BFADBF");

    return MealsInfoResponse.builder()
            .upsellItems(List.of(upsellItem))
            .softBundles(List.of(softBundle))
            .build();
  }

  private MealsInfoResponse getSoftBundlesResponse_EciPlusOtherForRate(String ratePlanCode) {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                        .id("HSCKIN")
                        .name("Early check-in")
                        .description("Early check-in")
                        .images(List.of("checkin.jpg"))
                        .build()
            ))
            .rate(List.of(ratePlanCode))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    UpsellItems upsellItem = new UpsellItems();
    upsellItem.setShow(true);
    upsellItem.setCode("HSCKIN");

    return MealsInfoResponse.builder()
            .upsellItems(List.of(upsellItem))
            .softBundles(List.of(softBundle))
            .build();
  }


  private String getArrivalDate() {
    return LocalDate.now().plusDays(1).toString();
  }

  private String getDepartureDate() {
    return LocalDate.now().plusDays(3).toString();
  }

  // ============================================
  // Tests for Fallback to UpsellItems
  // ============================================

  // ============================================
  // ============================================
  // Unit tests for new helper methods
  // ============================================

  @Test
  void softBundleNotComplete_ShouldReturnTrue_WhenNameIsNull() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name(null)
            .description("Description")
            .images(List.of("/image.png"))
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertTrue(result);
  }

  @Test
  void softBundleNotComplete_ShouldReturnTrue_WhenNameIsEmpty() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name("")
            .description("Description")
            .images(List.of("/image.png"))
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertTrue(result);
  }

  @Test
  void softBundleNotComplete_ShouldReturnTrue_WhenDescriptionIsNull() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name("Name")
            .description(null)
            .images(List.of("/image.png"))
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertTrue(result);
  }

  @Test
  void softBundleNotComplete_ShouldReturnTrue_WhenDescriptionIsEmpty() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name("Name")
            .description("")
            .images(List.of("/image.png"))
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertTrue(result);
  }

  @Test
  void softBundleNotComplete_ShouldReturnTrue_WhenImagesIsNull() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name("Name")
            .description("Description")
            .images(null)
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertTrue(result);
  }

  @Test
  void softBundleNotComplete_ShouldReturnTrue_WhenImagesIsEmpty() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name("Name")
            .description("Description")
            .images(List.of())
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertTrue(result);
  }

  @Test
  void softBundleNotComplete_ShouldReturnFalse_WhenAllFieldsArePresent() throws Exception {
    // Arrange
    PackageCode packageCode = PackageCode.builder()
            .id("BFADBF")
            .name("Name")
            .description("Description")
            .images(List.of("/image.png"))
            .build();

    // Act
    boolean result = invokeSoftBundleNotComplete(packageCode);

    // Assert
    assertFalse(result);
  }

  @Test
  void getSoftBundlesAttachments_ShouldReturnNull_WhenPackageCodeIsNull() throws Exception {
    // Act
    List<uk.co.whitbread.domain.model.availability.out.Attachments> result = invokeGetSoftBundlesAttachments(null);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getSoftBundlesAttachments_ShouldReturnNull_WhenAttachmentsIsNull() throws Exception {
    // Act
    List<uk.co.whitbread.domain.model.availability.out.Attachments> result = invokeGetSoftBundlesAttachments(null);

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getSoftBundlesAttachments_ShouldReturnNull_WhenAttachmentsIsEmpty() throws Exception {
    // Act
    List<uk.co.whitbread.domain.model.availability.out.Attachments> result = invokeGetSoftBundlesAttachments(List.of());

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getSoftBundlesAttachments_ShouldConvertAttachments_WhenPresent() throws Exception {
    // Arrange
    List<uk.co.whitbread.domain.model.packages.out.Attachments> attachments = List.of(
            uk.co.whitbread.domain.model.packages.out.Attachments.builder()
                    .path("/path1.pdf")
                    .label("Label 1")
                    .type("pdf")
                    .build(),
            uk.co.whitbread.domain.model.packages.out.Attachments.builder()
                    .path("/path2.pdf")
                    .label("Label 2")
                    .type("pdf")
                    .build()
    );

    // Act
    List<uk.co.whitbread.domain.model.availability.out.Attachments> result = invokeGetSoftBundlesAttachments(attachments);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("/path1.pdf", result.get(0).getPath());
    assertEquals("Label 1", result.get(0).getLabel());
    assertEquals("pdf", result.get(0).getType());
    assertEquals("/path2.pdf", result.get(1).getPath());
    assertEquals("Label 2", result.get(1).getLabel());
    assertEquals("pdf", result.get(1).getType());
  }

  @Test
  void hotelHasEci_ShouldReturnTrue_WhenEciIsAvailable() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "HSCKIN", ExtrasDto.builder()
                    .id("HSCKIN")
                    .name("Early Check-in")
                    .available(10)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasEci(extraItemsMap);

    // Assert
    assertTrue(result);
  }

  @Test
  void hotelHasEci_ShouldReturnTrue_WhenEciAvailableIsNull() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "HSCKIN", ExtrasDto.builder()
                    .id("HSCKIN")
                    .name("Early Check-in")
                    .available(null)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasEci(extraItemsMap);

    // Assert
    assertTrue(result);
  }

  @Test
  void hotelHasEci_ShouldReturnFalse_WhenEciAvailableIsZero() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "HSCKIN", ExtrasDto.builder()
                    .id("HSCKIN")
                    .name("Early Check-in")
                    .available(0)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasEci(extraItemsMap);

    // Assert
    assertFalse(result);
  }

  @Test
  void hotelHasEci_ShouldReturnFalse_WhenEciIsNotPresent() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "FI24HR", ExtrasDto.builder()
                    .id("FI24HR")
                    .name("WiFi")
                    .available(10)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasEci(extraItemsMap);

    // Assert
    assertFalse(result);
  }

  @Test
  void hotelHasLco_ShouldReturnTrue_WhenLcoIsAvailable() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "HSCOU2", ExtrasDto.builder()
                    .id("HSCOU2")
                    .name("Late Check-out")
                    .available(5)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasLco(extraItemsMap);

    // Assert
    assertTrue(result);
  }

  @Test
  void hotelHasLco_ShouldReturnFalse_WhenLcoAvailableIsZero() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "HSCOU2", ExtrasDto.builder()
                    .id("HSCOU2")
                    .name("Late Check-out")
                    .available(0)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasLco(extraItemsMap);

    // Assert
    assertFalse(result);
  }

  @Test
  void hotelHasWifi_ShouldReturnTrue_WhenWifiIsAvailable() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "FI24HR", ExtrasDto.builder()
                    .id("FI24HR")
                    .name("Ultimate WiFi")
                    .available(100)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasWifi(extraItemsMap);

    // Assert
    assertTrue(result);
  }

  @Test
  void hotelHasWifi_ShouldReturnFalse_WhenWifiAvailableIsZero() throws Exception {
    // Arrange
    Map<String, ExtrasDto> extraItemsMap = Map.of(
            "FI24HR", ExtrasDto.builder()
                    .id("FI24HR")
                    .name("Ultimate WiFi")
                    .available(0)
                    .build()
    );

    // Act
    boolean result = invokeHotelHasWifi(extraItemsMap);

    // Assert
    assertFalse(result);
  }

  @Test
  void hotelHasMealsAndRestaurantIsOpen_ShouldReturnTrue_WhenRestaurantIsOpen() throws Exception {
    // Arrange
    PackagesResponse response = PackagesResponse.builder()
            .restaurant(Restaurant.builder()
                    .restaurantNotFound(false)
                    .noMealsFound(false)
                    .build())
            .build();

    // Act
    boolean result = invokeHotelHasMealsAndRestaurantIsOpen(response);

    // Assert
    assertTrue(result);
  }

  @Test
  void hotelHasMealsAndRestaurantIsOpen_ShouldReturnFalse_WhenRestaurantNotFound() throws Exception {
    // Arrange
    PackagesResponse response = PackagesResponse.builder()
            .restaurant(Restaurant.builder()
                    .restaurantNotFound(true)
                    .noMealsFound(false)
                    .build())
            .build();

    // Act
    boolean result = invokeHotelHasMealsAndRestaurantIsOpen(response);

    // Assert
    assertFalse(result);
  }

  @Test
  void hotelHasMealsAndRestaurantIsOpen_ShouldReturnFalse_WhenNoMealsFound() throws Exception {
    // Arrange
    PackagesResponse response = PackagesResponse.builder()
            .restaurant(Restaurant.builder()
                    .restaurantNotFound(false)
                    .noMealsFound(true)
                    .build())
            .build();

    // Act
    boolean result = invokeHotelHasMealsAndRestaurantIsOpen(response);

    // Assert
    assertFalse(result);
  }

  @Test
  void hotelHasMealsAndRestaurantIsOpen_ShouldReturnFalse_WhenRestaurantIsNull() throws Exception {
    // Arrange
    PackagesResponse response = PackagesResponse.builder()
            .restaurant(null)
            .build();

    // Act
    boolean result = invokeHotelHasMealsAndRestaurantIsOpen(response);

    // Assert
    assertFalse(result);
  }

  // Helper methods to invoke private methods using reflection
  private boolean invokeSoftBundleNotComplete(PackageCode packageCode) throws Exception {
    java.lang.reflect.Method method = SoftBundlesAndRatesLogic.class.getDeclaredMethod(
            "softBundleNotComplete", PackageCode.class);
    method.setAccessible(true);
    return (boolean) method.invoke(null, packageCode);
  }

  private List<uk.co.whitbread.domain.model.availability.out.Attachments> invokeGetSoftBundlesAttachments(
          List<uk.co.whitbread.domain.model.packages.out.Attachments> packageAttachments) throws Exception {
    java.lang.reflect.Method method = SoftBundlesAndRatesLogic.class.getDeclaredMethod(
            "getSoftBundlesAttachments", List.class);
    method.setAccessible(true);
    return (List<uk.co.whitbread.domain.model.availability.out.Attachments>) method.invoke(null, packageAttachments);
  }

  private boolean invokeHotelHasEci(Map<String, ExtrasDto> extraItemsMap) throws Exception {
    java.lang.reflect.Method method = SoftBundlesAndRatesLogic.class.getDeclaredMethod(
            "hotelHasEci", Map.class);
    method.setAccessible(true);
    return (boolean) method.invoke(softBundlesAndRatesLogic, extraItemsMap);
  }

  private boolean invokeHotelHasLco(Map<String, ExtrasDto> extraItemsMap) throws Exception {
    java.lang.reflect.Method method = SoftBundlesAndRatesLogic.class.getDeclaredMethod(
            "hotelHasLco", Map.class);
    method.setAccessible(true);
    return (boolean) method.invoke(softBundlesAndRatesLogic, extraItemsMap);
  }

  private boolean invokeHotelHasWifi(Map<String, ExtrasDto> extraItemsMap) throws Exception {
    java.lang.reflect.Method method = SoftBundlesAndRatesLogic.class.getDeclaredMethod(
            "hotelHasWifi", Map.class);
    method.setAccessible(true);
    return (boolean) method.invoke(softBundlesAndRatesLogic, extraItemsMap);
  }

  private boolean invokeHotelHasMealsAndRestaurantIsOpen(PackagesResponse response) throws Exception {
    java.lang.reflect.Method method = SoftBundlesAndRatesLogic.class.getDeclaredMethod(
            "hotelHasMealsAndRestaurantIsOpen", PackagesResponse.class);
    method.setAccessible(true);
    return (boolean) method.invoke(softBundlesAndRatesLogic, response);
  }

  // New tests for ancillaries fallback logic

  @Test
  void attachSoftBundles__ShouldUseMealUpsellItemsFallback_WhenPackageCodeIncomplete() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_IncompletePackageCodeWithUpsellItems();

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    assertThat(softBundles.getSoftBundleContent(), hasSize(greaterThanOrEqualTo(1)));
    
    // Find the meal bundle
    SoftBundleContent mealBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("BFADBF"))
            .findFirst()
            .orElseThrow();

    // Verify it used UpsellItems data (fallback)
    assertEquals("UpsellItems Breakfast", mealBundle.getName());
    assertEquals("UpsellItems breakfast description", mealBundle.getDescription());
  }

  @Test
  void attachSoftBundles__ShouldUseWifiExtrasFallback_WhenPackageCodeIncomplete() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_IncompleteWifiPackageCode();

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    
    // Find the WiFi bundle
    SoftBundleContent wifiBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("FI24HR"))
            .findFirst()
            .orElseThrow();

    // Verify it used ExtrasDto data (fallback) from packages response
    assertEquals("Ultimate Wi-Fi", wifiBundle.getName());
    assertEquals("Ultimate Wi-Fi description", wifiBundle.getDescription());
    assertEquals("/content/dam/global/extras/ultimate-wifi.png", wifiBundle.getImageSrc());
  }

  @Test
  void attachSoftBundles__ShouldUseEciExtrasFallback_WhenPackageCodeIncomplete() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_IncompleteEciPackageCode();

    // Create ancillariesContent with fallback data for ECI
    ExtrasLabelDto localAncillariesContent = new ExtrasLabelDto();
    var eciLabel = new uk.co.whitbread.hotel.content.generated.models.ExtrasDto();
    eciLabel.setId("HSCKIN");
    eciLabel.setName("Early check-in");
    eciLabel.setDescription("Check in from 11am");
    eciLabel.setImageSrc("/content/dam/global/extras/early-check-in.png");
    localAncillariesContent.setExtrasLabels(List.of(eciLabel));

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, localAncillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    
    // Find the ECI bundle
    SoftBundleContent eciBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("HSCKIN"))
            .findFirst()
            .orElseThrow();

    // Verify it used ExtrasDto data (fallback) from packages response
    assertEquals("Early check-in", eciBundle.getName());
    assertEquals("Check in from 11am", eciBundle.getDescription());
    assertEquals("/content/dam/global/extras/early-check-in.png", eciBundle.getImageSrc());
  }

  @Test
  void attachSoftBundles__ShouldUseLcoExtrasFallback_WhenPackageCodeIncomplete() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_IncompleteLcoPackageCode();

    // Create ancillariesContent with fallback data for LCO
    ExtrasLabelDto localAncillariesContent = new ExtrasLabelDto();
    var lcoLabel = new uk.co.whitbread.hotel.content.generated.models.ExtrasDto();
    lcoLabel.setId("HSCOU2");
    lcoLabel.setName("Late check-out");
    lcoLabel.setDescription("Check out up to 2pm");
    lcoLabel.setImageSrc("/content/dam/global/extras/late-check-out.png");
    localAncillariesContent.setExtrasLabels(List.of(lcoLabel));

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, localAncillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    
    // Find the LCO bundle
    SoftBundleContent lcoBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("HSCOU2"))
            .findFirst()
            .orElseThrow();

    // Verify it used ExtrasDto data (fallback) from packages response
    assertEquals("Late check-out", lcoBundle.getName());
    assertEquals("Check out up to 2pm", lcoBundle.getDescription());
    assertEquals("/content/dam/global/extras/late-check-out.png", lcoBundle.getImageSrc());
  }

  @Test
  void attachSoftBundles__ShouldUsePackageCodeData_WhenPackageCodeComplete() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    // Use ForRoomClass which has complete PackageCode (name, description, images)
    MealsInfoResponse upsell = getSoftBundlesResponse_ForRoomClass("ST");

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, ancillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    
    // Find the meal bundle
    SoftBundleContent mealBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("BFADBF"))
            .findFirst()
            .orElseThrow();

    // Verify it used PackageCode data (not any fallback) since PackageCode is complete
    assertEquals("Premier Inn Breakfast", mealBundle.getName());
    assertEquals("Breakfast", mealBundle.getDescription());
  }

  @Test
  void attachSoftBundles__ShouldUseLegacyFallback_WhenAncillariesContentIsNull() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_IncompletePackageCode();

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, null);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    
    // Find the meal bundle
    SoftBundleContent mealBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("BFADBF"))
            .findFirst()
            .orElseThrow();

    // Verify it used legacy meal data (not ancillary) - from getMeals() which returns "Premier Inn Breakfast"
    assertEquals("Premier Inn Breakfast", mealBundle.getName());
  }

  @Test
  void attachSoftBundles__ShouldUseLegacyFallback_WhenAncillariesContentHasEmptyList() {
    // Arrange
    HotelAvailabilityRequest request = buildSoftBundlesRequest("rate");
    HotelAvailability availability = getHotelAvailabilityWithRates(List.of("FLEXRATE"), "ST");
    PackagesResponse packagesResponse = getPackagesResponse(true);
    MealsInfoResponse upsell = getSoftBundlesResponse_IncompletePackageCode();
    ExtrasLabelDto localAncillariesContent = new ExtrasLabelDto();
    localAncillariesContent.setExtrasLabels(List.of());

    when(softBundlesProperties.getNonMealPackageIds())
            .thenReturn(List.of("FI24HR", "HSCKIN", "HSCOU2"));

    // Act
    softBundlesAndRatesLogic.updateAvailabilityResponseWithSoftBundles(
            request, availability, packagesResponse, upsell, localAncillariesContent);

    // Assert
    Room room = availability.getRoomRates().get(0).getRoomTypes().get(0).getRooms().get(0);
    SoftBundles softBundles = room.getSoftBundles();

    assertNotNull(softBundles);
    
    // Find the meal bundle
    SoftBundleContent mealBundle = softBundles.getSoftBundleContent().stream()
            .filter(bundle -> bundle.getId().equals("BFADBF"))
            .findFirst()
            .orElseThrow();

    // Verify it used legacy meal data (not ancillary) - from getMeals() which returns "Premier Inn Breakfast"
    assertEquals("Premier Inn Breakfast", mealBundle.getName());
  }

  // Helper methods to create test data for SoftBundles

  private MealsInfoResponse getSoftBundlesResponse_IncompletePackageCode() {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                            .id("BFADBF")
                            .name(null)  // Incomplete - no name
                            .description(null)  // Incomplete - no description
                            .images(null)  // Incomplete - no images
                            .build()
            ))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    return MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .build();
  }

  private MealsInfoResponse getSoftBundlesResponse_IncompletePackageCodeWithUpsellItems() {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                            .id("BFADBF")
                            .name(null)  // Incomplete - no name
                            .description(null)  // Incomplete - no description
                            .images(null)  // Incomplete - no images
                            .build()
            ))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    UpsellItems upsellItem = UpsellItems.builder()
            .code("BFADBF")
            .name("UpsellItems Breakfast")
            .description("UpsellItems breakfast description")
            .images(List.of("/content/dam/upsell/breakfast.png"))
            .build();

    return MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .upsellItems(List.of(upsellItem))
            .build();
  }

  private MealsInfoResponse getSoftBundlesResponse_IncompleteWifiPackageCode() {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                            .id("FI24HR")
                            .name(null)
                            .description(null)
                            .images(null)
                            .build()
            ))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    UpsellItems upsellItem = new UpsellItems();
    upsellItem.setShow(true);
    upsellItem.setCode("FI24HR");

    return MealsInfoResponse.builder()
            .upsellItems(List.of(upsellItem))
            .softBundles(List.of(softBundle))
            .build();
  }

  private MealsInfoResponse getSoftBundlesResponse_IncompleteEciPackageCode() {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                            .id("HSCKIN")
                            .name(null)
                            .description(null)
                            .images(null)
                            .build()
            ))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    return MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .build();
  }

  private MealsInfoResponse getSoftBundlesResponse_IncompleteLcoPackageCode() {
    SoftBundle softBundle = SoftBundle.builder()
            .packageCodes(List.of(
                    PackageCode.builder()
                            .id("HSCOU2")
                            .name(null)
                            .description(null)
                            .images(null)
                            .build()
            ))
            .rate(List.of("FLEXRATE"))
            .roomClass(List.of("ST"))
            .optional(false)
            .build();

    return MealsInfoResponse.builder()
            .softBundles(List.of(softBundle))
            .build();
  }
}
