package uk.co.whitbread.infrastructure.rest.client.packages;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.infrastructure.rest.client.packages.exceptions.PackagesException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(consumerName = "availability", stubsPerConsumer = true,
    stubsMode = StubRunnerProperties.StubsMode.LOCAL,
    ids = "uk.co.whitbread:ohip-adapter-service:+:stubs:8080")
@Disabled
class PackagesOhipClientContractTest {

  private static final String START_DATE = "2022-04-02";
  private static final String END_DATE = "2022-04-03";
  public static final int ADULTS = 2;
  public static final int NR_NIGHTS = 2;
  public static final int CHILDREN = 0;
  public static final String TITLE = "Premier Inn Breakfast";
  public static final String ID = "PIBTEST";
  public static final String CURRENCY = "GBP";
  public static final BigDecimal PRICE = BigDecimal.valueOf(9.5);
  public static final String EXPECTED_MESSAGE = "An error was returned by OHIP Adapter !";

  @Autowired
  PackagesOutPortImpl packagesOutPort;

  @Test
  void getPackagesOhip__ShouldReturnOK() {
    //Arrange

    //Act
    PackagesResponse packagesResponse = packagesOutPort.getPackages(
        createValidGetPackagesRequest());

    var restaurant = packagesResponse.getRestaurant();
    assertThat(restaurant, notNullValue());

    var meal = packagesResponse.getPackages().getMeals().get(0);
    assertThat(meal.getName(), is(TITLE));
    assertThat(meal.getId(), is(ID));
    assertThat(meal.getPrice(), is(PRICE));
    assertThat(meal.getCurrency(), is(CURRENCY));

  }

  @Test
  void getPackages__NonExistentHotel__InternalServerError() {
    //Arrange
    PackagesRequest packagesRequest = createNonExistentHotel();

    //Act
    Exception exception = assertThrows(PackagesException.class, () ->
        this.packagesOutPort.getPackages(packagesRequest));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(EXPECTED_MESSAGE));
  }

  @Test
  void getPackagesOhip_invalidDate_BadRequest() {
    //Arrange
    var packagesRequest = createInvalidDateRequest();

    //Act
    Exception exception = assertThrows(PackagesException.class, () -> {
      this.packagesOutPort.getPackages(packagesRequest);
    });

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(EXPECTED_MESSAGE));
  }

  @Test
  void getPackages__EmptyParams__BadRequest() {
    //Arrange
    PackagesRequest packagesRequest = createEmptyParamsRequest();
    //Act
    Exception exception = assertThrows(PackagesException.class, () ->
        this.packagesOutPort.getPackages(packagesRequest));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(EXPECTED_MESSAGE));
  }

  private PackagesRequest createNonExistentHotel() {
    return PackagesRequest
        .builder()
        .hotelId("HOTELCODE3")
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .build();
  }

  private PackagesRequest createValidGetPackagesRequest() {
    return PackagesRequest
        .builder()
        .hotelId("HOTELCODE4")
        .startDate(START_DATE)
        .endDate(END_DATE)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .nightsNumber(NR_NIGHTS)
        .build();
  }

  private PackagesRequest createEmptyParamsRequest() {
    return PackagesRequest
        .builder()
        .hotelId("HOTELCODE1")
        .startDate("")
        .endDate("")
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .build();
  }

  private PackagesRequest createInvalidDateRequest() {
    return PackagesRequest
        .builder()
        .hotelId("HOTELCODE2")
        .startDate("invalid")
        .endDate(END_DATE)
        .adultsNumber(ADULTS)
        .childrenNumber(CHILDREN)
        .build();
  }
}
