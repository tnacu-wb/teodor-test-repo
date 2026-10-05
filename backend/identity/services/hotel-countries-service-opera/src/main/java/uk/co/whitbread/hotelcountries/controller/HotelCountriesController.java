package uk.co.whitbread.hotelcountries.controller;

import static uk.co.whitbread.hotelcountries.util.Utils.sanitizeInputString;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.hotelcountries.model.CountriesResponse;
import uk.co.whitbread.hotelcountries.service.HotelCountriesService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HotelCountriesController {

    private static final String DEFAULT_COUNTRY = "gb";
    private static final String DEFAULT_LANGUAGE = "en";

    private final HotelCountriesService hotelCountriesService;

    @Deprecated
    @GetMapping(value = "/countries", produces = MediaType.APPLICATION_JSON_VALUE)
    public CountriesResponse retrieveAllCountriesWithHeaders(
            @RequestHeader(name = "country", defaultValue = DEFAULT_COUNTRY) String country,
            @RequestHeader(name = "language", defaultValue = DEFAULT_LANGUAGE) String language) {

        if (log.isDebugEnabled()) {
            log.debug("Called /countries with country {} and language {}",
                sanitizeInputString(country), sanitizeInputString(language));
        }
        return hotelCountriesService.getCountries(country.toLowerCase(), language.toLowerCase());
    }

    @GetMapping(value = "/countries", params = { "country", "language" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public CountriesResponse retrieveAllCountries(
            @RequestParam(name = "country", defaultValue = DEFAULT_COUNTRY) String country,
            @RequestParam(name = "language", defaultValue = DEFAULT_LANGUAGE) String language) {

        if (log.isDebugEnabled()) {
            log.debug("Called /countries with country {} and language {}",
                sanitizeInputString(country), sanitizeInputString(language));
        }
        return hotelCountriesService.getCountries(country.toLowerCase(), language.toLowerCase());
    }
}
