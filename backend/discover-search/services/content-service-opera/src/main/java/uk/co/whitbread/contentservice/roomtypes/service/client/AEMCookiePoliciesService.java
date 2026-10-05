package uk.co.whitbread.contentservice.roomtypes.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMFeignClientCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoCookiePoliciesDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.SubBrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.aem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AEMCookiePoliciesService {
    private static final String ERROR_MESSAGE = "AEM cookie policies not found.";
    private final AEMFeignClientCookiePolicies aemFeignClientCookiePolicies;
    private final FeignProperties feignProperties;

    /**
     * Method responsible for getting cookie policies based on the language, brand, and subBrand:
     * Calls AEM to get the model.json from the brand page
     *
     * @param language language could be en/de
     * @param brand    brand could be pi,restaurant,businessbooker
     * @param subBrand e.g. beefeater
     * @return List<AEMCookiePolicies>
     */
    public List<AEMCookiePolicies> getCookiePolicies(String language, String brand, String subBrand) {
        List<AEMCookiePolicies> aemCookiePolicies = new ArrayList<>();
        try {
            String country = language;
            if (language.equalsIgnoreCase(LanguageCode.en.name())) {
                country = CountryCode.gb.name();
            }
            getCookiePolicies(language, aemCookiePolicies, country, brand, subBrand);
        } catch (Exception exception) {
            throw new NoCookiePoliciesDataFoundException(ERROR_MESSAGE);
        }
        return aemCookiePolicies;
    }

    private void getCookiePolicies(String language, List<AEMCookiePolicies> aemCookiePolicies, String country, String brand, String subBrand) {
        AEMCookiePoliciesResponse cookiePoliciesJson = getCookiePoliciesJson(country, language, brand, subBrand);
        final Map<String, CookiePoliciesContentFragment> contentFragments = Optional.ofNullable(cookiePoliciesJson.getItems())
                .map(AEMCookiePoliciesItems::getRoot)
                .map(CookiePoliciesRoot::getItems)
                .orElseThrow(() -> new NoCookiePoliciesDataFoundException(ERROR_MESSAGE));
        if (!contentFragments.isEmpty()) {
            contentFragments.values().forEach(contentFragment -> {
                AEMCookiePolicies cookiePolicies = contentFragment.getElements();
                aemCookiePolicies.add(cookiePolicies);
            });
        }
    }

    private AEMCookiePoliciesResponse getCookiePoliciesJson(String country, String language, String brand, String subBrand) {
        AEMCookiePoliciesResponse cookiePoliciesJson = null;
        if (!subBrand.isEmpty() && !subBrand.equals(SubBrandCode.none.name())) {
            cookiePoliciesJson = aemFeignClientCookiePolicies.getCookiePolicies(country, language, brand, subBrand, feignProperties.getAem().getCookiePoliciesResource());
            if (cookiePoliciesJson == null) {
                cookiePoliciesJson = aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), brand, subBrand, feignProperties.getAem().getCookiePoliciesResource());

            }
        }

        if (cookiePoliciesJson == null) {
            cookiePoliciesJson = aemFeignClientCookiePolicies.getCookiePolicies(country, language, brand, feignProperties.getAem().getCookiePoliciesResource());
        }

        if (cookiePoliciesJson == null) {
            cookiePoliciesJson = aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), brand, feignProperties.getAem().getCookiePoliciesResource());
            if (cookiePoliciesJson == null) {
                cookiePoliciesJson = aemFeignClientCookiePolicies.getCookiePolicies(country, language, BrandCode.pi.name(), feignProperties.getAem().getCookiePoliciesResource());
            }
        }
        return cookiePoliciesJson;
    }
}
