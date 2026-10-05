package uk.co.whitbread.payments.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.IntStream;

@Service
@Slf4j
public class PaymentIdGenerator {

    private static final String CONTROL_DIGITAL_CHARACTER = "D";
    private static SecureRandom random;

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating a secure random id");
        }
    }

    public final String generatePaymentId() {
        StringBuilder paymentId = new StringBuilder();
        IntStream intStream = random.ints(11, 1, 9);
        intStream.forEach(paymentId::append);
        return paymentId.append(CONTROL_DIGITAL_CHARACTER).toString();
    }
}