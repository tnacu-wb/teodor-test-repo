package uk.co.whitbread.ohip.domain.model.packages.in;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.ohip.domain.model.reservation.in.BaseValidation;

class PackagesRequestTest extends BaseValidation {

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> PackagesRequest.builder()
        .adults(1)
        .children(1)
        .nrNights(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyAdults_shouldSelfValidateAndThrow() {
    String expectedMessage = "adults: must be greater than 0";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(-1)
        .children(1)
        .nrNights(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyChildren_shouldSelfValidateAndThrow() {
    String expectedMessage = "children: must not be null";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .nrNights(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_negativeChildren_shouldSelfValidateAndThrow() {
    String expectedMessage = "children: must be greater than or equal to 0";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .children(-1)
        .nrNights(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyNrNights_shouldSelfValidateAndThrow() {
    String expectedMessage = "nrNights: must not be null";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .children(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_negativeNrNights_shouldSelfValidateAndThrow() {
    String expectedMessage = "nrNights: must be greater than 0";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .children(1)
        .nrNights(-1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyStartDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "startDate: must not be empty";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .children(1)
        .nrNights(1)
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyEndDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "endDate: must not be empty";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adults(1)
        .children(1)
        .nrNights(1)
        .startDate("2022-02-02")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      PackagesRequest.builder()
          .hotelId("LONEUS")
          .adults(1)
          .children(1)
          .nrNights(1)
          .startDate("2022-02-02")
          .endDate("2022-02-05")
          .build();
    });
  }

}