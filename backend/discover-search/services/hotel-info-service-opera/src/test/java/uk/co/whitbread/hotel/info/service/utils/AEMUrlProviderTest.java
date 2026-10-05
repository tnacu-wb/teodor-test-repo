package uk.co.whitbread.hotel.info.service.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.info.config.AEMConfiguration;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.Language;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.info.model.domain.Country.GB;
import static uk.co.whitbread.hotel.info.model.domain.Language.DE;
import static uk.co.whitbread.hotel.info.model.domain.Language.EN;

public class AEMUrlProviderTest {

    private static final String AEM_URL =
            "http://{host}/{country}/{language}/hoteldirectory/{hotelBrandCode}/{hotelCodeInitial}/{hotelCode}.{hotelInfoFormat}.data";

    private static final String AEM_ALL_HOTELS_URL =
            "http://{host}/{country}/{language}/hoteldirectory/list.hotels.data";

    private AEMUrlProvider sut;

    @BeforeEach
    public void setUp() {
        AEMConfiguration aemConfiguration = mock(AEMConfiguration.class);
        when(aemConfiguration.getUrl()).thenReturn(AEM_URL);
        when(aemConfiguration.getAllHotelsUrl()).thenReturn(AEM_ALL_HOTELS_URL);
        when(aemConfiguration.getHost(GB, EN)).thenReturn("GB_host");
        when(aemConfiguration.getHost(Country.DE, DE)).thenReturn("DE_host");
        sut = new AEMUrlProvider(aemConfiguration);
    }

    @Test
    public void should_get_GB_hub_complete_info() {
        //Given
        BartHotelBrandCode brand = BartHotelBrandCode.HUB;
        Country country = GB;
        Language language = EN;
        String hotelCode = "abcd";
        HotelInfoFormat format = HotelInfoFormat.LONG;

        //When
        String url = sut.getHotelInfoUrl(brand, country, language, hotelCode, format);

        //Then
        final String expected = "http://GB_host/gb/en/hoteldirectory/hub/A/abcd.complete.data";
        assertThat(url, is(expected));
    }

    @Test
    public void should_get_GB_pi_short_info() {
        //Given
        BartHotelBrandCode brand = BartHotelBrandCode.PI;
        Country country = GB;
        Language language = EN;
        String hotelCode = "abcd";
        HotelInfoFormat format = HotelInfoFormat.SHORT;

        //When
        String url = sut.getHotelInfoUrl(brand, country, language, hotelCode, format);

        //Then
        final String expected = "http://GB_host/gb/en/hoteldirectory/A/abcd.summary.data";
        assertThat(url, is(expected));
    }

    @Test
    public void should_get_DE_pi_complete_info() {
        //Given
        BartHotelBrandCode brand = BartHotelBrandCode.PI;
        Country country = Country.DE;
        Language language = DE;
        String hotelCode = "abcd";
        HotelInfoFormat format = HotelInfoFormat.LONG;

        //When
        String url = sut.getHotelInfoUrl(brand, country, language, hotelCode, format);

        //Then
        final String expected = "http://DE_host/de/de/hoteldirectory/A/abcd.complete.data";
        assertThat(url, is(expected));
    }

    @Test
    public void should_get_all_hotels_url_GB() {

        String result = sut.getAllHotelsUrl(GB, EN);
        assertThat(result, is("http://GB_host/gb/en/hoteldirectory/list.hotels.data"));
    }

    @Test
    public void should_get_all_hotels_url_DE() {

        String result = sut.getAllHotelsUrl(Country.DE, DE);
        assertThat(result, is("http://DE_host/de/de/hoteldirectory/list.hotels.data"));
    }
}