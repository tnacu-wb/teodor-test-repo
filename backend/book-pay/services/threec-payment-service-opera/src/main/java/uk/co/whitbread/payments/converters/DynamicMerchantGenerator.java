package uk.co.whitbread.payments.converters;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.model.PaymentSubType;

@Service
@Slf4j
public class DynamicMerchantGenerator {

    @Value("${3c.emerchant.validation.code}")
    private String eMerchantValidationCode;

    @Value("${3c.username.tokenisation}")
    private String tokensationUsername;

    @Value("${3c.username.tokenisationDE}")
    private String tokensationUsernameDE;

    public static final String COUNTRY_DE = "de";

    public EMerchantDetails getDynamicEMerchantDetails(String subType, String hotelCode) {
        switch (PaymentSubType.valueOf(subType)) {
            case ECOMM:
            case MIT:
            case AUTHORIZE_CARD:
            case SECURE_BOOKING:
            case MIT_CC:
            case ECKOH: {
                return generateDynamicMerchantDetails("eWB-" + hotelCode);
            }
            case MOTO: {
                return generateDynamicMerchantDetails("mWB-" + hotelCode);
            }
            default:
                break;
        }
        return new EMerchantDetails();
    }

    public EMerchantDetails getTokenisedEMerchantDetails() {
        return generateDynamicMerchantDetails(tokensationUsername);
    }

    private EMerchantDetails generateDynamicMerchantDetails(String username) {
        EMerchantDetails eMerchantDetails = new EMerchantDetails();
        eMerchantDetails.setUsername(username);
        eMerchantDetails.setPassword(eMerchantValidationCode);
        return eMerchantDetails;
    }

    public EMerchantDetails getEMerchantDetailsByCountry(String country) {
        String eMerchantId =
            country.equalsIgnoreCase(COUNTRY_DE) ? tokensationUsernameDE : tokensationUsername;
        return generateDynamicMerchantDetails(eMerchantId);
    }
}
