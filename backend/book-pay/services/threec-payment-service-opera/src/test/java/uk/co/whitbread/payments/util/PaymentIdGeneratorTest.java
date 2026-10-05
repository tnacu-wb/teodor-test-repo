package uk.co.whitbread.payments.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PaymentIdGeneratorTest {

    private final PaymentIdGenerator paymentIdGenerator = new PaymentIdGenerator();

    @Test
    void testPaymentIdFormat() {
        String paymentId = paymentIdGenerator.generatePaymentId();
        assertEquals(12, paymentId.length());
        assertEquals('D', paymentId.charAt(11));
    }

    @Test
    void testPaymentIdIdempotency() {
        assertNotEquals(paymentIdGenerator.generatePaymentId(), paymentIdGenerator.generatePaymentId());
        assertNotEquals(paymentIdGenerator.generatePaymentId(), paymentIdGenerator.generatePaymentId());
        assertNotEquals(paymentIdGenerator.generatePaymentId(), paymentIdGenerator.generatePaymentId());
        assertNotEquals(paymentIdGenerator.generatePaymentId(), paymentIdGenerator.generatePaymentId());
        assertNotEquals(paymentIdGenerator.generatePaymentId(), paymentIdGenerator.generatePaymentId());
    }
}