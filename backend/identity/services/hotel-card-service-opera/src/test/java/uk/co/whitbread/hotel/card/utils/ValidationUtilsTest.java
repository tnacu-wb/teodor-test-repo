package uk.co.whitbread.hotel.card.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidationUtilsTest {

  public static final String COMPANY_ID_FROM_TOKEN = "companyId";
  public static final String COMPANY_ID_FROM_REQUEST = "companyId";

  @Test
  void isSameCompany_ShouldReturnTrue() {

    // Act
    boolean result = ValidationUtils.isSameCompany(COMPANY_ID_FROM_TOKEN, COMPANY_ID_FROM_REQUEST);

    // Assert
    assertTrue(result);
  }

  @Test
  void isSameCompany_WithDifferentCompanyId_ShouldReturnFalse() {

    // Act
    boolean result = ValidationUtils.isSameCompany("dummy", COMPANY_ID_FROM_REQUEST);

    // Assert
    assertFalse(result);
  }

}