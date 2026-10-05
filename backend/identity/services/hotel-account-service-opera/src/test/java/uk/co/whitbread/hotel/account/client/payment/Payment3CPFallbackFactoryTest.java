package uk.co.whitbread.hotel.account.client.payment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import uk.co.whitbread.hotel.account.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.account.client.payment.model.CardHolder;

class Payment3CPFallbackFactoryTest {

    @Test
    void shouldCreateFallbackInstance() {
        Payment3CPFallbackFactory factory = new Payment3CPFallbackFactory();
        Throwable cause = new RuntimeException("Bad request");
        Payment3CP fallback = factory.create(cause);

        assertNotNull(fallback);
        assertInstanceOf(Payment3CP.class, fallback);
    }

    @Test
    void fallbackShouldThrowEmployeeUpdate500Exception() {
        Payment3CPFallbackFactory factory = new Payment3CPFallbackFactory();
        Payment3CP fallback = factory.create(new RuntimeException("test"));

        Exception exception = assertThrows(
            uk.co.whitbread.hotel.account.exceptions.EmployeeUpdate500Exception.class,
            () -> fallback.createToken(null)
        );
        assertEquals("test", exception.getMessage());
    }

    @Test
    void fallbackShouldThrowEmployeeUpdate500ExceptionWhenRequestIsNotNull() {
        Payment3CPFallbackFactory factory = new Payment3CPFallbackFactory();
        Payment3CP fallback = factory.create(new RuntimeException("not-null-request"));
        CreateTokenRequest request = CreateTokenRequest.builder()
                .requestId("req-123")
                .cardNumber("4111111111111111")
                .expiryMonth("12")
                .expiryYear("2030")
                .cardHolder(CardHolder.builder()
                        .cardHolderName("Test User")
                        .email("test@example.com")
                        .build())
                .build();

        Exception exception = assertThrows(
            uk.co.whitbread.hotel.account.exceptions.EmployeeUpdate500Exception.class,
            () -> fallback.createToken(request)
        );
        assertEquals("not-null-request", exception.getMessage());
    }

    @Test
    void fallbackShouldThrowEmployeeUpdate500ExceptionWhenCardNumberIsNull() {
        Payment3CPFallbackFactory factory = new Payment3CPFallbackFactory();
        Payment3CP fallback = factory.create(new RuntimeException("card-number-null"));
        CreateTokenRequest request = CreateTokenRequest.builder()
                .requestId("req-456")
                .cardNumber(null)
                .expiryMonth("11")
                .expiryYear("2029")
                .cardHolder(CardHolder.builder()
                        .cardHolderName("Null Card User")
                        .email("nullcard@example.com")
                        .build())
                .build();

        Exception exception = assertThrows(
            uk.co.whitbread.hotel.account.exceptions.EmployeeUpdate500Exception.class,
            () -> fallback.createToken(request)
        );
        assertEquals("card-number-null", exception.getMessage());
    }
}
