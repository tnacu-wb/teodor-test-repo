package uk.co.whitbread.hotelcountries.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotelcountries.model.CountriesResponse;
import uk.co.whitbread.hotelcountries.model.Country;
import uk.co.whitbread.hotelcountries.model.aem.AemCountries;
import uk.co.whitbread.hotelcountries.model.aem.AemCountry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CountriesResponseConverter {

    public CountriesResponse convertToCountriesResponse(AemCountries aemCountries) {
        List<Country> countries = new ArrayList<>();
        for (AemCountry aemCountry : aemCountries.getCountries()) {
            Country country = buildCountry(aemCountry);
            countries.add(country);
        }

        countries.sort(Comparator.comparing(Country::getCountryCode));
        return new CountriesResponse(countries);
    }

    private Country buildCountry(AemCountry aemCountry) {
        Country country = new Country();
        country.setCountryCode(aemCountry.getCountryCode());
        country.setCountryCodeISO(aemCountry.getIsoCode());
        country.setCountryLegend(aemCountry.getLegend());
        country.setPassportRequired(Optional.of(aemCountry).map(AemCountry::isPassportRequired).orElse(false));
        country.setDialingCode(aemCountry.getDialingCode());
        country.setFlagImg(aemCountry.getFlagImg());

        return country;
    }

}
