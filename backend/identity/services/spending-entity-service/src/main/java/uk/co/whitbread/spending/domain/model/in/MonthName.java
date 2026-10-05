package uk.co.whitbread.spending.domain.model.in;

public enum MonthName {
  JANUARY(1, "January", "Januar"),
  FEBRUARY(2, "February", "Februar"),
  MARCH(3, "March", "März"),
  APRIL(4, "April", "April"),
  MAY(5, "May", "Mai"),
  JUNE(6, "June", "Juni"),
  JULY(7, "July", "Juli"),
  AUGUST(8, "August", "August"),
  SEPTEMBER(9, "September", "September"),
  OCTOBER(10, "October", "Oktober"),
  NOVEMBER(11, "November", "November"),
  DECEMBER(12, "December", "Dezember");

  private final int monthNumber;
  private final String englishName;
  private final String germanName;

  MonthName(int monthNumber, String englishName, String germanName) {
    this.monthNumber = monthNumber;
    this.englishName = englishName;
    this.germanName = germanName;
  }

  public static String getMonthName(int monthNumber, String language) {
    for (MonthName month : values()) {
      if (month.monthNumber == monthNumber) {
        return "DE".equalsIgnoreCase(language) ? month.germanName : month.englishName;
      }
    }
    throw new IllegalArgumentException("Invalid month number: " + monthNumber);
  }

}
