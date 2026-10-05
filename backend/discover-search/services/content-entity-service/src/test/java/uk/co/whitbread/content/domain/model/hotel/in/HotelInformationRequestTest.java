package uk.co.whitbread.content.domain.model.hotel.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.content.utils.TestUtils;

class HotelInformationRequestTest {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String HOTEL_ID_EDIPAR = "EDIPAR";
  private static final String SLUG_LONDON_KINGS_CROSS = "england/greater-london/london/hub-london-kings-cross";

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> HotelInformationRequest.builder()
        .hotelId(HOTEL_ID_EDIPAR)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build());
  }

  @Test
  void verifyMandatoryFieldsWithSlug() {
    assertDoesNotThrow(() -> HotelInformationRequest.builder()
        .slug(SLUG_LONDON_KINGS_CROSS)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"country: must not be empty",
        "Must specify at least one of the following: `hotelId` or `slug`, but not both.",
        "language: must not be empty"};

    TestUtils.checkErrorThrown(() -> HotelInformationRequest.builder().build(), errors);

  }

  @Test
  void verifyErrorMessageWhenBothHotelIdAndSlugAreSet() {
    String[] errors = {
        "Must specify at least one of the following: `hotelId` or `slug`, but not both."};

    TestUtils.checkErrorThrown(() -> HotelInformationRequest.builder()
        .hotelId(HOTEL_ID_EDIPAR)
        .slug(SLUG_LONDON_KINGS_CROSS)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build(), errors);
  }

}
