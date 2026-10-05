package uk.co.whitbread.basket.domain.model.payments.in;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDate;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class BookingTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> Booking.builder()
        .arrivalDate("2022-08-01")
        .businessSite(BusinessSite.builder()
            .identifier("identifier")
            .location("location")
            .name("name")
            .type("type")
            .build())
        .channel("channel")
        .departureDate("2022-08-04")
        .journey("journey")
        .language("en")
        .leadGuest(Guest.builder()
            .name("Guest")
            .previousBookings(3)
            .registered(true)
            .registeredSince(LocalDate.of(2022,1,1))
            .build())
        .reference("reference")
        .rooms(of(RoomType.builder()
            .adultsNumber(2)
            .rate("rate")
            .type("type")
            .build()))
        .type("type")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"businessSite: must not be null",
        "channel: must not be empty",
        "journey: must not be empty",
        "type: must not be empty"
    };

    TestUtils.checkErrorThrown(() -> Booking.builder().build(), errors);

  }

}
