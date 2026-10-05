package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.Test;

class HotelsInformationRequestDtoTest {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  private static final String HOTEL_ID_EDIPAR = "EDIPAR";
  private static final String HOTEL_ID_LONEUS = "LONEUS";

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> HotelsInformationRequestDto.builder()
        .hotelIds(List.of(HOTEL_ID_EDIPAR, HOTEL_ID_LONEUS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build());
  }
}
