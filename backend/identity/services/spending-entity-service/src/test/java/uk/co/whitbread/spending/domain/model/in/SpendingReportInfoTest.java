package uk.co.whitbread.spending.domain.model.in;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpendingReportInfoTest {

  @Test
  void fromLanguage_ShouldReturnCorrectHeadersForEnglish() {
    SpendingReportInfo info = SpendingReportInfo.fromLanguage("EN");
    assertEquals("Year", info.getYearHeader());
    assertEquals("Month", info.getMonthHeader());
    assertEquals("TransactionsTotal", info.getBookingValueHeader());
  }

  @Test
  void fromLanguage_ShouldReturnCorrectHeadersForGerman() {
    SpendingReportInfo info = SpendingReportInfo.fromLanguage("DE");
    assertEquals("Jahr", info.getYearHeader());
    assertEquals("Monat", info.getMonthHeader());
    assertEquals("TransaktionenGesamt", info.getBookingValueHeader());
  }

  @Test
  void fromLanguage_ShouldDefaultToEnglishForUnsupportedLanguage() {
    SpendingReportInfo info = SpendingReportInfo.fromLanguage("FR");
    assertEquals("Year", info.getYearHeader());
    assertEquals("Month", info.getMonthHeader());
    assertEquals("TransactionsTotal", info.getBookingValueHeader());
  }

  @Test
  void fromLanguage_ShouldGenerateCorrectFilename() {
    SpendingReportInfo info = SpendingReportInfo.fromLanguage("EN");
    assertTrue(info.getFilename().contains("Inn Business Pay Spend Over Time for "));
  }

  @Test
  void fromLanguage_ShouldGenerateCorrectFilenameForGerman() {
    SpendingReportInfo info = SpendingReportInfo.fromLanguage("DE");
    assertTrue(info.getFilename().contains("Inn Business Pay Ausgabenverlauf für "));
  }

}