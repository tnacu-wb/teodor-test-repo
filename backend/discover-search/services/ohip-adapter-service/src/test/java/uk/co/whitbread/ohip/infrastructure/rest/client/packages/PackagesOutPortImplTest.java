package uk.co.whitbread.ohip.infrastructure.rest.client.packages;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackagesInfoPackageCodesList;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.HotelPackageGroupsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfoPackageGroupList;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageCodeRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.Meal;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageCodes;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroups;
import uk.co.whitbread.ohip.domain.model.packages.out.PackageGroupsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.exceptions.PackagesException;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.DonationPackagesRequestOhipMapperImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.DonationPackagesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.MealResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupsRequestOhipMapperImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesRequestOhipMapperImpl;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesResponseOhipMapperTest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.RestaurantsResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipPackagesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipRestaurantsClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelPackageException;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class PackagesOutPortImplTest {

  public static final String MOCK_PACKAGES_RESPONSE_JSON = "__files/ohip_packageCodesList_object.json";
  public static final String MOCK_CHARITY_PACKAGES_RESPONSE_JSON = "__files/ohip_charityPackageCodesList_object.json";
  public static final String MOCK_RESTAURANTS_RESPONSE_JSON = "__files/ohip_hotelConfigInfo_object.json";
  public static final String ID = "BKFSTTEST";
  public static final String TITLE = "Premier Inn Breakfast";
  public static final String SYMBOL = "GBP";
  public static final BigDecimal PRICE = BigDecimal.valueOf(10L);
  public static final String PROMOTION_RATE = "PROMO";
  public static final String PROMOTION_PACKAGE = "PROMO_PACKAGE";

  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

  @Mock
  private OhipPackagesClient ohipPackagesClient;

  @Mock
  private OhipRestaurantsClient ohipRestaurantsClient;

  @Mock
  private MealResponseOhipMapper mealResponseOhipMapper;

  @InjectMocks
  private PackagesOutPortImpl packagesOutPort;

  @BeforeEach
  void init() {
    var packagesRequestOhipMapper = new PackagesRequestOhipMapperImpl();
    var charityPackagesRequestOhipMapper = new DonationPackagesRequestOhipMapperImpl();
    var packageGroupRequestOhipMapper = new PackageGroupsRequestOhipMapperImpl();
    var packageGroupResponseOhipMapper = new PackageGroupResponseOhipMapper();
    var packagesResponseOhipMapper = new PackagesResponseOhipMapper(
       mealResponseOhipMapper);
    var charityPackagesResponseOhipMapper = new DonationPackagesResponseOhipMapper();

    packagesOutPort = new PackagesOutPortImpl(
        packagesRequestOhipMapper, charityPackagesRequestOhipMapper, packagesResponseOhipMapper,
        charityPackagesResponseOhipMapper,ohipPackagesClient, ohipRestaurantsClient,
        new ConcurrentTracer(Tracer.NOOP),packageGroupRequestOhipMapper,packageGroupResponseOhipMapper);
  }

  @Test
  void getPackages__shouldReturnOk() throws IOException {

    // Arrange
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.SELL_SEPARATE_PARAM, singletonList("true"));
    params.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList("true"));

    when(ohipRestaurantsClient.getHotelRestaurants(any()))
        .thenReturn(getRestaurantsFromOhip());
    when(ohipPackagesClient.getPackages(any(), eq(params)))
        .thenReturn(getPackagesFromOhip());
    when(mealResponseOhipMapper.toModel(any(), any())).thenReturn(createMeal());

    // Act
    var response = packagesOutPort.getPackages(createPackagesRequest());

    // Assert
    assertNotNull(response);

    var packages = response.getPackages();
    assertNotNull(packages);
    assertNotNull(packages.getMeals());

    var meal = packages.getMeals().get(0);
    assertNotNull(meal);
    assertEquals(ID, meal.getId());
    assertEquals(TITLE, meal.getTitle());
    assertEquals(SYMBOL, meal.getCurrency());
    assertEquals(PRICE, meal.getPrice());

    var restaurant = response.getRestaurant();
    assertNotNull(restaurant);
    assertEquals(Boolean.FALSE, restaurant.getRestaurantNotFound());
    assertEquals(Boolean.FALSE, restaurant.getNoMealsFound());

  }

  @Test
  void getPackagesMealInclusiveRates__shouldReturnOk() throws IOException {

    // Arrange
    var packagesRequest = createPackagesRequest().toBuilder()
        .mealInclusiveRate(Boolean.TRUE)
        .build();

    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList("true"));

    when(ohipRestaurantsClient.getHotelRestaurants(any()))
        .thenReturn(getRestaurantsFromOhip());
    when(ohipPackagesClient.getPackages(any(), eq(params)))
        .thenReturn(getPackagesFromOhip());
    when(mealResponseOhipMapper.toModel(any(), any()))
            .thenReturn(createMeal());
    // Act
    var response = packagesOutPort.getPackages(packagesRequest);

    // Assert
    assertNotNull(response);
    assertNotNull(response.getPackages());
    assertNotNull(response.getPackages().getMeals());
    assertEquals(3, response.getPackages().getMeals().size());

    verify(ohipPackagesClient).getPackages(any(), eq(params));
  }

  @Test
  void getPackages__addPromotionalPackages__shouldReturnOk() throws IOException {

    // Arrange
    var packagesRequest = createPackagesRequest().toBuilder()
        .ratePlanCode(PROMOTION_RATE)
        .build();
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.SELL_SEPARATE_PARAM, singletonList("true"));
    params.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList("true"));
    final MultiValueMap<String, String> promoParams = new LinkedMultiValueMap<>();
    promoParams.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList("true"));
    promoParams.put(OhipConstants.PACKAGE_CODE, singletonList(PROMOTION_PACKAGE));
    when(ohipRestaurantsClient.getHotelRestaurants(any()))
        .thenReturn(getRestaurantsFromOhip());
    when(ohipPackagesClient.getPackages(any(), eq(params)))
        .thenReturn(getPackagesFromOhip());
    when(mealResponseOhipMapper.toModel(any(), any())).thenReturn(createMeal());

    // Act
    var response = packagesOutPort.getPackages(packagesRequest);

    // Assert
    assertNotNull(response);

    var packages = response.getPackages();
    assertNotNull(packages);
    assertNotNull(packages.getMeals());
    assertEquals(3, packages.getMeals().size());
  }

  @Test
  void getPackages__noPromoRate__shouldReturnOk() throws IOException {

    // Arrange
    var packagesRequest = createPackagesRequest().toBuilder()
        .ratePlanCode("FLEXRATE")
        .build();
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.SELL_SEPARATE_PARAM, singletonList("true"));
    params.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList("true"));

    when(ohipRestaurantsClient.getHotelRestaurants(any()))
        .thenReturn(getRestaurantsFromOhip());
    when(ohipPackagesClient.getPackages(any(), eq(params)))
        .thenReturn(getPackagesFromOhip());
    when(mealResponseOhipMapper.toModel(any(), any()))
            .thenReturn(createMeal());
    // Act
    var response = packagesOutPort.getPackages(packagesRequest);

    // Assert
    assertNotNull(response);

    var packages = response.getPackages();
    assertNotNull(packages);
    assertNotNull(packages.getMeals());
    assertEquals(3, packages.getMeals().size());
  }

  @Test
  void getCharityPackages__shouldReturnOk() throws IOException {

    // Arrange
    when(ohipPackagesClient.getDonationPackagesDetails(any()))
            .thenReturn(getDonationPackagesFromOhip());

    // Act
    var response = packagesOutPort.getDonationPackagesDetails(createCharityPackagesRequest());

    // Assert
    assertNotNull(response);

    var packages = response.getDonationPackages();
    assertNotNull(packages);

    var firstCharityPackage = packages.get(0);
    assertNotNull(firstCharityPackage);
    assertEquals("CHRTY1", firstCharityPackage.getCode());
    assertEquals("EUR", firstCharityPackage.getCurrency());
    assertEquals(BigDecimal.valueOf(3), firstCharityPackage.getUnitPrice());

  }

  @Test
  void getPackagesWithNoRestaurants__shouldThrowException() throws IOException {

    // Arrange
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put(OhipConstants.SELL_SEPARATE_PARAM, singletonList("true"));
    params.put(OhipConstants.INCLUDE_GROUP_PARAM, singletonList("true"));

    String error = "Error while trying to get packages for hotelId=LONEUS";
    PackagesRequest packagesRequest = createPackagesRequest();
    // Act
    PackagesException exception = Assertions
        .assertThrows(PackagesException.class, () -> {
          packagesOutPort.getPackages(packagesRequest);
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getCharityPackages__shouldThrowException() throws IOException {

    // Arrange
    String error = "An error was returned by OHIP!";
    when(ohipPackagesClient.getDonationPackagesDetails(any()))
        .thenThrow(
            new HotelPackageException(ErrorCode.OHIP_GET_DONATION_PACKAGES_DETAILS_EXCEPTION, error));
    DonationPackagesRequest donationPackagesRequest = new DonationPackagesRequest();
    // Act
    HotelPackageException exception = Assertions
        .assertThrows(HotelPackageException.class, () -> {
          packagesOutPort.getDonationPackagesDetails(donationPackagesRequest);
        });

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getPackagesGroups_shouldHandlePackageCodes() {
    // Arrange
    PackageGroupRequest request = createRequestWithPackageCodesOnly(true);
    when(ohipPackagesClient.getPackageGroups(any()))
        .thenReturn(Mono.just(createPackageGroupsInfoWithGroup()));

    // Act
    PackageGroupsResponse actualResponse = packagesOutPort.getPackagesGroups(request);

    // Assert
    assertNotNull(actualResponse);
    assertEquals("MD2DIN",
        actualResponse.getPackagesGroup().get(0).getPackageCodes().get(0).getPackageCode());
    assertEquals("Meal Deal Dinner",
        actualResponse.getPackagesGroup().get(0).getPackageCodes().get(0).getPackageDescription());
  }

  @Test
  void getPackagesGroups_shouldHandleSinglePackageCode() {
    // Arrange
    PackageGroupRequest request = createRequestWithPackageCodesOnly(false);
    when(ohipPackagesClient.getPackageGroups(any()))
        .thenReturn(Mono.just(createPackageGroupsInfoWithGroup()));

    // Act
    PackageGroupsResponse actualResponse = packagesOutPort.getPackagesGroups(request);

    // Assert
    assertNotNull(actualResponse);
    assertEquals("MD2DIN",
        actualResponse.getPackagesGroup().get(0).getPackageCodes().get(0).getPackageCode());
    assertEquals("Meal Deal Dinner",
        actualResponse.getPackagesGroup().get(0).getPackageCodes().get(0).getPackageDescription());
  }

  @Test
  void getPackagesGroups_shouldReturnMergedResponse() {
    // Arrange
    PackageGroupRequest request = createPackageGroupRequestWithGroups();

    PackageGroupsResponse expectedResponse = PackageGroupsResponse.builder()
        .packagesGroup(List.of(
            PackageGroups.builder()
                .packageGroup("MDP")
                .packageGroupDescription("Meal Deal Package")
                .packageCodes(List.of(
                    PackageCodes.builder()
                        .packageCode("MD2DIN")
                        .packageDescription("Meal Deal & Drink Bundle")
                        .build()
                ))
                .build()
        ))
        .build();

    when(ohipPackagesClient.getPackageGroups(any()))
        .thenReturn(Mono.just(createPackageGroupsInfoWithGroup()));
    // Act
    PackageGroupsResponse response = packagesOutPort.getPackagesGroups(request);

    // Assert
    assertNotNull(response);
    assertEquals(expectedResponse.getPackagesGroup().size(), response.getPackagesGroup().size());
    assertEquals(expectedResponse.getPackagesGroup().get(0).getPackageGroup(),
        response.getPackagesGroup().get(0).getPackageGroup());
    assertEquals(expectedResponse.getPackagesGroup().get(0).getPackageGroupDescription(),
        response.getPackagesGroup().get(0).getPackageGroupDescription());
  }

  private PackageGroupRequest createRequestWithPackageCodesOnly(Boolean includeDbr) {
    var mdpPkgCodes = PackageCodeRequest.builder()
        .packageCodes(Set.of("MD2DIN", "MDBEVA", "MDBFST")).build();
    var packageCodeReq = new HashSet<>(Set.of(mdpPkgCodes));
    if (includeDbr) {
      var dbrPkgCodes = PackageCodeRequest.builder().packageCodes(Set.of("DBBVPR", "DBDNPR"))
          .build();
      packageCodeReq.add(dbrPkgCodes);
    }
    return PackageGroupRequest.builder()
        .hotelId("HEAPTI")
        .packageGroupList(Set.of())
        .packageCodeList(packageCodeReq)
        .build();
  }

  private PackageGroupsInfo createPackageGroupsInfoWithGroup() {
    PackageGroupsInfoPackageGroupList groupList = new PackageGroupsInfoPackageGroupList();

    PackageGroupType packageGroupMDP = new PackageGroupType();
    packageGroupMDP.setCode("MDP");
    packageGroupMDP.setDescription("Meal Deal Package");
    packageGroupMDP.setMembersList(List.of(
        createPackageGroupMember("MD2DIN", "Meal Deal Dinner"),
        createPackageGroupMember("MDBEVA", "Meal Deal Dinner Beverage"),
        createPackageGroupMember("MDBFST", "Meal Deal Breakfast Food")
    ));

    PackageGroupType packageGroupDBR = new PackageGroupType();
    packageGroupDBR.setCode("DBR");
    packageGroupDBR.setDescription("Dinner and Drink Bundle (Room)");
    packageGroupDBR.setMembersList(List.of(
        createPackageGroupMember("DBBVPR", "Dinner Bundle Beverage"),
        createPackageGroupMember("DBDNPR", "Dinner Bundle Food")
    ));

    HotelPackageGroupsType hotelGroup = new HotelPackageGroupsType();
    hotelGroup.setPackageGroup(List.of(packageGroupMDP, packageGroupDBR));
    groupList.setPackageGroups(List.of(hotelGroup));

    PackageGroupsInfo info = new PackageGroupsInfo();
    info.setPackageGroupList(groupList);

    return info;
  }

  private PackageCodeType createPackageGroupMember(String code, String desc) {
    PackageCodeType member = new PackageCodeType();
    member.setCode(code);
    member.setDescription(desc);
    return member;
  }

  private PackageGroupRequest createPackageGroupRequestWithGroups() {
    return PackageGroupRequest.builder()
        .hotelId("HEAPTI")
        .packageGroupList(Set.of("MDP"))
        .build();
  }

  private Mono<PackagesResponseOhipDto> getPackagesFromOhip() throws IOException {
    return Mono.just(PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource(MOCK_PACKAGES_RESPONSE_JSON),
            PackagesInfoPackageCodesList.class))
        .build());
  }

  private RestaurantsResponseOhipDto getRestaurantsFromOhip() throws IOException {
    return RestaurantsResponseOhipDto.builder()
        .hotelConfigInfo(mapper.readValue(PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource(MOCK_RESTAURANTS_RESPONSE_JSON),
            HotelInfoType.class))
        .build();
  }

  private PackagesRequest createPackagesRequest() {
    return PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .children(1)
        .nrNights(2)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build();
  }

  private DonationPackagesRequest createCharityPackagesRequest() {
    return DonationPackagesRequest.builder().hotelId("HOTELCODE")
        .packageCodes(new ArrayList<>(List.of("CHRTY1")))
        .build();
  }

  private PackagesResponseOhipDto getDonationPackagesFromOhip() throws IOException {
    return PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource(MOCK_CHARITY_PACKAGES_RESPONSE_JSON),
            PackagesInfoPackageCodesList.class))
        .build();
  }

  private Meal createMeal() {
    return Meal.builder()
        .id(ID)
        .title(TITLE)
        .currency(SYMBOL)
        .price(PRICE)
        .build();
  }

}