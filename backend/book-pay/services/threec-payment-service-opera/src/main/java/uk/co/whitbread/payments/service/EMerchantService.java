package uk.co.whitbread.payments.service;

import uk.co.whitbread.payments.model.EMerchantDetails;


public interface EMerchantService {

    EMerchantDetails getEMerchantDetails(String subType, String hotelCode);

    EMerchantDetails getTokenisedEMerchantDetails();

    EMerchantDetails getEMerchantDetailsByCountry(String country);
}
