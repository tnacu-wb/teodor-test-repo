package uk.co.whitbread.hotel.card.utils;

import org.bouncycastle.util.Objects;

public class ValidationUtils {

  private ValidationUtils() {
  }

  public static boolean isSameCompany(String companyIdFromToken, String companyIdFromRequest) {
    return Objects.areEqual(companyIdFromToken, companyIdFromRequest);
  }
}