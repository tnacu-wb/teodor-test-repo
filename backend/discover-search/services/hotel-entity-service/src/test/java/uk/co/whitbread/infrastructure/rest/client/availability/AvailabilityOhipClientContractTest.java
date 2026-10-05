package uk.co.whitbread.infrastructure.rest.client.availability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.infrastructure.rest.client.availability.exception.HotelAvailabilityException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(consumerName = "availability", stubsPerConsumer = true,
    stubsMode = StubRunnerProperties.StubsMode.LOCAL,
    ids = "uk.co.whitbread:ohip-adapter-service:+:stubs:8080")
@Disabled
class AvailabilityOhipClientContractTest {

  @Autowired
  HotelAvailabilityOutPortImpl hotelAvailabilityOutPort;

  private final String EXPECTED_MESSAGE = "Error while trying to get hotel availability!";

  @Test
  void getHotelAvailabilityOhip__ShouldReturnOK() {
    //Arrange

    //Act
    HotelAvailability hotelAvailability = hotelAvailabilityOutPort.getHotelAvailability(
        createTestDataSuccess());

    //Assert
    assertThat(hotelAvailability, notNullValue());
    assertThat(hotelAvailability.getHotelId(), is("TKINPT"));
    assertThat(hotelAvailability.getStartDate(), is("2022-03-01"));
    assertThat(hotelAvailability.getEndDate(), is("2022-03-02"));
    assertThat(hotelAvailability.getRoomRates(), notNullValue());
    assertThat(hotelAvailability.getRoomRates(), hasSize(1));

    assertThat(hotelAvailability.getRoomRates().get(0), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRatePlanCode(), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRatePlanCode(), is("FLEXRATE"));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes(), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes(), hasSize(greaterThan(0)));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0).getRoomType(),
        is("DB"));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getSpecialRequests().get(0), is("SING"));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown(), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getCurrencyCode(), is("GBP"));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getTotalNetAmount(), is(new BigDecimal("230")));

    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices(), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices(), hasSize(2));

    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices().get(0), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices().get(0).getNetPrice(),
        is(new BigDecimal("115")));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices().get(0).getDate(),
        is("2022-03-01"));

    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
        .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices().get(1), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices().get(1).getNetPrice(),
        is(new BigDecimal("115")));
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown().getDailyPrices().get(1).getDate(),
        is("2022-03-02"));

  }

  @Test
  void getHotelAvailability__NonExistentHotel__InternalServerError() {

    //Arrange
    HotelAvailabilityRequest request = createTestDataNonExistentHotel();

    //Act
    Exception exception = assertThrows(HotelAvailabilityException.class, () ->
        this.hotelAvailabilityOutPort.getHotelAvailability(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(EXPECTED_MESSAGE));
  }

  @Test
  void getHotelAvailability__NotFoundRoomType__ShouldReturnOK() {
    //Arrange

    //Act
    HotelAvailability hotelAvailability =
        this.hotelAvailabilityOutPort.getHotelAvailability(createTestDataNonExistentRoomType());
    //Assert
    assertThat(hotelAvailability, notNullValue());
    assertThat(hotelAvailability.getHotelId(), is("TKINPT"));
    assertThat(hotelAvailability.getStartDate(), is(""));
    assertThat(hotelAvailability.getEndDate(), is(""));
    assertThat(hotelAvailability.getRoomRates(), notNullValue());
    assertThat(hotelAvailability.getRoomRates(), hasSize(1));

    assertThat(hotelAvailability.getRoomRates().get(0), notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes(),
        notNullValue());
    assertThat(hotelAvailability.getRoomRates().get(0).getRoomTypes(),
        hasSize(1));
  }

  private HotelAvailabilityRequest createTestDataSuccess() {
    return HotelAvailabilityRequest.builder()
        .hotelId("TKINPT")
        .arrivalDate("2022-03-01")
        .departureDate("2022-03-03")
        .roomTypes(Arrays.asList("DB", "DB"))
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .cotsRequired(List.of(false, false))
        .channel("PI")
        .subchannel("MOBILE")
        .language("EN")
        .build();
  }

  private HotelAvailabilityRequest createTestDataNonExistentHotel() {
    return HotelAvailabilityRequest.builder()
        .hotelId("WRONG")
        .arrivalDate("2022-03-01")
        .departureDate("2022-03-03")
        .roomTypes(List.of("DB"))
        .adultsNumber(List.of(1))
        .childrenNumber(List.of(0))
        .cotsRequired(List.of(false))
        .channel("PI")
        .subchannel("MOBILE")
        .language("EN")
        .build();
  }

  private HotelAvailabilityRequest createTestDataNonExistentRoomType() {
    return HotelAvailabilityRequest.builder()
        .hotelId("TKINPT")
        .arrivalDate("2022-03-01")
        .departureDate("2022-03-03")
        .roomTypes(Arrays.asList("DB", "TST"))
        .adultsNumber(Arrays.asList(1, 2))
        .childrenNumber(Arrays.asList(0, 0))
        .cotsRequired(List.of(false, false))
        .channel("PI")
        .subchannel("MOBILE")
        .language("EN")
        .build();
  }
}
