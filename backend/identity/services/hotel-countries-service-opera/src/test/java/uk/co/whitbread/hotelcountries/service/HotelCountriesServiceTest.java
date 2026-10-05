package uk.co.whitbread.hotelcountries.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotelcountries.client.AEMFeignClient;
import uk.co.whitbread.hotelcountries.config.FeignClientProperties;
import uk.co.whitbread.hotelcountries.config.FeignProperties;
import uk.co.whitbread.hotelcountries.converter.CountriesResponseConverter;
import uk.co.whitbread.hotelcountries.model.aem.AemCountries;

import java.io.IOException;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelCountriesServiceTest {

    private static final String LANGUAGE = "en";
    private static final String COUNTRY = "gb";
    private static final String PATH = "path";

    @Mock
    private FeignProperties feignProperties;

    @Mock
    private AEMFeignClient aemClient;

    @Mock
    private AemCountries aemCountries;

    @Mock
    private CountriesResponseConverter countriesResponseConverter;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private HotelCountriesService hotelCountriesService;


    @BeforeEach
    public void setUp() {
        FeignClientProperties aemProps = new FeignClientProperties();
        aemProps.setResource("path");
        when(feignProperties.getAem()).thenReturn(aemProps);
        when(aemClient.getAemCountries(COUNTRY, LANGUAGE, PATH)).thenReturn(aemCountries);
    }

    @Test
    public void getCountries_succesfullyReturnCountriesList() throws IOException {

        hotelCountriesService.getCountries(COUNTRY, LANGUAGE);

        verify(countriesResponseConverter).convertToCountriesResponse(eq(aemCountries));
    }

    @Test
    public void getCountries_noCountriesReturned() {

        hotelCountriesService.getCountries(COUNTRY, LANGUAGE);

        verify(countriesResponseConverter).convertToCountriesResponse(eq(aemCountries));
    }
}