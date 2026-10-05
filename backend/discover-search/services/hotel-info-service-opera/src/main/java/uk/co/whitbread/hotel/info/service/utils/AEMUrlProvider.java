package uk.co.whitbread.hotel.info.service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.info.config.AEMConfiguration;
import uk.co.whitbread.hotel.info.model.domain.BartHotelBrandCode;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.Language;

@Component
@RefreshScope
@RequiredArgsConstructor
public class AEMUrlProvider {

    private final AEMConfiguration aemConfiguration;

    public String getHotelInfoUrl(BartHotelBrandCode brand, Country country, Language language, String hotelCode, HotelInfoFormat format) {
        return aemConfiguration.getUrl()
                .replace("{host}", aemConfiguration.getHost(country, language))
                .replace("/{hotelBrandCode}", (brand == BartHotelBrandCode.PI) ? "" : "/" + brand.name().toLowerCase())
                .replace("{country}", country.name().toLowerCase())
                .replace("{language}", language.name().toLowerCase())
                .replace("{hotelCodeInitial}", hotelCode.substring(0, 1).toUpperCase())
                .replace("{hotelCode}", hotelCode)
                .replace("{hotelInfoFormat}", format.getPrefix());
    }

    public String getAllHotelsUrl(Country country, Language language) {
        return aemConfiguration.getAllHotelsUrl()
                .replace("{host}", aemConfiguration.getHost(country, language))
                .replace("{country}", country.name().toLowerCase())
                .replace("{language}", language.name().toLowerCase());
    }

}
