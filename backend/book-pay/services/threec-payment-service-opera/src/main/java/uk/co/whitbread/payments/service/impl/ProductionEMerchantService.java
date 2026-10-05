package uk.co.whitbread.payments.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.converters.DynamicMerchantGenerator;
import uk.co.whitbread.payments.model.EMerchantDetails;
import uk.co.whitbread.payments.service.EMerchantService;

@Service
@Profile("opera-prod")
@Slf4j
@RequiredArgsConstructor
public class ProductionEMerchantService implements EMerchantService {

    private final DynamicMerchantGenerator dynamicMerchantGenerator;

    @Override
    public EMerchantDetails getEMerchantDetails(String subType, String hotelCode) {
        return dynamicMerchantGenerator.getDynamicEMerchantDetails(subType, hotelCode);
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
