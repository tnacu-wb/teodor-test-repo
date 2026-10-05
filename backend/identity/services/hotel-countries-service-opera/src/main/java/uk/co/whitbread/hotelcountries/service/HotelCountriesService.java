package uk.co.whitbread.hotelcountries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotelcountries.client.AEMFeignClient;
import uk.co.whitbread.hotelcountries.config.FeignProperties;
import uk.co.whitbread.hotelcountries.converter.CountriesResponseConverter;
import uk.co.whitbread.hotelcountries.model.CountriesResponse;
import uk.co.whitbread.hotelcountries.model.aem.AemCountries;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelCountriesService {
    private final FeignProperties feignProperties;
    private final AEMFeignClient aemClient;
    private final CountriesResponseConverter countriesResponseConverter;

    @Cacheable(cacheNames = "hotelCountries", keyGenerator = "hotelCountriesKeyGenerator")
    public CountriesResponse getCountries(String country, String language) {
        log.info("Getting list of countries from BART.");

        AemCountries aemCountries = aemClient.getAemCountries(country,
                language, feignProperties.getAem().getResource());

        return countriesResponseConverter.convertToCountriesResponse(aemCountries);
    }

}
