package uk.co.whitbread.hotel.account.service;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.client.countries.CountriesClient;
import uk.co.whitbread.hotel.account.client.countries.model.Country;

@Service
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

    public String getCountryCodeISO(String countryCode) {
        final Country country = getCountriesByCode().get(countryCode);
        return Objects.isNull(country) ? null : country.getCountryCodeISO();
    }
}
