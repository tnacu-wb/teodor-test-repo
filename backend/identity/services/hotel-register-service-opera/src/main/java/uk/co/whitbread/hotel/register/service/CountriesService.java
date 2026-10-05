package uk.co.whitbread.hotel.register.service;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.register.client.CountriesClient;
import uk.co.whitbread.hotel.register.model.Country;

@Component
@RequiredArgsConstructor
public class CountriesService {

    private static final String DEFAULT_COUNTRY = "gb";
    private static final String DEFAULT_LANGUAGE = "en";

    private final CountriesClient countriesClient;
    private Map<String, Country> countriesByCode;

    private Map<String, Country> getCountriesByCode() {
        if (Objects.isNull(countriesByCode)) {
            countriesByCode = countriesClient.getCountries(DEFAULT_COUNTRY, DEFAULT_LANGUAGE)
                .getCountries()
                .stream()
                .collect(Collectors.toMap(Country::getCountryCode, Function.identity()));
        }
        return countriesByCode;
    }

    public String getCountryLegend(String countryCode) {
        final Country country = getCountriesByCode().get(countryCode);
        return Objects.isNull(country) ? null : country.getCountryLegend();
    }
}
