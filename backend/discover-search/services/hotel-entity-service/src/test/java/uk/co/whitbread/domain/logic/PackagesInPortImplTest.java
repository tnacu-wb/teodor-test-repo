package uk.co.whitbread.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.packages.in.DonationPackagesRequest;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
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
import uk.co.whitbread.domain.ports.secondary.PackagesOutPort;

@ExtendWith(MockitoExtension.class)
class PackagesInPortImplTest {

  private static final String HOTEL_ID = "TKINPT";
  private static final String START_DATE = "2022-03-01";
  private static final String END_DATE = "2022-03-03";
  public static final int ADULTS = 2;
  public static final int CHILDREN = 0;

  public static final int NIGHTS = 1;
  public static final String TITLE = "Premier Inn Breakfast";
  public static final String ID = "BFADBF";
  public static final String CURRENCY = "EUR";
  public static final BigDecimal PRICE = BigDecimal.valueOf(10L);

  @InjectMocks
  private PackagesInPortImpl packagesInPortImpl;

  @Mock
  private PackagesOutPort packagesOutPort;

  @Test
  void getHotelAvailability__ShouldReturnOk() {
    //Arrange
    var response = buildPackagesResponse();
    var request = buildPackagesRequest();

    when(this.packagesOutPort.getPackages(
        any(PackagesRequest.class))).thenReturn(response);

    //Act
    final var packagesResponse = packagesInPortImpl.getPackages(request);

    //Assert
    assertThat(packagesResponse, notNullValue());

    var restaurant = packagesResponse.getRestaurant();
    assertThat(restaurant, notNullValue());

    var meal = packagesResponse.getPackages().getMeals().get(0);
    MatcherAssert.assertThat(meal.getName(), is(TITLE));
    MatcherAssert.assertThat(meal.getId(), is(ID));
    MatcherAssert.assertThat(meal.getPrice(), is(PRICE));
    MatcherAssert.assertThat(meal.getCurrency(), is(CURRENCY));

    assertFalse(packagesResponse.getHotelHasCityTaxForBusiness());
    assertTrue(packagesResponse.getHotelHasCityTaxForLeisure());
  }

  private PackagesRequest buildPackagesRequest() {
    return PackagesRequest
        .builder()
        .hotelId(HOTEL_ID)
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .nightsNumber(NIGHTS)
        .build();
  }

  private PackagesResponse buildPackagesResponse() {

    var restaurant = Restaurant.builder()
        .noMealsFound(false)
        .build();

    var meal = Meal.builder()
        .name(TITLE)
        .id(ID)
        .price(PRICE)
        .currency(CURRENCY)
        .build();

    var packages = Packages.builder()
        .meals(Collections.singletonList(meal))
        .build();

    return PackagesResponse.builder()
        .restaurant(restaurant)
        .packages(packages)
        .hotelHasCityTaxForLeisure(Boolean.TRUE)
        .hotelHasCityTaxForBusiness(Boolean.FALSE)
        .build();
  }

  @Test
  void getDonationPackageDetails__ShouldReturnOk() {
    //Arrange
    var request = buildDonationPackagesRequest();
    var response = buildDonationPackagesResponse();

    when(this.packagesOutPort.getDonationPackageDetails(
        any(DonationPackagesRequest.class))).thenReturn(response);

    //Act
    final var donationPackagesResponse = packagesInPortImpl.getDonationPackageDetails(request);

    //Assert
    assertThat(donationPackagesResponse, notNullValue());
    assertThat(donationPackagesResponse.getDonationPackages().get(0).getCode(), is("CHRTY3"));
    assertThat(donationPackagesResponse.getDonationPackages().get(1).getCurrency(), is("GBP"));
    assertThat(donationPackagesResponse.getDonationPackages().get(2).getUnitPrice(),
        is(BigDecimal.ONE));
  }

  @Test
  void getUpsellItemsAndSoftBundles__ShouldReturnOk() {
    //Arrange
    when(this.packagesOutPort.getUpsellItemsAndSoftBundles(any(), any(), any())).thenReturn(
        buildMealsInfoResponse());

    //Act
    final var mealsInfoResponse = packagesInPortImpl.getUpsellItemsAndSoftBundles(HOTEL_ID, "gb",
        "en");

    //Assert
    assertThat(mealsInfoResponse, notNullValue());
    assertThat(mealsInfoResponse.getSoftBundles().size(), is(1));
    assertThat(mealsInfoResponse.getSoftBundles().stream()
        .flatMap(sb -> sb.getPackageCodes().stream())
        .map(PackageCode::getId)
        .toList(), contains("BFADBF"));
  }

  private MealsInfoResponse buildMealsInfoResponse() {
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

  private DonationPackagesRequest buildDonationPackagesRequest() {
    return DonationPackagesRequest
        .builder()
        .hotelId(HOTEL_ID)
        .packageCodes(List.of("CHRTY3", "CHRTY4", "CHRTY5"))
        .build();
  }

  private DonationPackagesResponse buildDonationPackagesResponse() {

    return DonationPackagesResponse.builder()
        .donationPackages(List.of(
            buildDonationPackageDetails("CHRTY3", BigDecimal.valueOf(5), "GBP"),
            buildDonationPackageDetails("CHRTY4", BigDecimal.valueOf(3), "GBP"),
            buildDonationPackageDetails("CHRTY5", BigDecimal.valueOf(1), "GBP")
        ))
        .build();
  }

  private DonationPackage buildDonationPackageDetails(String code, BigDecimal unitPrice,
      String currency) {
    return DonationPackage.builder().code(code).unitPrice(unitPrice).currency(currency).build();
  }
}