package uk.co.whitbread.contentservice.roomtypes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.whitbread.contentservice.roomtypes.model.CookiePoliciesResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMCookiePoliciesService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CookiePoliciesService {

    private final AEMCookiePoliciesService aemCookiePoliciesService;
    private final AEMResponseConverter aemResponseConverter;

    /**
     * Method responsible for getting cookie policies based on language, brand and subBrand:
     * Calls AEM to get the model.json for the brand page and subBrand (if applicable)
     * Converts the AEM response (json with Objects) to MS response (json with Strings)
     *
     * @param language language could be en/de
     * @param brand    brand could be pi,restaurant,businessbooker
     * @param subBrand e.g. beefeater
     * @return CookiePoliciesResponse
     */
    @Cacheable(cacheNames = "contentService-cookiePolicies", unless = "#result == null")
    public CookiePoliciesResponse getCookiePolicies(String language, String brand, String subBrand) {
        CookiePoliciesResponse cookiePoliciesResponse = new CookiePoliciesResponse();
        List<AEMCookiePolicies> cookiePolicies = aemCookiePoliciesService.getCookiePolicies(language, brand, subBrand);
        if (cookiePolicies != null && cookiePolicies.size() > 0) {
            cookiePoliciesResponse.setCookiePolicies(aemResponseConverter.convertAEMCookiePolicies(cookiePolicies));
        }
        return cookiePoliciesResponse;
    }
}
