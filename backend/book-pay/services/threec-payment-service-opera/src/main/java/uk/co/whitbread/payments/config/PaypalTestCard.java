package uk.co.whitbread.payments.config;

import lombok.Data;

import java.util.List;

@Data
public class PaypalTestCard {
    private Boolean enablePaypalTestCard;
    private List<String> testCardHotelCodes;
    private String testCardNumber;
    private String testCardExpiryDateMMYY;
    private String testCVV2;
    private String testCardholderStreetAddress1;
    private String testCardholderCity;
    private String testCardholderZipCode;
}
