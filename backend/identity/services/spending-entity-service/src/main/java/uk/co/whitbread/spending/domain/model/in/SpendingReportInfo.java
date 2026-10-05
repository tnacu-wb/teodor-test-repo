package uk.co.whitbread.spending.domain.model.in;

import lombok.Getter;

@Getter
public enum SpendingReportInfo {
  UK("Year", "Month", "TransactionsTotal", "Inn Business Pay Spend Over Time for "),
  DE("Jahr", "Monat", "TransaktionenGesamt", "Inn Business Pay Ausgabenverlauf für ");

  private final String yearHeader;
  private final String monthHeader;
  private final String bookingValueHeader;
  private final String filename;

  SpendingReportInfo(String yearHeader, String monthHeader, String bookingValueHeader, String filename) {
    this.yearHeader = yearHeader;
    this.monthHeader = monthHeader;
    this.bookingValueHeader = bookingValueHeader;
    this.filename = filename;
  }

  public static SpendingReportInfo fromLanguage(String language) {
    if (language != null && language.equalsIgnoreCase("DE")) {
      return DE;
    }
    return UK;
  }
}
