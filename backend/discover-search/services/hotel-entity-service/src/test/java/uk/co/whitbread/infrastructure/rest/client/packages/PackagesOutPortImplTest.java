package uk.co.whitbread.infrastructure.rest.client.packages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.packages.in.CutOffMinute;
import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.domain.model.packages.in.HotelInformationExtended;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.CutOffExtras;
import uk.co.whitbread.domain.model.packages.out.DonationPackage;
import uk.co.whitbread.domain.model.packages.out.DonationPackagesResponse;
import uk.co.whitbread.domain.model.packages.out.Meal;
import uk.co.whitbread.domain.model.packages.out.MealsInfoResponse;
import uk.co.whitbread.domain.model.packages.out.PackageCode;
import uk.co.whitbread.domain.model.packages.out.Packages;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.model.packages.out.Restaurant;
import uk.co.whitbread.domain.model.packages.out.SoftBundle;
import uk.co.whitbread.domain.model.packages.out.UpsellItems;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.CutOffOutPort;
import uk.co.whitbread.hotel.content.generated.models.*;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackageDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DonationPackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.InventoryAvailabilityDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInventoryResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.MealDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RestaurantDto;
import uk.co.whitbread.infrastructure.config.PackagesProperties;
import uk.co.whitbread.infrastructure.rest.client.availability.model.ItemInventoryRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.OhipClient;
import uk.co.whitbread.infrastructure.rest.client.content.ContentServiceClient;
import uk.co.whitbread.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.ExtrasMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.DonationPackagesRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.DonationPackagesResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.HotelInformationExtendedMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.MealsInfoResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.PackagesRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.mapper.PackagesResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.packages.model.DonationPackagesRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.client.packages.model.PackagesRequestOhipDto;
import uk.co.whitbread.infrastructure.rest.controller.packages.model.out.ExtrasDto;

@ExtendWith(MockitoExtension.class)
class PackagesOutPortImplTest {

  private static final String HOTEL_ID = "TKINPT";
  private static final String START_DATE = "2032-03-01";
  private static final String END_DATE = "2032-03-04";
  private static final String WRONG_START_DATE = "202-03-01";
  public static final int ADULTS = 2;
  public static final int NIGHTS_NUMBER = 2;
  public static final int CHILDREN = 0;
  public static final String TITLE = "Premier Inn Breakfast";
  public static final String ID1 = "HSCKIN";
  public static final String ID2 = "HSCOU2";

  public static final String ID3 = "FI24HR";
  public static final String WRONG_ID = "WRONG";
  public static final String CURRENCY = "EUR";
  public static final BigDecimal PRICE = BigDecimal.valueOf(10L);
  public static final String ECI_CODE = "ECI";
  public static final String LC2_CODE = "LC2";
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  public static final String ID4 = "FHSCOU2";

  @InjectMocks
  private PackagesOutPortImpl packagesOutPort;

  @Mock
  private OhipClient ohipClient;

  @Mock
  private ContentServiceClient contentServiceClient;

  @Mock
  private PackagesRequestMapper packagesRequestMapper;

  @Mock
  private PackagesResponseMapper packagesResponseMapper;

  @Mock
  private DonationPackagesRequestMapper donationPackagesRequestMapper;

  @Mock
  private DonationPackagesResponseMapper donationPackagesResponseMapper;

  @Mock
  private MealsInfoResponseMapper mealsInfoResponseMapper;

  @Mock
  private ExtrasMapper extrasMapper;

  @Mock
  private HotelInformationExtendedMapper hotelInformationExtendedMapper;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private PackagesProperties packagesProperties;

  @Mock
  private ContentServiceOutPort contentServiceOutPort;

  @Mock
  private CutOffOutPort cutOffOutPort;

  @Test
  void getPackages_whenPackageDoesNotExist_ShouldReturnEmptyExtras() {
    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any())).thenReturn(mockWrongPackagesResponse());

    when(contentServiceClient.getExtrasLabels(any(), any())).thenReturn(mockExtrasLabelDto());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
        .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsBottleOfProsecco())).thenReturn(true);

    //Act
    var request = mockPackagesRequest("PI");
    var packageResponse = packagesOutPort.getPackages(request);

    //Assert
    assertThat(packageResponse, notNullValue());
    assertTrue(packageResponse.getPackages().getExtrasItems().isEmpty());
  }

  @Test
  void getPackages_whenChannelIsNull_ShouldReturnEmptyExtras() {
    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsBottleOfProsecco())).thenReturn(true);

    //Act
    var request = mockPackagesRequest(null);

    var packageResponse = packagesOutPort.getPackages(request);

    //Assert
    assertThat(packageResponse, notNullValue());
    assertThat(packageResponse.getPackages().getExtrasItems(), nullValue());
  }

  @ParameterizedTest
  @CsvSource({"PI, true", "BB, true", "CCUI, true","PI, false", "BB, false", "CCUI, false"})
  void getPackages_FF_byChannel_ShouldReturnExtrasItems(String channel, Boolean featureFlag) {
    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    if (featureFlag) {
      when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any())).thenReturn(mockItemInventoryRequestOhipDto());
      when(contentServiceClient.getGlobalConfig(any(), any())).thenReturn(mockGlobalConfigDto());
      when(ohipClient.getItemInventory(mockItemInventoryRequestOhipDto())).thenReturn(mockItemInventoryResponseDto());
      when(contentServiceClient.getExtrasLabels(any(), any()))
              .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));
      when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any())).thenReturn(mockUpsellItemsDto());
      when(extrasMapper.toDto(any(), any(), any())).thenReturn(mockExtrasDto());

      // Mock hotel information and cutoff minutes
      when(contentServiceOutPort.getHotelInformation(any(), any(), any())).thenReturn(mockHotelInformationExtendedDto());
      when(hotelInformationExtendedMapper.toModel(mockHotelInformationExtendedDto())).thenReturn(mockHotelInformationExtended());

      var cutOff = CutOffExtras.builder().build();
      when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
      when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest(channel), cutOff)).thenReturn(
          true);
      when(cutOffOutPort.availableRooms(any(), any(), anyInt())).thenAnswer(invocation -> invocation.getArgument(2));
    }

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    if ("PI".equals(channel)) {
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
              .thenReturn(featureFlag);
    } else if ("BB".equals(channel)) {
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsBb()))
              .thenReturn(featureFlag);
    }else if ("CCUI".equals(channel)) {
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsCcui()))
              .thenReturn(featureFlag);
    }

    //Act
    var packagesResponse = packagesOutPort.getPackages(mockPackagesRequest(channel));

    //Assert
    assertThat(packagesResponse, notNullValue());
    if (featureFlag) {
      assertThat(packagesResponse.getPackages().getExtrasItems(), notNullValue());
      assertThat(packagesResponse.getPackages().getExtrasItems().get(0).getAvailable(), is(10));
      assertEquals(packagesResponse.getPackages().getExtrasItems().get(0).getPrice(), BigDecimal.valueOf(10));
      verify(cutOffOutPort, atLeastOnce()).availableRooms(any(), any(), anyInt());
    } else {
      assertThat(packagesResponse.getPackages().getExtrasItems(), nullValue());
      verify(cutOffOutPort, never()).availableRooms(any(), any(), anyInt());
    }
  }

  @ParameterizedTest(name = "code={0}, shouldApplyCutOff={1}")
  @CsvSource({"HSCKIN,true", "HSCOU2,false"})
  void getPackages_whenExtraRequiresInventory_appliesCutOffRoomsOnlyForHsckin(
      String code, boolean shouldApplyCutOff) {
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
        .thenReturn(mockItemInventoryRequestOhipDto());
    when(contentServiceClient.getGlobalConfig(any(), any())).thenReturn(mockGlobalConfigDto());
    when(ohipClient.getItemInventory(mockItemInventoryRequestOhipDto()))
        .thenReturn(mockItemInventoryResponseDto());
    when(contentServiceClient.getExtrasLabels(any(), any()))
        .thenReturn(mockExtrasLabels(code, true, "START_DATE"));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
        .thenReturn(mockUpsellItemsDto());
    when(extrasMapper.toDto(any(), any(), any())).thenReturn(mockExtrasDto());

    when(contentServiceOutPort.getHotelInformation(any(), any(), any()))
        .thenReturn(mockHotelInformationExtendedDto());
    when(hotelInformationExtendedMapper.toModel(any(HotelInformationExtendedDto.class)))
        .thenReturn(mockHotelInformationExtended());

    var cutOff = CutOffExtras.builder().build();
    when(cutOffOutPort.getCutOffMinute(any(HotelInformationExtended.class)))
        .thenReturn(new CutOffMinute(null, null, null, null));
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime(any(), any(), any())).thenReturn(true);
    if (shouldApplyCutOff) {
      when(cutOffOutPort.availableRooms(any(), any(), anyInt()))
          .thenAnswer(invocation -> invocation.getArgument(2));
    }

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi())).thenReturn(true);

    packagesOutPort.getPackages(mockPackagesRequest("PI"));

    if (shouldApplyCutOff) {
      verify(cutOffOutPort).availableRooms(any(), any(), anyInt());
    } else {
      verify(cutOffOutPort, never()).availableRooms(any(), any(), anyInt());
    }
  }

  @ParameterizedTest
  @CsvSource({"PI, true", "BB, true", "CCUI, true","PI, false", "BB, false", "CCUI, false"})
  void getPackages_FF_byChannel_showFalse_ShouldNotReturnWifi(String channel, Boolean featureFlag) {
    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    if (featureFlag) {
      var upsellItemsDto = mockUpsellItemsDto();
      upsellItemsDto.getUpsellItems().get(0).setShow(false);
      when(contentServiceClient.getGlobalConfig(any(), any())).thenReturn(mockGlobalConfigDto());
      when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any())).thenReturn(mockItemInventoryRequestOhipDto());
      when(ohipClient.getItemInventory(mockItemInventoryRequestOhipDto())).thenReturn(mockItemInventoryResponseDto());
      when(contentServiceClient.getExtrasLabels(any(), any()))
              .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));
      when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any())).thenReturn(upsellItemsDto);
      when(extrasMapper.toDto(any(), any(), any())).thenReturn(mockExtrasDto());
      var cutOff= CutOffExtras.builder().build();
      when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
      when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest(channel), cutOff)).thenReturn(
          true);
    }

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);

    if ("PI".equals(channel)) {
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
              .thenReturn(featureFlag);
    } else if ("BB".equals(channel)) {
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsBb()))
              .thenReturn(featureFlag);
    }else if ("CCUI".equals(channel)) {
      when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsCcui()))
              .thenReturn(featureFlag);
    }

    //Act
    var packagesResponse = packagesOutPort.getPackages(mockPackagesRequest(channel));

    //Assert
    assertThat(packagesResponse, notNullValue());
    if (featureFlag) {
      var wifiPackage = packagesResponse.getPackages().getExtrasItems().stream()
              .filter(extrasDto -> extrasDto.getId().equals("FI24HR"))
              .toList();
      assertTrue(wifiPackage.isEmpty());
    } else {
      assertThat(packagesResponse.getPackages().getExtrasItems(), nullValue());
    }
  }

  @Test
  void getPackages__ShouldReturnException() {
    //Arrange
    when(ohipClient.getPackages(any())).thenThrow(new PackagesException("message",
            "Error while trying to get packages!", new Exception(), 1));

    PackagesRequest packagesRequest = PackagesRequest.builder()
        .hotelId("x")
        .startDate("1")
        .endDate("1")
        .adultsNumber(1)
        .childrenNumber(0)
        .nightsNumber(1)
        .build();

    //Act
    Exception exception = assertThrows(PackagesException.class, () ->
        packagesOutPort.getPackages(packagesRequest));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is("Error while trying to get packages!"));
  }

  @Test
  void getDonationPackagesDetails__ShouldReturnOK() {
    //Arrange
    when(donationPackagesRequestMapper.toOhipDto(any())).thenReturn(
        mockDonationPackagesRequestOhip());

    when(ohipClient.getHotelCharityPackagesDetails(mockDonationPackagesRequestOhip())).thenReturn(
        mockDonationPackagesResponseOhip());

    when(donationPackagesResponseMapper.toModel(any())).thenReturn(mockDonationPackagesResponse());

    //Act
    var donationPackagesResponse = packagesOutPort.getDonationPackageDetails(
        mockDonationPackagesRequest());

    //Assert
    assertThat(donationPackagesResponse, notNullValue());
  }

  @Test
  void getDonationPackagesDetails__ShouldReturnException() {
    //Arrange
    when(ohipClient.getHotelCharityPackagesDetails(any()))
        .thenThrow(new PackagesException("message",
                "Error while trying to get donation packages!", new Exception(), 1));

    DonationPackagesRequest donationPackagesRequest = DonationPackagesRequest.builder()
        .packageCodes(List.of())
        .build();
    //Act
    Exception exception = assertThrows(PackagesException.class, () ->
        packagesOutPort.getDonationPackageDetails(donationPackagesRequest));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is("Error while trying to get donation packages!"));
  }

  @Test
  void getUpsellItemsAndSoftBundles__ShouldReturnOK() {
    //Arrange
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any())).thenReturn(
        mockUpsellItemsDto());

    when(mealsInfoResponseMapper.toDomainModel(any())).thenReturn(mockMealsInfoResponse());

    //Act
    var mealsInfoResponse = packagesOutPort.getUpsellItemsAndSoftBundles(HOTEL_ID, "gb", "en");

    //Assert
    assertThat(mealsInfoResponse, notNullValue());
    assertThat(mealsInfoResponse.getSoftBundles().size(), is(1));
    assertThat(mealsInfoResponse.getSoftBundles().stream()
        .flatMap(sb -> sb.getPackageCodes().stream())
        .map(PackageCode::getId)
        .toList(), contains("BFADBF"));
  }

  @Test
  void getUpsellItems__ShouldReturnException() {
    //Arrange
    when(contentServiceClient.getUpsellItemsAndSoftBundles(HOTEL_ID, "gb", "en"))
        .thenThrow(new PackagesException("message",
            "Error while trying to get upsell items and soft bundles!", new Exception(), 1));
    //Act
    Exception exception = assertThrows(PackagesException.class, () ->
        packagesOutPort.getUpsellItemsAndSoftBundles(HOTEL_ID, "gb", "en"));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(),
        is("Error while trying to get upsell items and soft bundles!"));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, true})
  void getPackages_FF_byChannel_ForManageBookingPage(Boolean extrasItems) {
    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());

    when(contentServiceClient.getGlobalConfig(any(), any()))
            .thenReturn(mockGlobalConfigDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    if (extrasItems) {
      when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any())).thenReturn(mockItemInventoryRequestOhipDto());
      when(ohipClient.getItemInventory(mockItemInventoryRequestOhipDto())).thenReturn(mockItemInventoryResponseDto());
      when(contentServiceClient.getExtrasLabels(any(), any()))
              .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));
      when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any())).thenReturn(mockUpsellItemsDto());
      when(extrasMapper.toDto(any(), any(), any())).thenReturn(mockExtrasDto());
      var cutOff = CutOffExtras.builder().build();
      when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
      when(
          cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequestForManageBookingPage("PI"),
              cutOff)).thenReturn(
          true);
    }

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag())
            .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
            .thenReturn(extrasItems);

    //Act
    var packagesResponse = packagesOutPort.getPackages(mockPackagesRequestForManageBookingPage("PI"));

    //Assert
    assertThat(packagesResponse, notNullValue());
    if (extrasItems) {
      assertThat(packagesResponse.getPackages().getExtrasItems(), notNullValue());
      assertThat(packagesResponse.getPackages().getExtrasItems().get(0).getAvailable(), is(10));
    } else {
      assertThat(packagesResponse.getPackages().getExtrasItems(), nullValue());
    }
  }

  @Test
  void getPackages_addConfiguredDrinkExtras_whenMatchingCodeExists_shouldMoveToExtras() {
    // Arrange
    String matchingMeal = "DBPROS";
    var mockPackagesResponse = mockPackagesResponse();
    var meal = new Meal();
    meal.setIdDesc("Bottle of Prosecco");
    meal.setId(matchingMeal);
    meal.setPrice(PRICE);
    meal.setCurrency(CURRENCY);
    mockPackagesResponse.getPackages().setMeals(new ArrayList<>(List.of(meal)));

    var mockExtrasDto = mockExtrasDto();
    mockExtrasDto.setId(matchingMeal);

    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(mockPackagesRequestOhip())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse);
    when(contentServiceClient.getExtrasLabels(any(), any())).thenReturn(mockExtrasLabelDto());
    when(extrasMapper.toDto(any(), any(), any())).thenReturn(mockExtrasDto);
    when(packagesProperties.getExtrasPackageCodes()).thenReturn(List.of(matchingMeal));

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi())).thenReturn(false);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsBottleOfProsecco())).thenReturn(true);

    //Act
    var packagesResponse = packagesOutPort.getPackages(
        mockPackagesRequestForManageBookingPage("PI"));

    //Assert
    assertThat(packagesResponse, notNullValue());
    List<String> mealIds = packagesResponse.getPackages().getMeals().stream()
        .map(Meal::getId)
        .toList();

    assertThat(mealIds.contains(matchingMeal), is(false));
  }

  @Test
  void getPackages_whenFreePackageExists_shouldPickFreePackage() {
    // Arrange
    var response = mockPackagesResponse();

    response.getPackages().getMeals().get(0).setIsFree(false);
    response.getPackages().getMeals().get(3).setIsFree(true);

    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(any())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(response);

    when(contentServiceClient.getGlobalConfig(any(), any())).thenReturn(mockGlobalConfigDto());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
            .thenReturn(mockItemInventoryRequestOhipDto());
    when(ohipClient.getItemInventory(any())).thenReturn(mockItemInventoryResponseDto());

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCOU2", true, "START_DATE"));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(mockUpsellItemsDto());

    var cutOff = CutOffExtras.builder().build();
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime("HSCOU2", mockPackagesRequest("PI"), cutOff)).thenReturn(
        true);

    when(extrasMapper.toDto(any(), any(), any())).thenReturn(mockExtrasDto());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getFreeFnbExtrasPi()))
            .thenReturn(true);

    // Act
    var result = packagesOutPort.getPackages(mockPackagesRequest("PI"));

    // Assert
    assertThat(result.getPackages().getExtrasItems(), notNullValue());
    assertTrue(
            result.getPackages().getMeals().stream()
                    .anyMatch(meal -> meal.getPromoText() != null)
    );
    verify(cutOffOutPort, never()).availableRooms(any(), any(), anyInt());
  }

  @Test
  void getPackages_whenWifiUpsellPresent_shouldReturnWifiExtra() {

    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(any())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    when(contentServiceClient.getGlobalConfig(any(), any())).thenReturn(mockGlobalConfigDto());

    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(mockUpsellItemsDto());

    ExtrasLabelDto labels = new ExtrasLabelDto();
    labels.setExtrasLabels(List.of());
    labels.setExtrasList(List.of("FI24HR"));

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(labels);

    when(extrasMapper.toDtoFromUpsell(any(), any(), any()))
            .thenReturn(mockUpsellItemsMapperDto());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    //Act
    var result = packagesOutPort.getPackages(mockPackagesRequest("PI"));

    //Assert
    assertThat(result.getPackages().getExtrasItems(), notNullValue());
    assertThat(result.getPackages().getExtrasItems().get(0).getId(), is("FI24HR"));
  }

  @Test
  void getPackages_whenEmployeeChannel_shouldSkipWifi() {

    //Arrange
    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(any())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(mockPackagesResponse());

    when(contentServiceClient.getGlobalConfig(any(), any())).thenReturn(mockGlobalConfigDto());
    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("FIFR24", true, "START_DATE"));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(mockUpsellItemsDto());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
            .thenReturn(true);

    //Act
    var result = packagesOutPort.getPackages(mockPackagesRequest("EMPLOYEE"));

    //Assert
    assertTrue(result.getPackages().getExtrasItems().isEmpty());
  }

  @Test
  void getPackages_whenUpsellItemsExtrasEmpty_shouldUsePaidPackage() {

    // Arrange
    var response = mockPackagesResponse();

    response.getPackages().getMeals().get(0).setIsFree(false);
    response.getPackages().getMeals().get(1).setIsFree(false);

    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(any())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(response);

    when(contentServiceClient.getGlobalConfig(any(), any()))
            .thenReturn(new GlobalConfigDto());

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
            .thenReturn(mockItemInventoryRequestOhipDto());

    when(ohipClient.getItemInventory(any()))
            .thenReturn(mockItemInventoryResponseDto());

    when(extrasMapper.toDto(any(), any(), any()))
            .thenReturn(mockExtrasDto());

    var cutOff = CutOffExtras.builder().isEciCutOffBooking(true).build();
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest("PI"), cutOff)).thenReturn(
        true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getFreeFnbExtrasPi()))
            .thenReturn(true);

    // Act
    var result = packagesOutPort.getPackages(mockPackagesRequest("PI"));

    // Assert
    assertThat(result.getPackages().getExtrasItems(), notNullValue());
    assertThat(result.getPackages().getExtrasItems().get(0).getId(), is("HSCKIN"));
  }

  @Test
  void getPackages_whenFreeMappingExistsButFreePackageMissing_shouldFallbackToPaid() {

    //Arrange
    var response = mockPackagesResponse();

    response.getPackages().getMeals().forEach(m -> m.setIsFree(false));

    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(any())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(response);

    when(contentServiceClient.getGlobalConfig(any(), any()))
            .thenReturn(mockGlobalConfigDto());

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
            .thenReturn(mockItemInventoryRequestOhipDto());

    when(ohipClient.getItemInventory(any()))
            .thenReturn(mockItemInventoryResponseDto());

    when(extrasMapper.toDto(any(), any(), any()))
            .thenReturn(mockExtrasDto());

    var cutOff = CutOffExtras.builder().build();
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest("PI"), cutOff)).thenReturn(
        true);
    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
            .thenReturn(true);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getFreeFnbExtrasPi()))
            .thenReturn(true);

    //Act
    var result = packagesOutPort.getPackages(mockPackagesRequest("PI"));

    //Assert
    assertThat(result.getPackages().getExtrasItems(), notNullValue());
    assertThat(result.getPackages().getExtrasItems().get(0).getId(), is("HSCKIN"));
  }

  @Test
  void getPackages_whenPaidPackageMissing_shouldNotFail() {

    //Arrange
    var response = mockPackagesResponse();

    response.getPackages().setMeals(
            response.getPackages().getMeals().stream()
                    .filter(m -> !"HSCKIN".equals(m.getId()))
                    .toList()
    );

    when(packagesRequestMapper.toOhipDto(any())).thenReturn(mockPackagesRequestOhip());
    when(ohipClient.getPackages(any())).thenReturn(mockPackagesResponseDto());
    when(packagesResponseMapper.toModel(any())).thenReturn(response);

    when(contentServiceClient.getGlobalConfig(any(), any()))
            .thenReturn(new GlobalConfigDto());

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getExtrasItemsPi()))
            .thenReturn(true);

    //Act
    var result = packagesOutPort.getPackages(mockPackagesRequest("PI"));

    //Assert
    assertThat(result.getPackages().getExtrasItems(), notNullValue());
  }

  @Test
  void getPackages_whenInvalidReferenceDateType_shouldThrowException() {

    // Arrange
    ItemInventoryResponseDto inventoryResponse = new ItemInventoryResponseDto();

    ItemInventoryDto item = new ItemInventoryDto();
    item.setCode(ECI_CODE);

    InventoryAvailabilityDto inv = new InventoryAvailabilityDto();
    inv.setDate(START_DATE);
    inv.setAvailable(10);

    item.setInventories(List.of(inv));
    inventoryResponse.setItemsInventory(List.of(item));

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(mockPackagesResponse());

    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
            .thenReturn(mockItemInventoryRequestOhipDto());
    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, WRONG_START_DATE));

    when(ohipClient.getItemInventory(any()))
            .thenReturn(inventoryResponse);
    var cutOff = CutOffExtras.builder().build();
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest("PI"), cutOff)).thenReturn(
        true);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    // Act + Assert
    PackagesRequest request = mockPackagesRequest("PI");
    assertThrows(
            PackagesException.class,
            () -> packagesOutPort.getPackages(request)
    );
  }

  @Test
  void getAvailability_whenNoMatchingDate_shouldThrowException() {

    // Arrange
    InventoryAvailabilityDto inv = new InventoryAvailabilityDto();
    inv.setDate("WRONG_DATE");
    inv.setAvailable(10);

    ItemInventoryDto item = new ItemInventoryDto();
    item.setCode(ECI_CODE);
    item.setInventories(List.of(inv));

    ItemInventoryResponseDto inventoryResponse = new ItemInventoryResponseDto();
    inventoryResponse.setItemsInventory(List.of(item));

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(mockPackagesResponse());

    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
            .thenReturn(mockItemInventoryRequestOhipDto());
    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));

    when(ohipClient.getItemInventory(any()))
            .thenReturn(inventoryResponse);

    var cutOff = CutOffExtras.builder().build();
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest("PI"), cutOff)).thenReturn(
        true);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    // Act + Assert
    PackagesRequest request = mockPackagesRequest("PI");
    assertThrows(
            PackagesException.class,
            () -> packagesOutPort.getPackages(request)
    );
  }

  @Test
  void getPackages_whenPackagesRequestIsNull_shouldThrowException() {

    NullPointerException exception = assertThrows(
            NullPointerException.class,
            () -> packagesOutPort.getPackages(null)
    );

    assertEquals(
            "packagesRequest must not be null",
            exception.getMessage()
    );
  }

  @Test
  void getAvailability_whenInventoryIsNull_shouldThrowException() {

    // Arrange
    ItemInventoryDto item = new ItemInventoryDto();
    item.setCode("AAA");
    item.setInventories(Collections.emptyList());

    ItemInventoryResponseDto inventoryResponse = new ItemInventoryResponseDto();
    inventoryResponse.setItemsInventory(List.of(item));

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(mockPackagesResponse());

    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    when(packagesRequestMapper.toItemInventoryRequestOhipDto(any(), any()))
            .thenReturn(mockItemInventoryRequestOhipDto());

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));

    when(ohipClient.getItemInventory(any()))
            .thenReturn(inventoryResponse);

    var cutOff = CutOffExtras.builder().build();
    when(cutOffOutPort.isCutOffByHotel(any(), any())).thenReturn(cutOff);
    when(cutOffOutPort.isOutsideCutOffTime("HSCKIN", mockPackagesRequest("PI"), cutOff)).thenReturn(
        true);

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    // Act + Assert
    PackagesRequest request = mockPackagesRequest("PI");

    assertThrows(
            PackagesException.class,
            () -> packagesOutPort.getPackages(request)
    );
  }

  @ParameterizedTest
  @CsvSource({
          ", en",
          "gb, ",
          ", "
  })
  void getPackages_whenCountryOrLanguageIsNull_shouldSkipExtrasLabelsCall(
          String country,
          String language) {

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(mockPackagesResponse());

    var mockedFeatureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);

    PackagesRequest request = PackagesRequest.builder()
            .channel("PI")
            .country(country)
            .language(language)
            .hotelId(HOTEL_ID)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .nightsNumber(NIGHTS_NUMBER)
            .childrenNumber(CHILDREN)
            .build();

    assertDoesNotThrow(() -> packagesOutPort.getPackages(request));

    verify(contentServiceClient, never())
            .getExtrasLabels(any(), any());
  }

  @Test
  void getPackages_whenMealIdIsNull_shouldSkipInventoryMapping() {

    // Arrange
    PackagesResponse response = mockPackagesResponse();

    Meal meal = response.getPackages().getMeals().get(0);
    meal.setId(null);

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(response);

    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(new MealsInfoResponseDto());

    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("HSCKIN", true, "START_DATE"));

    FeatureFlag featureFlag = mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(true);

    // Act & Assert
    assertDoesNotThrow(() -> packagesOutPort.getPackages(mockPackagesRequest("PI")));
  }

  @Test
  void applyFreeMealLogic_shouldMarkPromoMealAsFreeAndRemovePaidMeal() {

    // Arrange
    PackagesRequest request = mockPackagesRequest("PI")
                              .toBuilder()
                              .packageSelections("OBFPRO")
                              .build();

    PackagesResponse response = mockPackagesResponse();

    response.getPackages().setMeals(new ArrayList<>(List.of(
            Meal.builder()
                    .id("OBFBRK")
                    .price(BigDecimal.valueOf(10))
                    .currency("GBP")
                    .build(),

            Meal.builder()
                    .id("OBFPRO")
                    .price(BigDecimal.valueOf(-10))
                    .currency("GBP")
                    .build()
    )));

    FeatureFlag featureFlag = mockFeatureFlag(request.getChannel());

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);

    when(unleashWrapper.isEnabled(featureFlag.getExtrasItemsPi()))
            .thenReturn(true);

    when(unleashWrapper.isEnabled(featureFlag.getFreeFnbExtrasPi()))
            .thenReturn(true);

    when(contentServiceClient.getGlobalConfig("gb", "en"))
            .thenReturn(mockGlobalConfigResponse());
    when(contentServiceClient.getExtrasLabels(any(), any()))
            .thenReturn(mockExtrasLabels("OBFBRK", false, null));
    when(contentServiceClient.getUpsellItemsAndSoftBundles(any(), any(), any()))
            .thenReturn(mockUpsellItemsDto());

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(response);

    // Act
    PackagesResponse result = packagesOutPort.getPackages(request);

    // Assert
    Meal resultPromoMeal = result.getPackages().getMeals().stream()
            .filter(meal -> "OBFPRO".equals(meal.getId()))
            .findFirst()
            .orElseThrow();

    assertEquals(Boolean.TRUE, resultPromoMeal.getIsFree());
    assertEquals(BigDecimal.ZERO, resultPromoMeal.getPrice());

    assertEquals(1, result.getPackages().getMeals().size());
    assertEquals("OBFPRO",
            result.getPackages().getMeals().getFirst().getId());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("provideNoFreeMealLogicScenarios")
  void getPackages_shouldNotApplyFreeMealLogic_whenPromoConditionsAreNotMet(
      String scenario, String packageSelections, BigDecimal promoPrice, boolean freeFnbEnabled) {

    // Arrange
    PackagesRequest request = PackagesRequest.builder()
            .channel("PI")
            .country("gb")
            .language("en")
            .hotelId(HOTEL_ID)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .nightsNumber(NIGHTS_NUMBER)
            .childrenNumber(CHILDREN)
            .packageSelections(packageSelections)
            .build();

    Meal paidMeal = new Meal();
    paidMeal.setId("OBFBRK");
    paidMeal.setPrice(BigDecimal.valueOf(10));

    Meal promoMeal = new Meal();
    promoMeal.setId("OBFPRO");
    promoMeal.setPrice(promoPrice);

    Packages packages = new Packages();
    packages.setMeals(new ArrayList<>(List.of(paidMeal, promoMeal)));

    PackagesResponse response = new PackagesResponse();
    response.setPackages(packages);

    FeatureFlag featureFlag = mockFeatureFlag(request.getChannel());

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);

    when(unleashWrapper.isEnabled(featureFlag.getExtrasItemsPi()))
            .thenReturn(false);

    when(unleashWrapper.isEnabled(featureFlag.getFreeFnbExtrasPi()))
            .thenReturn(freeFnbEnabled);

    when(packagesRequestMapper.toOhipDto(any()))
            .thenReturn(mockPackagesRequestOhip());

    when(ohipClient.getPackages(any()))
            .thenReturn(mockPackagesResponseDto());

    when(packagesResponseMapper.toModel(any()))
            .thenReturn(response);

    when(contentServiceClient.getExtrasLabels(any(), any())).thenReturn(mockExtrasLabelDto());


    // Act
    PackagesResponse result = packagesOutPort.getPackages(request);

    // Assert
    Meal resultPromoMeal = result.getPackages().getMeals().stream()
            .filter(meal -> "OBFPRO".equals(meal.getId()))
            .findFirst()
            .orElseThrow();

    assertFalse(resultPromoMeal.getIsFree());
    assertNull(resultPromoMeal.getBasePrice());
    assertEquals(promoPrice, resultPromoMeal.getPrice());
    assertEquals(2, result.getPackages().getMeals().size());
    verify(contentServiceClient, never()).getGlobalConfig("gb", "en");
  }

  private static Stream<Arguments> provideNoFreeMealLogicScenarios() {
    return Stream.of(
        Arguments.of("hasFreeMealPromotion is false", "UNKNOWN", BigDecimal.valueOf(5), true),
        Arguments.of("freeFnbEnabled is false", "OBFPRO", BigDecimal.valueOf(-10), false)
    );
  }

  private GlobalConfigDto mockGlobalConfigResponse() {

    UpsellItemsExtraDto mapping = new UpsellItemsExtraDto();
    mapping.setPackageCode("OBFBRK");
    mapping.setPromoPackageCode("OBFPRO");

    GlobalConfigDto response = new GlobalConfigDto();
    response.setUpsellItemsExtras(List.of(mapping));

    return response;
  }

  private PackagesRequestOhipDto mockPackagesRequestOhip() {
    return PackagesRequestOhipDto
        .builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .build();
  }

  private ItemInventoryRequestOhipDto mockItemInventoryRequestOhipDto() {
    return ItemInventoryRequestOhipDto.builder()
            .hotelId(HOTEL_ID)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .itemCodes(List.of(ECI_CODE, LC2_CODE))
            .build();
  }

  private PackagesRequest mockPackagesRequest(String channel) {
    return PackagesRequest
        .builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .nightsNumber(NIGHTS_NUMBER)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .nightsNumber(NIGHTS_NUMBER)
        .channel(channel)
        .country(COUNTRY)
        .language(LANGUAGE)
        .build();
  }


  private PackagesResponseDto mockPackagesResponseDto() {

    var restaurant = new RestaurantDto();
    restaurant.setRestaurantNotFound(false);

    var meal = new MealDto();
    meal.setTitle(TITLE);
    meal.setId(ID1);
    meal.setPrice(PRICE);
    meal.setCurrency(CURRENCY);

    var packages = new PackagesDto();
    packages.setMeals(Collections.singletonList(meal));

    var packagesResponseDto = new PackagesResponseDto();
    packagesResponseDto.setPackages(packages);
    packagesResponseDto.setRestaurant(restaurant);
    packagesResponseDto.setHotelHasCityTaxForBusiness(Boolean.FALSE);
    packagesResponseDto.setHotelHasCityTaxForLeisure(Boolean.TRUE);

    return packagesResponseDto;

  }

  private ItemInventoryResponseDto mockItemInventoryResponseDto() {

    InventoryAvailabilityDto inventoryAvailabilityDto1 = new InventoryAvailabilityDto();
    inventoryAvailabilityDto1.setDate(START_DATE);
    inventoryAvailabilityDto1.setAvailable(10);

    InventoryAvailabilityDto inventoryAvailabilityDto2 = new InventoryAvailabilityDto();
    inventoryAvailabilityDto2.setDate(END_DATE);
    inventoryAvailabilityDto2.setAvailable(10);

    ItemInventoryDto eciItem = new ItemInventoryDto();
    eciItem.setCode(ECI_CODE);
    eciItem.setInventories(List.of(inventoryAvailabilityDto1, inventoryAvailabilityDto2));

    ItemInventoryDto lc2Item = new ItemInventoryDto();
    lc2Item.setCode(LC2_CODE);
    lc2Item.setInventories(List.of(inventoryAvailabilityDto1, inventoryAvailabilityDto2));

    ItemInventoryResponseDto itemInventoryResponseDto = new ItemInventoryResponseDto();
    itemInventoryResponseDto.setItemsInventory(List.of(eciItem, lc2Item));

    return itemInventoryResponseDto;
  }

  private PackagesResponse mockPackagesResponse() {

    var restaurant = Restaurant.builder()
        .noMealsFound(false)
        .build();

    var meal1 = Meal.builder()
        .name(TITLE)
        .id(ID1)
        .price(PRICE)
        .currency(CURRENCY)
        .inventoryItem("ECI")
        .build();

    var meal2 = Meal.builder()
            .name(TITLE)
            .id(ID2)
            .price(PRICE)
            .currency(CURRENCY)
            .inventoryItem("LC2")
            .build();

    var meal3 = Meal.builder()
            .name(TITLE)
            .id(ID3)
            .price(PRICE)
            .currency(CURRENCY)
            .build();

    var meal4 = Meal.builder()
            .name(TITLE)
            .id(ID4)
            .price(PRICE)
            .currency(CURRENCY)
            .inventoryItem("LC2")
            .build();

    var packages = Packages.builder()
        .meals(List.of(meal1, meal2, meal3, meal4))
        .build();

    return PackagesResponse.builder()
        .restaurant(restaurant)
        .packages(packages)
        .hotelHasCityTaxForBusiness(Boolean.FALSE)
        .hotelHasCityTaxForLeisure(Boolean.TRUE)
        .build();

  }

  private PackagesResponse mockWrongPackagesResponse() {

    var packages = Packages.builder()
            .meals(List.of(Meal.builder().id(WRONG_ID).build()))
            .build();

    return PackagesResponse.builder()
            .packages(packages)
            .hotelHasCityTaxForBusiness(Boolean.FALSE)
            .hotelHasCityTaxForLeisure(Boolean.TRUE)
            .build();

  }

  private ExtrasDto mockExtrasDto() {
    return ExtrasDto.builder().available(10).id(ID1).price(BigDecimal.valueOf(10)).build();
  }

  private ExtrasDto mockUpsellItemsMapperDto() {
    return ExtrasDto.builder().available(10).id(ID3).price(BigDecimal.valueOf(5)).build();
  }

  private ExtrasLabelDto mockExtrasLabelDto() {
    var extrasLabelDto = new ExtrasLabelDto();
    extrasLabelDto.setExtrasLabels(List.of());
    return extrasLabelDto;
  }

  private MealsInfoResponseDto mockUpsellItemsDto() {
    var upsellItemsDto = new MealsInfoResponseDto();
    var upsellItems = new UpsellItemsDto();
    upsellItems.setShow(true);
    upsellItems.setCode("FI24HR");
    upsellItemsDto.setUpsellItems(List.of(upsellItems));

    var softBundle = new SoftBundleDto();
    var pkgCode = new PackageCodeDto();
    pkgCode.setId("BFADBF");
    pkgCode.setDescription("Unlimited Premier Inn Breakfast for entire stay");
    softBundle.setPackageCodes(List.of(pkgCode));
    softBundle.setRate(List.of("FLEXRATE", "SEMIFLEX"));
    softBundle.setRoomClass(List.of("ST", "PP", "SV"));
    softBundle.setOptional(false);
    upsellItemsDto.setSoftBundles(List.of(softBundle));
    return upsellItemsDto;
  }

  private MealsInfoResponse mockMealsInfoResponse() {
    var upsellItems = new UpsellItems();
    upsellItems.setShow(true);
    upsellItems.setCode("FI24HR");

    var softBundle = new SoftBundle();
    var pkgCode = new PackageCode();
    pkgCode.setId("BFADBF");
    pkgCode.setDescription("Unlimited Premier Inn Breakfast for entire stay");
    softBundle.setPackageCodes(List.of(pkgCode));
    softBundle.setRate(List.of("FLEXRATE", "SEMIFLEX"));
    softBundle.setRoomClass(List.of("ST", "PP", "SV"));
    softBundle.setOptional(false);

    return MealsInfoResponse.builder()
        .upsellItems(List.of(upsellItems))
        .softBundles(List.of(softBundle))
        .build();
  }

  private DonationPackagesRequestOhipDto mockDonationPackagesRequestOhip() {
    return DonationPackagesRequestOhipDto.builder()
        .hotelId(HOTEL_ID)
        .packageCodes(List.of("CHRTY3", "CHRTY4", "CHRTY5"))
        .build();
  }

  private DonationPackageDto mockDonationPackageOhip(String code, BigDecimal unitPrice,
      String currency) {
    var donationPackageDetails = new DonationPackageDto();
    donationPackageDetails.setCode(code);
    donationPackageDetails.currency(currency);
    donationPackageDetails.unitPrice(unitPrice);
    return donationPackageDetails;
  }

  private DonationPackagesResponseDto mockDonationPackagesResponseOhip() {
    var donationPackagesOhip = new DonationPackagesResponseDto();
    donationPackagesOhip.setDonationPackages(
        List.of(mockDonationPackageOhip("CHRTY3", BigDecimal.valueOf(5), "GBP"),
            mockDonationPackageOhip("CHRTY4", BigDecimal.valueOf(3), "GBP"),
            mockDonationPackageOhip("CHRTY5", BigDecimal.valueOf(1), "GBP"))
    );
    return donationPackagesOhip;
  }

  private DonationPackage mockDonationPackage(String code, BigDecimal unitPrice, String currency) {
    return DonationPackage.builder().code(code).unitPrice(unitPrice).currency(currency).build();
  }

  private DonationPackagesResponse mockDonationPackagesResponse() {
    return DonationPackagesResponse.builder()
        .donationPackages(List.of(
            mockDonationPackage("CHRTY3", BigDecimal.valueOf(5), "GBP"),
            mockDonationPackage("CHRTY4", BigDecimal.valueOf(3), "GBP"),
            mockDonationPackage("CHRTY5", BigDecimal.valueOf(1), "GBP")
        )).build();
  }

  private DonationPackagesRequest mockDonationPackagesRequest() {
    return DonationPackagesRequest
        .builder()
        .hotelId(HOTEL_ID)
        .packageCodes(List.of("CHRTY3", "CHRTY4", "CHRTY5"))
        .build();
  }

  private PackagesRequest mockPackagesRequestForManageBookingPage(String channel) {
    return PackagesRequest
            .builder()
            .hotelId(HOTEL_ID)
            .startDate(START_DATE)
            .endDate(END_DATE)
            .nightsNumber(NIGHTS_NUMBER)
            .adultsNumber(ADULTS)
            .childrenNumber(CHILDREN)
            .nightsNumber(NIGHTS_NUMBER)
            .channel(channel)
            .country(COUNTRY)
            .language(LANGUAGE)
            .isManageBookingPage(Boolean.TRUE)
            .build();
  }

  private GlobalConfigDto mockGlobalConfigDto() {

    GlobalConfigDto dto = new GlobalConfigDto();

    UpsellItemsExtraDto item1 = new UpsellItemsExtraDto();
    item1.setPackageCode("FI24HR");
    item1.setPromoPackageCode("FIFR24");
    item1.setPromoText("Free Ultimate Wi-Fi - 24 hours");

    UpsellItemsExtraDto item2 = new UpsellItemsExtraDto();
    item2.setPackageCode("HSCKIN");
    item2.setPromoPackageCode("HSCKIF");
    item2.setPromoText("Free Early Check In");

    UpsellItemsExtraDto item3 = new UpsellItemsExtraDto();
    item3.setPackageCode("HSCOU2");
    item3.setPromoPackageCode("FHSCOU2");
    item3.setPromoText("Free Late Check Out");

    dto.setUpsellItemsExtras(List.of(item1, item2, item3));

    return dto;
  }

  private ExtrasLabelDto mockExtrasLabels(String id, boolean requiresInventory, String date) {
    var label = new uk.co.whitbread.hotel.content.generated.models.ExtrasDto();
    label.setId(id);
    label.setReferenceDateType(date);
    label.setRequiresInventory(requiresInventory);

    var labels = new ExtrasLabelDto();
    labels.setExtrasLabels(List.of(label));
    labels.setExtrasList(List.of(id));

    return labels;
  }

  private FeatureFlag mockFeatureFlag(String channel) {

    FeatureFlag featureFlag = new FeatureFlag();

    FeatureFlag.Feature feature = new FeatureFlag.Feature();
    feature.setFallback(false);

    switch (channel) {
      case "PI" -> {
        feature.setKey("release_pi_free_fnb_and_extras");
        featureFlag.setFreeFnbExtrasPi(feature);
        featureFlag.setExtrasItemsPi(feature);
      }
      case "BB" -> {
        feature.setKey("release_bb_free_fnb_and_extras");
        featureFlag.setFreeFnbExtrasBb(feature);
        featureFlag.setExtrasItemsBb(feature);
      }
      case "CCUI" -> {
        feature.setKey("release_ccui_free_fnb_and_extras");
        featureFlag.setFreeFnbExtrasCcui(feature);
        featureFlag.setExtrasItemsCcui(feature);
      }
      default -> throw new IllegalArgumentException(
              "Unsupported channel: " + channel);
    }

    return featureFlag;
  }

  /**
   * Creates a mock HotelInformationExtendedDto with cutoff configuration.
   *
   * @return HotelInformationExtendedDto with HSCKIN and HSCOU2 cutoff values
   */
  private HotelInformationExtendedDto mockHotelInformationExtendedDto() {
    var hsckinCutoff = new ExtraCutoffDto();
    hsckinCutoff.setCode("HSCKIN");
    hsckinCutoff.setCutOffMinutesCIOL(60);
    hsckinCutoff.setCutOffMinutesBooking(120);

    var hscou2Cutoff = new ExtraCutoffDto();
    hscou2Cutoff.setCode("HSCOU2");
    hscou2Cutoff.setCutOffMinutesCIOL(90);
    hscou2Cutoff.setCutOffMinutesBooking(150);

    var hotelInfo = new HotelInformationExtendedDto();
    hotelInfo.setExtrasCutoffs(List.of(hsckinCutoff, hscou2Cutoff));
    return hotelInfo;
  }

  /**
   * Creates a mock HotelInformationExtended domain model (transformed from DTO).
   *
   * @return HotelInformationExtended with cutoff configuration
   */
  private uk.co.whitbread.domain.model.packages.in.HotelInformationExtended mockHotelInformationExtended() {
    var hsckinCutoff = new uk.co.whitbread.domain.model.packages.in.ExtrasCutoff();
    hsckinCutoff.setCode("HSCKIN");
    hsckinCutoff.setCutOffMinutesCIOL(60);
    hsckinCutoff.setCutOffMinutesBooking(120);

    var hscou2Cutoff = new uk.co.whitbread.domain.model.packages.in.ExtrasCutoff();
    hscou2Cutoff.setCode("HSCOU2");
    hscou2Cutoff.setCutOffMinutesCIOL(90);
    hscou2Cutoff.setCutOffMinutesBooking(150);

    return uk.co.whitbread.domain.model.packages.in.HotelInformationExtended.builder()
        .extrasCutoffs(List.of(hsckinCutoff, hscou2Cutoff))
        .build();
  }

}
