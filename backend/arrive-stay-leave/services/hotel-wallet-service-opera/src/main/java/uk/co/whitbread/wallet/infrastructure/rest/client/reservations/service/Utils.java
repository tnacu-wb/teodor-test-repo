package uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service;


public class Utils {


  private Utils() {
  }

  public static String capitalizeFirstLetter(String value) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    return value.substring(0, 1).toUpperCase() + value.substring(1);
  }
}
