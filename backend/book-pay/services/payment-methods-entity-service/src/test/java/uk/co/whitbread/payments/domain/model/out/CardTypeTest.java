package uk.co.whitbread.payments.domain.model.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.payments.domain.model.out.CardType.*;

class CardTypeTest {

    private static final String BUSINESS_ACCOUNT = "Business Account";
    private static final String MASTERCARD_CREDIT = "Mastercard Credit";
    @Test
    void isBusinessCard_Valid_Business_cards() {
        assertTrue(isBusinessCard(AT.name()));
        assertTrue(isBusinessCard(BD.name()));
        assertTrue(isBusinessCard(PI.name()));
    }

    @Test
    void isBusinessCard_InValid_Business_cards() {
        assertFalse(isBusinessCard(DI.name()));
        assertFalse(isBusinessCard(VI.name()));
    }

    @Test
    void validateCardNames(){
        assertEquals(MASTERCARD_CREDIT, AC.getType());
        assertEquals(MASTERCARD_CREDIT, MC.getType());
        assertEquals(MASTERCARD_CREDIT, AX.getType());
        assertEquals(BUSINESS_ACCOUNT, AT.getType());
        assertEquals(BUSINESS_ACCOUNT, PI.getType());
        assertEquals(BUSINESS_ACCOUNT, BD.getType());
    }

}