package uk.co.whitbread.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.domain.model.packages.in.PackagesRequest;

class PackageRequestSelfValidationTest {

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> PackagesRequest.builder()
        .adultsNumber(1)
        .childrenNumber(1)
        .nightsNumber(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyAdultsNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "adultsNumber: must be greater than 0";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(-1)
        .childrenNumber(1)
        .nightsNumber(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyChildrenNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "childrenNumber: must not be null";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(1)
        .nightsNumber(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_negativeChildrenNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "childrenNumber: must be greater than or equal to 0";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(1)
        .childrenNumber(-1)
        .nightsNumber(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyNightsNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "nightsNumber: must not be null";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(1)
        .childrenNumber(1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_negativeNightsNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "nightsNumber: must be greater than 0";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(1)
        .childrenNumber(1)
        .nightsNumber(-1)
        .startDate("2022-02-02")
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyStartDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "startDate: must not be empty";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(1)
        .childrenNumber(1)
        .nightsNumber(1)
        .endDate("2022-02-05")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyEndDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "endDate: must not be empty";

    checkErrorThrown(() -> PackagesRequest.builder()
        .hotelId("LONEUS")
        .adultsNumber(1)
        .childrenNumber(1)
        .nightsNumber(1)
        .startDate("2022-02-02")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      PackagesRequest.builder()
          .hotelId("LONEUS")
          .adultsNumber(1)
          .childrenNumber(1)
          .nightsNumber(1)
          .startDate("2022-02-02")
          .endDate("2022-02-05")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
