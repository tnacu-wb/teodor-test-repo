package uk.co.whitbread.spending.domain.model.in;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonthNameTest {

  @Test
  void getMonthName_ShouldReturnCorrectEnglishName() {
    assertEquals("January", MonthName.getMonthName(1, "EN"));
    assertEquals("December", MonthName.getMonthName(12, "EN"));
  }

  @Test
  void getMonthName_ShouldReturnCorrectGermanName() {
    assertEquals("Januar", MonthName.getMonthName(1, "DE"));
    assertEquals("Dezember", MonthName.getMonthName(12, "DE"));
  }

  @Test
  void getMonthName_ShouldThrowExceptionForInvalidMonthNumber() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
        MonthName.getMonthName(13, "EN")
    );
    assertEquals("Invalid month number: 13", exception.getMessage());
  }

  @Test
  void getMonthName_ShouldDefaultToEnglishForUnknownLanguage() {
    assertEquals("January", MonthName.getMonthName(1, "FR"));
  }
}