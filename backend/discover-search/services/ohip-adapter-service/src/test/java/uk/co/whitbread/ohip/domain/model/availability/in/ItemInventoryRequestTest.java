package uk.co.whitbread.ohip.domain.model.availability.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.ohip.domain.model.reservation.in.BaseValidation;

class ItemInventoryRequestTest extends BaseValidation {

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      ItemInventoryRequest.builder()
          .hotelId("LONEUS")
          .startDate("2022-12-23")
          .endDate("2022-12-26")
          .build();
    });
  }

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> ItemInventoryRequest.builder()
        .startDate("2015-10-20")
        .endDate("2015-10-22")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyStartDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "startDate: must not be empty";

    checkErrorThrown(() -> ItemInventoryRequest.builder()
        .hotelId("LONEUS")
        .endDate("2015-10-22")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyEndDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "endDate: must not be empty";

    checkErrorThrown(() -> ItemInventoryRequest.builder()
        .startDate("2015-10-20")
        .hotelId("LONEUS")
        .build(), expectedMessage);
  }

}