package uk.co.whitbread.hotel.info.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.hotel.info.exception.RemoteCallException;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.HotelKeyData;
import uk.co.whitbread.hotel.info.model.domain.Language;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelKeyService {
    private static final String UNABLE_TO_GET_KEY_FOR_S_REASON_S = "Unable to get key for %s. Reason: %s";

    private final HotelInfoService hotelInfoService;
    private final JsonMapper jsonMapper;

    public HotelKeyData getHotelKeyData(Language language, String hotelCode) {
        Country country = chooseCountryBasedOnLanguage(language);

        try {
            String hotelInfoData = hotelInfoService.getHotelInfo(hotelCode, country, language, HotelInfoFormat.SHORT);
            return jsonMapper.readValue(hotelInfoData, HotelKeyData.class);
        } catch (AbstractMALException e) {
            // The error message was previously formatted in the hotel info provider
            throw e;
        } catch (JacksonException e) {
            throw new RemoteCallException(
                    String.format(UNABLE_TO_GET_KEY_FOR_S_REASON_S, hotelCode, "Invalid JSON response.")
            );
        }

    }

    private Country chooseCountryBasedOnLanguage(Language language) {
        switch (language) {
            case DE:
                return Country.DE;
            case EN:
                return Country.GB;
            default:
                return Country.GB;
        }
    }

    public List<HotelKeyData> getHotelKeyData(Language language, List<String> hotelCodes) {

        return Optional.ofNullable(hotelCodes)
                .orElse(Collections.emptyList())
                .parallelStream()
                .map(hotelCode -> getHotelKeyDataIfPresent(language, hotelCode))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Returns the key data of a hotel if it can be found but do not fail if it is missing (returns null).
     *
     * Useful in the context of retrieving the key data of several hotels at once when we do not want
     * the whole operation to fail in case of one hotel missing its key data.
     */
    protected HotelKeyData getHotelKeyDataIfPresent(Language language, String hotelCode) {

        try {

            return getHotelKeyData(
                    language,
                    hotelCode);

        }
        catch(AbstractMALException e) {
            log.error("Could not retrieve the hotel key data for hotel with code {}, call error code {}", hotelCode, e.getErrorCode(), e);
            return null;
        }
    }
}