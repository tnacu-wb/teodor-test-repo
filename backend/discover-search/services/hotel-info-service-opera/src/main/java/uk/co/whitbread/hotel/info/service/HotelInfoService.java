package uk.co.whitbread.hotel.info.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.hotel.info.cache.AllHotelsCacheProvider;
import uk.co.whitbread.hotel.info.cache.HotelInfoCacheProvider;
import uk.co.whitbread.hotel.info.command.CacheFallbackCommand;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.HotelInfoFormat;
import uk.co.whitbread.hotel.info.model.domain.Language;
import uk.co.whitbread.hotel.info.service.utils.OptionalFuture;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelInfoService {

    private final AllHotelsCacheProvider allHotelsCacheProvider;
    private final HotelInfoCacheProvider hotelInfoCacheProvider;
    @Qualifier("aemExecutor")
    private final Executor executor;

    public String getHotelInfo(String hotelCode, Country country, Language language, HotelInfoFormat format) {

        return CacheFallbackCommand.execute("CacheGroup", "hotelinfo", "hotelinfo",
                () -> hotelInfoCacheProvider.getCached(hotelCode, country, language, format),
                () -> hotelInfoCacheProvider.getNonCached(hotelCode, country, language, format));

    }

    public List<String> getHotelInfo(List<String> hotelCodes, Country country, Language language, HotelInfoFormat hotelInfoFormat) {

        final List<OptionalFuture<String>> futureList = Optional.ofNullable(hotelCodes)
                .orElse(Collections.emptyList())
                .stream()
                .map(key -> new OptionalFuture<>(CompletableFuture
                        .supplyAsync(() -> getHotelInfoIfPresent(country, language, hotelInfoFormat, key), executor)))
                .collect(toList());

        return futureList
                .stream()
                .map(OptionalFuture::finishAndGet)
                .map(s -> s.orElse(null))
                .filter(Objects::nonNull)
                .collect(toList());
    }


    public List<String> getAllHotels(Country country, Language language) {

        return CacheFallbackCommand.execute("CacheGroup", "allhotels", "allhotels",
                () -> allHotelsCacheProvider.getCached(country, language),
                () -> allHotelsCacheProvider.getNonCached(country, language));
    }

    /**
     * Returns the information of a hotel if it can be found but do not fail if it is missing (returns null).
     *
     * Useful in the context of retrieving the information of several hotels at once when we do not want
     * the whole operation to fail in case of one hotel missing information.
     */
    protected String getHotelInfoIfPresent(Country country, Language language, HotelInfoFormat hotelInfoFormat, String key) {
        try {
            return getHotelInfo(
                    key,
                    country,
                    language,
                    hotelInfoFormat);
        }
        catch(AbstractMALException e) {
            log.error("Could not retrieve the hotel information for hotel with code {}, call error code {}", key, e.getErrorCode(), e);
            return null;
        }
    }

}
