package uk.co.whitbread.contentservice.roomtypes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.whitbread.contentservice.roomtypes.model.RateClassificationResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassifications;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMRateClassificationsService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateClassificationService {

    private final AEMRateClassificationsService aemRateClassificationsService;
    private final AEMResponseConverter aemResponseConverter;

    /**
     * Method responsible for getting all the room types details for a given country, language and brand:
     * Calls AEM to get the model.json for the brand page
     * Converts the AEM response (json with Objects) to MS response (json with Strings)
     *
     * @param language language could be en/de
     * @param brand    brand could be pi,hub,zip,pid
     * @param hotelCode e.g. HEAFIV
     * @return RateClassifciatonsResponse
     */
    @Cacheable(cacheNames = "contentService-allRateClassifications", unless = "#result == null")
    public RateClassificationResponse getAllRateClassifications(String language, String brand, String hotelCode) {
        RateClassificationResponse rateClassificationResponse = new RateClassificationResponse();
        // Call AEM to get room types for the given country,language and brand
        List<AEMRateClassifications> aemRateClassifications = aemRateClassificationsService.getRateClassifications(language, brand, hotelCode);
        rateClassificationResponse.setRateClassifications(aemResponseConverter.convertAEMResponseRates(aemRateClassifications));
        return rateClassificationResponse;
    }

    /**
     * Method responsible for getting the specific room type details for a given country, language and brand:
     * Calls AEM to get the model.json for the brand page
     * Converts the AEM response (json with Objects) to MS response (json with Strings)
     *
     * @param language     language could be en/de
     * @param brand        brand could be pi,hub,zip,pid
     * @param hotelCode    hotelCode e.g. DUBAIR, BATJAM, LONSTM
     * @param rateClassification    rate classification code e.g. Q, L
     * @return RateClassificationResponse
     */
    @Cacheable(cacheNames = "contentService-rateClassificationsForRate", unless = "#result == null")
    public RateClassificationResponse getRateClassificationsForRate(String language, String brand, String hotelCode, String rateClassification) {
        RateClassificationResponse rateClassificationResponse = new RateClassificationResponse();
        // Call AEM to get specific  the given room type code
        try {
            List<AEMRateClassifications> aemRateClassifications = aemRateClassificationsService.getRateClassifications(language, brand, hotelCode);
            rateClassificationResponse.setRateClassifications(aemResponseConverter.getRateClassificationsForRate(aemRateClassifications, rateClassification));
        } catch (Error e) {
            //this failed so will return an empty array as the rate code doesn't exit
        }
        return rateClassificationResponse;
    }
}
