package uk.co.whitbread.spending.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.SpendingReportFile;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ReportingInPortImplTest {

  @InjectMocks
  private ReportingInPortImpl reportingInPort;

  @Test
  void generateAccountSpendingCsv_ShouldGenerateCorrectFilename() {
    // Arrange
    AccountSpendingResponse response = createAccountSpendingResponse();
    String language = "en";
    String pibaAccountId = "12345";
    Scheme scheme = Scheme.GB;

    // Act
    SpendingReportFile reportFile = reportingInPort.generateAccountSpendingCsv(response, language, pibaAccountId, scheme);

    // Assert
    assertTrue(reportFile.filename().contains(pibaAccountId));
    assertTrue(reportFile.filename().endsWith(".csv"));
  }

  @ParameterizedTest
  @CsvSource({
      "de, DE, '\uFEFFJahr,Monat,TransaktionenGesamt (€)\n'",
      "en, GB, '\uFEFFYear,Month,TransactionsTotal (£)\n'",
      "de, GB, '\uFEFFJahr,Monat,TransaktionenGesamt (£)\n'",
      "en, DE, '\uFEFFYear,Month,TransactionsTotal (€)\n'"
  })
  void generateAccountSpendingCsv_ShouldIncludeCorrectHeaders(String language, Scheme scheme, String expectedHeaders) {
    // Arrange
    AccountSpendingResponse response = createAccountSpendingResponse();
    String pibaAccountId = "12345";

    // Act
    SpendingReportFile reportFile = reportingInPort.generateAccountSpendingCsv(response, language, pibaAccountId, scheme);

    // Assert
    String csvContent = reportFile.content();
    assertEquals(expectedHeaders, csvContent.substring(0, expectedHeaders.length()));
  }

  @ParameterizedTest
  @CsvSource({
      """
          en, GB, '2023,January,"1,000.23"'""",
      """
          en, DE, '2023,January,"1.000,23"'"""
  })
  void generateAccountSpendingCsv_ShouldFormatNumberCorrectlyBasedOnSchema(String language, Scheme scheme, String expectedDataRow) {
      // Arrange
      AccountSpendingResponse response = createAccountSpendingResponse();
      String pibaAccountId = "12345";

      // Act
      SpendingReportFile reportFile = reportingInPort.generateAccountSpendingCsv(response, language, pibaAccountId, scheme);

      // Assert
      String csvContent = reportFile.content();
      String actualDataRow = csvContent.split("\n")[1];
      assertEquals(expectedDataRow, actualDataRow,
          String.format("Mismatch for language=%s, scheme=%s. Expected: '%s', Actual: '%s'",
                        language, scheme, expectedDataRow, actualDataRow));
  }

  @ParameterizedTest
  @CsvSource({
      """
          en, GB, '2023,January,"10"'""",
      """
      en, DE, '2023,January,"10"'""",
      """
          de, GB, '2023,Januar,"10"'""",
      """
          de, DE, '2023,Januar,"10"'"""
  })
  void generateAccountSpendingCsv_ShouldTranslateMonthNameCorrectlyBasedOnLanguage(String language, Scheme scheme, String expectedDataRow) {
    // Arrange
    AccountSpendingResponse response = createAccountSpendingResponse();
    response.getAccountSpendingList().get(0).setBookingValue(BigDecimal.TEN);
    String pibaAccountId = "12345";

    // Act
    SpendingReportFile reportFile = reportingInPort.generateAccountSpendingCsv(response, language, pibaAccountId, scheme);

    // Assert
    String csvContent = reportFile.content();
    String actualDataRow = csvContent.split("\n")[1];
    assertEquals(expectedDataRow, actualDataRow,
        String.format("Mismatch for language=%s, scheme=%s. Expected: '%s', Actual: '%s'",
            language, scheme, expectedDataRow, actualDataRow));
  }

  @Test
  void generateAccountSpendingCsv_ShouldOrderDataRowsDescendingByYearAndMonth() {
    // Arrange
    AccountSpendingResponse response = AccountSpendingResponse.builder()
        .accountSpendingList(List.of(
            AccountSpending.builder().year(2025).month(2).bookingValue(BigDecimal.valueOf(300)).build(),
            AccountSpending.builder().year(2025).month(3).bookingValue(BigDecimal.valueOf(400)).build(),
            AccountSpending.builder().year(2024).month(11).bookingValue(BigDecimal.valueOf(1000)).build(),
            AccountSpending.builder().year(2025).month(1).bookingValue(BigDecimal.valueOf(500)).build(),
            AccountSpending.builder().year(2024).month(12).bookingValue(BigDecimal.valueOf(10400.22)).build()
            ))
        .build();
    String language = "EN";
    String pibaAccountId = "12345";
    Scheme scheme = Scheme.GB;

    // Act
    SpendingReportFile reportFile = reportingInPort.generateAccountSpendingCsv(response, language, pibaAccountId, scheme);

    // Assert
    String expectedCsv = "\uFEFFYear,Month,TransactionsTotal (£)\n"
        .concat("2025,March,\"400\"\n")
        .concat("2025,February,\"300\"\n")
        .concat("2025,January,\"500\"\n")
        .concat("2024,December,\"10,400.22\"\n")
        .concat("2024,November,\"1,000\"\n");
    assertEquals(expectedCsv, reportFile.content());
  }

  private AccountSpendingResponse createAccountSpendingResponse() {
    return AccountSpendingResponse.builder()
        .accountSpendingList(List.of(
            AccountSpending.builder()
                .year(2023)
                .month(1)
                .noOfBookings(10)
                .bookingValue(BigDecimal.valueOf(1000.23))
                .build()
        ))
        .build();
  }

}
