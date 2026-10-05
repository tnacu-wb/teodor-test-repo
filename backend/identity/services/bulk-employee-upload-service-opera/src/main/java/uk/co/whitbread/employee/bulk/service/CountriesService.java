package uk.co.whitbread.employee.bulk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import uk.co.whitbread.employee.bulk.client.CountriesClient;
import uk.co.whitbread.employee.bulk.model.Country;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CountriesService {

    private static final String DEFAULT_COUNTRY = "gb";
    private static final String DEFAULT_LANGUAGE = "en";

    private final CountriesClient countriesClient;

    @Cacheable("countries")
    public Map<String, Country> getCountriesByLegend() {
        return countriesClient.getCountries(DEFAULT_COUNTRY, DEFAULT_LANGUAGE)
                .getCountries()
                .stream()
                .collect(Collectors.toMap(Country::getCountryLegend, Function.identity()));
    }
}
