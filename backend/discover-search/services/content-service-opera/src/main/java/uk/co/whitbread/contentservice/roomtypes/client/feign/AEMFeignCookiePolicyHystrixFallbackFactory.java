package uk.co.whitbread.contentservice.roomtypes.client.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePoliciesResponse;

@Slf4j
@Component
public class AEMFeignCookiePolicyHystrixFallbackFactory implements
    FallbackFactory<AEMFeignClientCookiePolicies> {

    @Override
    public AEMFeignClientCookiePolicies create(Throwable throwable) {

        return new AEMFeignClientCookiePolicies() {

            @Override
            public AEMCookiePoliciesResponse getCookiePolicies(String country, String language, String brand, String resource) {
                log.error("Failed to call aem to get getCookiePolicies (or circuit breaker is open) " +
                        "country={}, language={}, brand={}, resource={}", country, language, brand, resource, throwable);
                return null;
            }

            @Override
            public AEMCookiePoliciesResponse getCookiePolicies(String country, String language, String brand, String subBrand, String resource) {
                log.error("Failed to call aem to getCookiePolicies with subBrand (or circuit breaker is open) " +
                        "country={}, language={}, brand={}, subBrand={}, resource={}", country, language, brand, subBrand, resource, throwable);
                return null;
            }
        };
    }
}