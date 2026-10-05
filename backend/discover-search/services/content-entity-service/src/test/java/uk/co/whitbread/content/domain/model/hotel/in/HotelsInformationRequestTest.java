package uk.co.whitbread.content.domain.model.hotel.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.content.utils.TestUtils;

class HotelsInformationRequestTest {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String HOTEL_ID_EDIPAR = "EDIPAR";
  private static final String HOTEL_ID_LONEUS = "LONEUS";
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> HotelsInformationRequest.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSetAndBadHotelId() {

    String[] errors = {"country: must not be empty",
        "All `hotelId` in the list must be non empty.",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(
        () -> HotelsInformationRequest.builder().hotelIds(List.of("")).build(), errors);
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"country: must not be empty",
        "hotelIds: must not be empty",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(() -> HotelsInformationRequest.builder().hotelIds(List.of()).build(),
        errors);
  }
}
