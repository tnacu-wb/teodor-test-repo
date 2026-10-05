package uk.co.whitbread.payments.converters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardholderStateMapperTest {

    private static final String USA_COUNTRY_CODE = "US";
    private static final String USA_STATE_CODE = "CA";
    private static final String CANADA_COUNTRY_CODE = "CA";
    private static final String CANADA_STATE_CODE = "ON";
    private static final String EMPTY_STRING = "";

    private final CardholderStateMapper stateMapper = new CardholderStateMapper();

    @Test
    void verifyNullCountryCode() {
        var state = stateMapper.getCardholderState(null);
        assertEquals(EMPTY_STRING, state);
    }

    @Test
    void test_WhenUSACountryCode_ExpectStateCA() {
        var state = stateMapper.getCardholderState(USA_COUNTRY_CODE);
        assertEquals(USA_STATE_CODE, state);
    }

    @Test
    void test_WhenCanadaCountryCode_ExpectStateON() {
        var state = stateMapper.getCardholderState(CANADA_COUNTRY_CODE);
        assertEquals(CANADA_STATE_CODE, state);
    }

    @Test
    void test_WhenOtherCountryCode_ExpectStateEmpty() {
        var state = stateMapper.getCardholderState("GB");
        assertEquals(EMPTY_STRING, state);
    }
}