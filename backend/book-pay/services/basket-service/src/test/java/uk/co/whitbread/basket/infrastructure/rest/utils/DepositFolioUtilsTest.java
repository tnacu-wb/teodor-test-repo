package uk.co.whitbread.basket.infrastructure.rest.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.exception.CompressionException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.in.Charge;
import uk.co.whitbread.basket.domain.model.basket.in.ChargeAmount;

class DepositFolioUtilsTest {

  @Test
  void getBytesFromAndGetChargesFrom_shouldRoundTripCharges() {
    var charge = Charge.builder()
        .transactionCode("TRX")
        .postingQuantity(1)
        .postingReference("POST_REF")
        .chargeAmount(ChargeAmount.builder()
            .amount(new BigDecimal("12.50"))
            .currencyCode("GBP")
            .build())
        .build();
    var charges = List.of(charge);

    var compressed = DepositFolioUtils.getBytesFrom(charges);
    var restored = DepositFolioUtils.getChargesFrom(compressed);

    assertEquals(charges, restored);
  }

  @Test
  void getChargesFrom_shouldThrowCompressionExceptionForInvalidPayload() {
    var invalidPayload = "not-gzip-data".getBytes(StandardCharsets.UTF_8);

    var thrownException = assertThrowsExactly(CompressionException.class,
        () -> DepositFolioUtils.getChargesFrom(invalidPayload));

    assertEquals(ErrorCode.DIGITAL_DECOMPRESSION_EXCEPTION.getCode(), thrownException.getErrorCode());
  }
}
