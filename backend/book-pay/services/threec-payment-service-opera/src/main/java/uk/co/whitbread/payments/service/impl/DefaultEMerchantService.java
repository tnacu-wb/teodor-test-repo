package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.converters.DynamicMerchantGenerator;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.service.EMerchantService;

import java.util.List;

@Service
@Profile("!opera-prod")
@Slf4j
@RequiredArgsConstructor
public class DefaultEMerchantService implements EMerchantService {

    private final DynamicMerchantGenerator dynamicMerchantGenerator;

    @Value("${hotel.codes.list}")
    private List<String> hotelCodes;

    @Value("${3c.username.default}")
    private String defaultUsername;

    /*
     * Setup e-emerchant id's based on subtype (ECOMM, MOTO) and hotelcode (eg, CARNOR)
     * Only few hotels are setup with e-emerchant details in 3CP test environment and the rest would use a default merchantId
     */
    @Override
    public EMerchantDetails getEMerchantDetails(String subType, String hotelCode) {
        if(!hotelCodes.isEmpty() && hotelCodes.contains(hotelCode)) {
            log.info("hotel code {} is configured at 3CP end.",hotelCode);
            return dynamicMerchantGenerator.getDynamicEMerchantDetails(subType, hotelCode);
        } else {
            log.debug("hotel code {} is NOT configured at 3CP end in test environment.",hotelCode);
            return dynamicMerchantGenerator.getDynamicEMerchantDetails(subType, defaultUsername);
        }
    }

    @Override
    public EMerchantDetails getTokenisedEMerchantDetails() {
        return dynamicMerchantGenerator.getTokenisedEMerchantDetails();
    }

    @Override
    public EMerchantDetails getEMerchantDetailsByCountry(String country) {
        return dynamicMerchantGenerator.getEMerchantDetailsByCountry(country);
    }
}
