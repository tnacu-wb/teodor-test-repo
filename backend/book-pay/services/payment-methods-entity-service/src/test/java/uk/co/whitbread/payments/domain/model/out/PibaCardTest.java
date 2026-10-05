package uk.co.whitbread.payments.domain.model.out;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.whitbread.payments.domain.model.out.Country.GB;
import static uk.co.whitbread.payments.domain.model.out.Country.IE;
import static uk.co.whitbread.payments.domain.model.out.PibaCard.DE;
import static uk.co.whitbread.payments.domain.model.out.PibaCard.UK;

class PibaCardTest {

    @Test
    void getType() {
        assertEquals("PIBA", UK.getType());
        assertEquals("PIBA", DE.getType());
    }

    @Test
    void getSubType() {
        assertEquals("PIBAGB", UK.getSubType());
        assertEquals("PIBADE", DE.getSubType());
    }

    @Test
    void getCountry() {
        assertEquals(GB, UK.getCountry());
        assertEquals(Country.DE, DE.getCountry());
    }

    @Test
    void isEnabledCountry() {
        assertTrue(PibaCard.isEnabledCountry(GB));
        assertTrue(PibaCard.isEnabledCountry(Country.DE));
        assertFalse(PibaCard.isEnabledCountry(IE));
        assertFalse(PibaCard.isEnabledCountry(null));
    }

    @Test
    void isPibaUkInInvalidCountry() {
        assertTrue(PibaCard.isPibaUkInInvalidCountry(UK.getSubType(), Country.DE));
        assertFalse(PibaCard.isPibaUkInInvalidCountry(UK.getSubType(), GB));
        assertTrue(PibaCard.isPibaUkInInvalidCountry(UK.getSubType(), IE));
        assertFalse(PibaCard.isPibaUkInInvalidCountry(DE.getSubType(), GB));
        assertFalse(PibaCard.isPibaUkInInvalidCountry(null, GB));
        assertTrue(PibaCard.isPibaUkInInvalidCountry(UK.getSubType(), null));
        assertFalse(PibaCard.isPibaUkInInvalidCountry(null, null));
    }

    @Test
    void isPibaEuInInvalidCountry() {
        assertTrue(PibaCard.isPibaEuInInvalidCountry(DE.getSubType(), GB));
        assertFalse(PibaCard.isPibaEuInInvalidCountry(DE.getSubType(), Country.DE));
        assertTrue(PibaCard.isPibaEuInInvalidCountry(DE.getSubType(), IE));
        assertFalse(PibaCard.isPibaEuInInvalidCountry(UK.getSubType(), Country.DE));
        assertFalse(PibaCard.isPibaEuInInvalidCountry(null, Country.DE));
        assertTrue(PibaCard.isPibaEuInInvalidCountry(DE.getSubType(), null));
        assertFalse(PibaCard.isPibaEuInInvalidCountry(null, null));

    }
    @Test
    void resolveSubTypeForPibaCards() {
        Optional<PibaCard> card = PibaCard.resolveSubTypeForPibaCards("PI");
        assertFalse(card.isEmpty());
    }
}