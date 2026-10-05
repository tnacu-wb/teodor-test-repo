package uk.co.whitbread.hotel.info.cache;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.hotel.info.config.HotelConfiguration;
import uk.co.whitbread.hotel.info.exception.AemInternalError;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.Language;
import uk.co.whitbread.hotel.info.service.utils.AEMUrlProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotelInfoCacheProvider {

    private static final String UNABLE_TO_GET_INFO_FOR_S_REASON_S = "Unable to get info for %s. Reason: %s";

    private final AEMUrlProvider urlProvider;
    private final RestTemplate restTemplate;
    private final HotelConfiguration hotelConfiguration;

    @Cacheable(value = "hotelInfo", keyGenerator = "infoKeyGenerator")
    public String getCached(String hotelCode, Country country, Language language, HotelInfoFormat format) {
        return getNonCached(hotelCode, country, language, format);
    }

    public String getNonCached(String hotelCode, Country country, Language language, HotelInfoFormat format) {
        log.info("Retrieving hotel info for hotel {} and format {}", hotelCode, format);

        BartHotelBrandCode brand = determineBrandAgainstConfiguration(hotelCode);
        String url = urlProvider.getHotelInfoUrl(brand, country, language, hotelCode, format);

        try {
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RemoteCallException(
                        String.format(UNABLE_TO_GET_INFO_FOR_S_REASON_S, hotelCode, exception.getMessage()),
                        exception
                );
            }
            throw new AemInternalError(
                    String.format(UNABLE_TO_GET_INFO_FOR_S_REASON_S, hotelCode, exception.getMessage()),
                    exception
            );
        } catch (RestClientException exception) {
            throw new AemInternalError(
                    String.format(UNABLE_TO_GET_INFO_FOR_S_REASON_S, hotelCode, exception.getMessage()),
                    exception
            );
        }
    }

    private BartHotelBrandCode determineBrandAgainstConfiguration(String hotelCode) {
        return hotelConfiguration.getHubHotelCodes().contains(hotelCode) ? BartHotelBrandCode.HUB : BartHotelBrandCode.PI;
    }
}
