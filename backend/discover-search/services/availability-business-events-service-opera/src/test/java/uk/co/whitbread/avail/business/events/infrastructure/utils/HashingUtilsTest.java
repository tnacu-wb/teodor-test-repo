package uk.co.whitbread.avail.business.events.infrastructure.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class HashingUtilsTest {

  private static final String API_KEY = "399c82b8-50fe-4cc1-ae82-4b7aa470bf58";
  private static final String EXPECTED_HASHED_API_KEY =
      "3cc6054463d01e4dfd845f1f0afdd37128ea9368192de89c48c6b381ce06980e";

  private static final String EXPECTED_HASHED_API_KEY_WRONG =
      "3cc6054463d01e4dfd845f1f0afdd37128ea9368192de89c48c6b381ce07991e";


  @Test
  public void getSha256HashPositiveTest() {
    final String actualHashedApiKey = HashingUtils.getSha256Hash(API_KEY);
    assertEquals(EXPECTED_HASHED_API_KEY, actualHashedApiKey);
  }

  @Test
  public void getSha256HashExpectedAndAcutalKeyShouldNotMatch() {
    final String actualHashedApiKey = HashingUtils.getSha256Hash(API_KEY);
    assertNotEquals(EXPECTED_HASHED_API_KEY_WRONG, actualHashedApiKey);
  }

  @Test
  public void maskStringExceptLast4Test() {
    final String actualHashedApiKey = HashingUtils.getSha256Hash(API_KEY);
    String maskedString = HashingUtils.maskStringExceptLast4(actualHashedApiKey);

    String maskedStringWithFiveChar = HashingUtils.maskStringExceptLast4("abcde");

    String maskedStringWithFourChar = HashingUtils.maskStringExceptLast4("abcd");

    String maskedStringWithThreeChar = HashingUtils.maskStringExceptLast4("abc");

    String maskedStringWithEmptyInput = HashingUtils.maskStringExceptLast4("");

    String maskedStringWithNull = HashingUtils.maskStringExceptLast4(null);

    assertEquals("************************************************************980e", maskedString);

    assertEquals("*bcde", maskedStringWithFiveChar);

    assertEquals("abcd", maskedStringWithFourChar);

    assertEquals("abc", maskedStringWithThreeChar);

    assertEquals("", maskedStringWithEmptyInput);

    assertNull(maskedStringWithNull);
  }

}
