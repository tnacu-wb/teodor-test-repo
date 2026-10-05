package uk.co.whitbread.hotel.card.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.card.model.PaymentCard;


public class PaymentCardMaskerTest {

    private PaymentCardMasker sut;

    @BeforeEach
    public void setUp() {
        sut = new PaymentCardMasker();
    }

    @Test
    public void maskNumber() {

        // Given
        String cardNumber = "4444333322221111";

        // When
        String actual = sut.maskNumber(cardNumber);

        //Then
        assertEquals("************1111", actual);
    }

    @Test
    public void isMasked_unmaskedNumber_shouldReturnFalse() {

        // Given
        String cardNumber = "4444333322221111";

        // When
        boolean actual = sut.isMasked(cardNumber);

        // Then
        assertFalse(actual);
    }

    @Test
    public void isMasked_maskedNumber_shouldReturnTrue() {

        // Given
        String cardNumber = "************1111";

        // When
        boolean actual = sut.isMasked(cardNumber);

        // Then
        assertTrue(actual);
    }

    @Test
    public void hideSensitiveInfo_MasksCardNumberAndRemovesPassword() {

        // Given
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardId("1");
        paymentCard.setCardHolderName("Smith");
        paymentCard.setCardNumber("4444333322221112");
        paymentCard.setCnpRequired(true);
        paymentCard.setCnpBusinessAccountUsername("baUsername");
        paymentCard.setCnpBusinessAccountPassword("baPassword");

        // When
        PaymentCard actual = sut.hideSensitiveInfoFromCard(paymentCard, false);

        // Then
        assertEquals("************1112", actual.getCardNumber());
        assertNull(actual.getCnpBusinessAccountPassword());
        assertEquals("1", actual.getCardId());
        assertEquals("Smith", actual.getCardHolderName());
        assertTrue(actual.isCnpRequired());
        assertEquals("baUsername", actual.getCnpBusinessAccountUsername());
    }

    @Test
    public void hideSensitiveInfo_RetrieveCardInfoWhenUnmaskFlagIsTrue_ReturnsCardNumberAndPassword() {

        // Given
        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardId("1");
        paymentCard.setCardHolderName("Smith");
        paymentCard.setCardNumber("4444333322221112");
        paymentCard.setCnpRequired(true);
        paymentCard.setCnpBusinessAccountPassword("baPassword");

        // When
        PaymentCard actual = sut.hideSensitiveInfoFromCard(paymentCard, true);

        // Then
        assertEquals("4444333322221112", actual.getCardNumber());
        assertEquals("baPassword", actual.getCnpBusinessAccountPassword());
        assertEquals("1", actual.getCardId());
        assertEquals("Smith", actual.getCardHolderName());
        assertTrue(actual.isCnpRequired());
    }
}