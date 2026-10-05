package uk.co.whitbread.wallet.infrastructure.rest.client.reservations;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.Utils;

class UtilsTest {

  @ParameterizedTest
  @ValueSource(strings = {"a", "A", "abc", "Abc", " john", "1abc", "!abc"})
  void testCapitalizeFirstLetter_variousCases(String input) {
    String expected;
    if (input == null) {
      expected = null;
    } else if (input.isEmpty()) {
      expected = "";
    } else {
      expected = input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    assertEquals(expected, Utils.capitalizeFirstLetter(input));
  }

  @Test
  void testCapitalizeFirstLetter_nullAndEmpty() {
    assertEquals(null, Utils.capitalizeFirstLetter(null));
    assertEquals("", Utils.capitalizeFirstLetter(""));
  }
}