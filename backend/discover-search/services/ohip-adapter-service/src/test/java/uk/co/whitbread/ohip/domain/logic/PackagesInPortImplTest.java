package uk.co.whitbread.ohip.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackageGroupRequest;
import uk.co.whitbread.ohip.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.ohip.domain.model.packages.out.*;
import uk.co.whitbread.ohip.domain.ports.secondary.PackagesOutPort;

@ExtendWith(MockitoExtension.class)
class PackagesInPortImplTest {

  public static final String HOTEL_ID = "LONEUS";
  public static final String START_DATE = "2022-04-07";
  public static final String END_DATE = "2022-04-10";
  public static final int NR_NIGHTS = 2;
  public static final int ADULTS = 2;
  public static final int CHILDREN = 0;
  public static final String TITLE = "Premier Inn Breakfast";
  public static final String ID = "BFADBF";
  public static final String CURRENCY = "EUR";
  public static final BigDecimal PRICE = BigDecimal.valueOf(10L);

  private PackagesInPortImpl packagesInPort;

  @Mock
  private PackagesOutPort packagesOutPort;

  @BeforeEach
  public void init() {
    packagesInPort = new PackagesInPortImpl(packagesOutPort);
  }

  @Test
  void getPackages__ShouldReturnOk() {

    // Arrange
    PackagesRequest request = createPackagesRequest();

    PackagesResponse response = createPackagesResponse();

    when(packagesOutPort.getPackages(request)).thenReturn(response);

    // Act
    PackagesResponse realResponse = this.packagesInPort.getPackages(request);

    // Assert
    assertThat(realResponse, notNullValue());

    List<Meal> meals = realResponse.getPackages().getMeals();
    Restaurant restaurant = realResponse.getRestaurant();

    assertThat(restaurant, notNullValue());

    assertThat(meals, notNullValue());
    assertThat(meals.size(), is(1));

    Meal meal = meals.get(0);

    assertThat(meal.getId(), is(ID));
    assertThat(meal.getCurrency(), is(CURRENCY));
    assertThat(meal.getPrice(), is(PRICE));
    assertThat(meal.getTitle(), is(TITLE));

    assertThat(restaurant.getRestaurantNotFound(), is(false));
    assertThat(restaurant.getNoMealsFound(), is(false));

  }

  @Test
  void getDonationPackages__ShouldReturnOk() {

    // Arrange
    DonationPackagesRequest request = createCharityPackagesRequest();

    DonationPackagesResponse response = createCharityPackagesResponse();

    when(packagesOutPort.getDonationPackagesDetails(request)).thenReturn(response);

    // Act
    DonationPackagesResponse realResponse = packagesInPort.getDonationPackagesDetails(request);

    // Assert
    assertThat(realResponse, notNullValue());

    var firstCharityPackage = realResponse.getDonationPackages().get(0);
    assertNotNull(firstCharityPackage);
    assertEquals("CHRTY1", firstCharityPackage.getCode());
    assertEquals("EUR", firstCharityPackage.getCurrency());
    assertEquals(BigDecimal.valueOf(1), firstCharityPackage.getUnitPrice());

    var secondCharityPackage = realResponse.getDonationPackages().get(1);
    assertNotNull(secondCharityPackage);
    assertEquals("CHRTY2", secondCharityPackage.getCode());
    assertEquals("EUR", secondCharityPackage.getCurrency());
    assertEquals(BigDecimal.valueOf(10), secondCharityPackage.getUnitPrice());
  }

  @Test
  void getPackagesEmptyMeals__ShouldReturnOk() {

    // Arrange
    PackagesRequest request = createPackagesRequest();

    PackagesResponse response = createPackagesResponseEmptyMeals();

    when(packagesOutPort.getPackages(request)).thenReturn(response);

    // Act
    PackagesResponse realResponse = this.packagesInPort.getPackages(request);

    // Assert
    assertThat(realResponse, notNullValue());
    assertThat(realResponse.getPackages(), notNullValue());
    assertThat(realResponse.getPackages().getMeals().size(), is(0));

  }

  @Test
  void getPackagesNoRestaurants__ShouldReturnOk() {

    // Arrange
    PackagesRequest request = createPackagesRequest();

    PackagesResponse response = createPackageResponseNoRestaurants();

    when(packagesOutPort.getPackages(request)).thenReturn(response);

    //Act
    PackagesResponse realResponse = this.packagesInPort.getPackages(request);

    // Assert
    assertThat(realResponse, notNullValue());
    assertThat(realResponse.getPackages(), notNullValue());
    assertThat(realResponse.getRestaurant().getRestaurantNotFound(), is(true));

  }

  @Test
  void getPackagesGroups_ShouldReturnOk() {
    // Arrange
    PackageGroupRequest request = createPackageGroupRequest();
    PackageGroupsResponse expectedResponse = createPackageGroupsResponse();

    when(packagesOutPort.getPackagesGroups(request)).thenReturn(expectedResponse);

    // Act
    PackageGroupsResponse actualResponse = packagesInPort.getPackagesGroups(request);

    // Assert
    assertThat(actualResponse, notNullValue());

    assertThat(actualResponse.getPackagesGroup(), notNullValue());
    assertThat(actualResponse.getPackagesGroup().size(),
        is(expectedResponse.getPackagesGroup().size()));

  }

  private PackageGroupRequest createPackageGroupRequest() {
    return PackageGroupRequest.builder()
        .hotelId("HEAPTI")
        .build();
  }

  private PackageGroupsResponse createPackageGroupsResponse() {
    PackageGroups group1 = PackageGroups.builder()
        .packageGroup("MDP")
        .packageGroupDescription("Meal Deal")
        .packageCodes(List.of(
            PackageCodes.builder()
                .packageCode("MD2DIN")
                .packageDescription("Meal Deal Dinner")
                .build(),
            PackageCodes.builder()
                .packageCode("MDBEVA")
                .packageDescription("Meal Deal Dinner Beverage")
                .build(),
            PackageCodes.builder()
                .packageCode("MDBFST")
                .packageDescription("Meal Deal Breakfast Food")
                .build()
        )).build();

    PackageGroups group2 = PackageGroups.builder()
        .packageGroup("DBR")
        .packageGroupDescription("Dinner and Drink Bundle")
        .packageCodes(List.of(
            PackageCodes.builder()
                .packageCode("DBBVPR")
                .packageDescription("Dinner Bundle Beverage")
                .build(),
            PackageCodes.builder()
                .packageCode("DBDNPR")
                .packageDescription("Dinner Bundle Food")
                .build()
        ))
        .build();
    return PackageGroupsResponse.builder()
        .packagesGroup(List.of(group1, group2))
        .build();
  }

  private PackagesRequest createPackagesRequest() {
    return PackagesRequest.builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adults(ADULTS)
        .children(CHILDREN)
        .nrNights(NR_NIGHTS)
        .build();
  }

  private DonationPackagesRequest createCharityPackagesRequest() {
    return DonationPackagesRequest.builder()
        .hotelId(HOTEL_ID)
        .packageCodes(new ArrayList<>(Arrays.asList("CHRTY1", "CHRTY2")))
        .build();
  }

  private DonationPackagesResponse createCharityPackagesResponse() {
    var firstCharityPackage = DonationPackage.builder().code("CHRTY1").unitPrice(BigDecimal.ONE).currency("EUR")
        .build();
    var secondCharityPackage = DonationPackage.builder().code("CHRTY2").unitPrice(BigDecimal.TEN).currency("EUR")
        .build();
    return DonationPackagesResponse.builder()
        .donationPackages(new ArrayList<>(Arrays.asList(firstCharityPackage, secondCharityPackage))).build();
  }

  private PackagesResponse createPackagesResponse() {
    Restaurant restaurant = Restaurant.builder()
        .restaurantNotFound(false)
        .noMealsFound(false)
        .build();
    Packages packages = Packages.builder()
        .meals(Collections.singletonList(Meal.builder()
            .title(TITLE)
            .id(ID)
            .currency(CURRENCY)
            .price(PRICE)
            .build()))
        .build();

    return PackagesResponse.builder()
        .restaurant(restaurant)
        .packages(packages)
        .build();

  }

  private PackagesResponse createPackagesResponseEmptyMeals() {
    Restaurant restaurant = Restaurant.builder()
        .noMealsFound(true)
        .build();
    Packages packages = Packages.builder()
        .meals(Collections.emptyList())
        .build();

    return PackagesResponse.builder()
        .restaurant(restaurant)
        .packages(packages)
        .build();
  }

  private PackagesResponse createPackageResponseNoRestaurants() {
    Restaurant restaurant = Restaurant.builder()
        .restaurantNotFound(true)
        .noMealsFound(false)
        .build();
    Packages packages = Packages.builder()
        .meals(Collections.singletonList(Meal.builder()
            .title(TITLE)
            .id(ID)
            .currency(CURRENCY)
            .price(PRICE)
            .build()))
        .build();

    return PackagesResponse.builder()
        .restaurant(restaurant)
        .packages(packages)
        .build();

  }

}