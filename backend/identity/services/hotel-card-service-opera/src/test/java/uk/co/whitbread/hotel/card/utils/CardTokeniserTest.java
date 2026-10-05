package uk.co.whitbread.hotel.card.utils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.client.payment.Payment3CP;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenRequest;
import uk.co.whitbread.hotel.card.client.payment.model.CreateTokenResponse;
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.PaymentCard;


@ExtendWith(MockitoExtension.class)
class CardTokeniserTest {

    @InjectMocks
    private CardTokeniser sut;
    @Mock
    private Payment3CP payment3CP;

    @Test
     void testTokeniseCard_WithUnmaskedCardNumber() {

        // Given
        PaymentCard paymentCard = mockPaymentCard();
        // When
        when(payment3CP.createToken(any())).thenReturn(new CreateTokenResponse("token", "VI"));

         sut.tokeniseCard(paymentCard, "test@test.com");

        //Then
        assertEquals("token", paymentCard.getCardToken());
        assertEquals("VI", paymentCard.getCardType());
        verify(payment3CP, times(1)).createToken(any());
    }

    @Test
     void testTokeniseCard_WithMaskedCardNumber() {
        // Given
        PaymentCard paymentCard = mockPaymentCard();
        paymentCard.setCardNumber("****1234");

        // Act
        sut.tokeniseCard(paymentCard, "test@test.com");

        // Assert
        verify(payment3CP, never()).createToken(any(CreateTokenRequest.class));
        assertNull(paymentCard.getCardToken());
    }

  @Test
  void testTokeniseCard_throwsError_jsonErrorMessage() {
    // Given
    PaymentCard paymentCard = mockPaymentCard();

    // Act
    doThrow(new RuntimeException("{\"message\": \"Invalid card number\"}"))
        .when(payment3CP).createToken(any(CreateTokenRequest.class));

    assertThatThrownBy(() -> sut.tokeniseCard(paymentCard, "test@test.com"))
        .isInstanceOf(ThreeCPClientException.class)
        .hasMessage("Invalid card number");
  }

  @Test
  void testTokeniseCard_throwsError_stringErrorMessage() {
    // Given
    PaymentCard paymentCard = mockPaymentCard();

    // Act
    doThrow(new RuntimeException("ThreeCp error message"))
        .when(payment3CP).createToken(any(CreateTokenRequest.class));

    assertThatThrownBy(() -> sut.tokeniseCard(paymentCard, "test@test.com"))
        .isInstanceOf(ThreeCPClientException.class)
        .hasMessage("Something went wrong, please try again later");
  }

    private PaymentCard mockPaymentCard () {
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardId("7");
        paymentCard.setCardNumber("4444333322221111");
        paymentCard.setExpiryDate("10/24");
        paymentCard.setBillingAddress(Address.builder()
                .line1("line 1").countryCode("RO").postCode("100200").build());
        return paymentCard;
    }
}
