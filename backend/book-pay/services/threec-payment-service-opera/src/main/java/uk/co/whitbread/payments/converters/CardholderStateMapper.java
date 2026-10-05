package uk.co.whitbread.payments.converters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CardholderStateMapper {

    private static final String USA_COUNTRY_CODE = "US";
    private static final String USA_STATE_CODE = "CA";
    private static final String CANADA_COUNTRY_CODE = "CA";
    private static final String CANADA_STATE_CODE = "ON";
    private static final String EMPTY_STRING = "";

    public String getCardholderState(String countryCode) {
        if (countryCode == null)
            return EMPTY_STRING;

        switch (countryCode) {
            case USA_COUNTRY_CODE:
                return USA_STATE_CODE;
            case CANADA_COUNTRY_CODE:
                return CANADA_STATE_CODE;
            default:
                return EMPTY_STRING;
        }
    }
}
