package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DiscountUtils.class)
class DiscountUtilsTest {


  @Test
  void getDividedWeightedAmounts_getExactDivision() {

    var inputList = mockInputListEqual();
    assertEquals(List.of(new BigDecimal("5.00"), new BigDecimal("5.00"),
            new BigDecimal("5.00"), new BigDecimal("5.00")),
        DiscountUtils.getWeightedDiscountAmounts(inputList,
            BigDecimal.valueOf(20)));
  }

  @Test
  void getDividedWeightedAmounts_forDivisionWithRemainder() {
    var inputList = mockInputListNotEqual();
    assertEquals(List.of(new BigDecimal("6.80"), new BigDecimal("6.60"), new BigDecimal("6.60")),
        DiscountUtils.getWeightedDiscountAmounts(inputList,
            BigDecimal.valueOf(20)));
  }

  private List<BigDecimal> mockInputListEqual() {
    List<BigDecimal> inputRates = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      inputRates.add(BigDecimal.TEN);
    }
    return inputRates;
  }

  private List<BigDecimal> mockInputListNotEqual() {
    List<BigDecimal> inputRates = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      inputRates.add(BigDecimal.TEN);
    }

    return inputRates;
  }
}
