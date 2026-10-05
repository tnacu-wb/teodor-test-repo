package uk.co.whitbread.contentservice.roomtypes.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMFeignClientRates;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoRatesDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.aem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AEMRateClassificationsService {
    private static final String ERROR_MESSAGE = "AEM rate classifications not found.";
    private final AEMFeignClientRates aemFeignClientRates;
    private final FeignProperties feignProperties;

    /**
     * Method responsible for getting all the room types details for a given country, language and brand:
     * Calls AEM to get the model.json for the brand page
     *
     * @param language  language could be en/de
     * @param brand     brand could be pi,hub,zip,pid
     * @param hotelCode hotelCode for specific rate e.g. DUBAIR
     * @return List<AEMRateClassifications></AEMRoomType>
     */
    public List<AEMRateClassifications> getRateClassifications(String language, String brand, String hotelCode) {
        List<AEMRateClassifications> aemRateClassifications = new ArrayList<>();
        try {
            String country = language;
            String brandOrHotelCode = brand;
            if (language.equalsIgnoreCase(LanguageCode.en.name())) {
                country = CountryCode.gb.name();
            }
            if (!hotelCode.equalsIgnoreCase("")) {
                brandOrHotelCode = hotelCode;
            }
            try {
                getRateClassifications(language, aemRateClassifications, country, brandOrHotelCode);
            } catch (Exception exception) {
                log.info("Failed to get hotel specific rates for hotelCode={}, defaulting to brand: {}", hotelCode, brand);

                getRateClassifications(language, aemRateClassifications, country, brand);
            }
        } catch (Exception exception) {
            log.error("Failed to get rates for " +
                    "language={}, brand={}, hotelCode={}, exception={} ", language, brand, hotelCode, exception);

            throw new NoRatesDataFoundException(ERROR_MESSAGE);
        }

        return aemRateClassifications;
    }

    private void getRateClassifications(String language, List<AEMRateClassifications> aemRateClassifications, String country, String brandOrHotelCode) {
        AEMRateClassificationsResponse rateClassificationsJson = aemFeignClientRates.getRateClassifications(country, language, brandOrHotelCode, feignProperties.getAem().getRatesresource());
        final Map<String, RateContentFragment> contentFragments = Optional.ofNullable(rateClassificationsJson.getItems())
                .map(AEMRateItems::getRoot)
                .map(RateRoot::getItems)
                .orElseThrow(() -> new NoRatesDataFoundException(ERROR_MESSAGE));

            if (!contentFragments.isEmpty()) {
                contentFragments.values().forEach(contentFragment -> {
                    AEMRateClassifications aemRateClassification = contentFragment.getElements();
                    aemRateClassifications.add(aemRateClassification);
                });
            }
            else {
                throw new NoRatesDataFoundException(ERROR_MESSAGE);
            }
    }
}
