package uk.co.whitbread.hotel.info.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.hotel.info.model.aem.AEMHotelBasicDescription;
import uk.co.whitbread.hotel.info.model.domain.Country;
import uk.co.whitbread.hotel.info.model.domain.Language;
import uk.co.whitbread.hotel.info.service.utils.AEMUrlProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PROTECTED;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllHotelsCacheProvider {
    
    @Getter(PROTECTED)
    private Map<String, List<String>> lastSuccessResults = new HashMap<>();

    private final AEMUrlProvider urlProvider;
    private final RestTemplate restTemplate;
    private final JsonMapper jsonMapper;

    private static final Comparator<AEMHotelBasicDescription> AEM_HOTEL_BASIC_DESCRIPTION_CODE_COMPARATOR =
            Comparator.comparing(AEMHotelBasicDescription::getTitle);

    @Cacheable(value = "allHotels", key = "#country + '-' + #language")
    public List<String> getCached(Country country, Language language) {
        return getNonCached(country, language);
    }


    public List<String> getNonCached(Country country, Language language) {

        log.info("Retrieving all hotels for country {} and language {}", country, language);

        try {
            String url = urlProvider.getAllHotelsUrl(country, language);

            AEMHotelBasicDescription[] aemHotels =
                    restTemplate.getForObject(url, AEMHotelBasicDescription[].class);

        /* Convert each element to a String to fit in the model of the endpoint
           which is returning a list of String when a list of hotel codes is passed.
         */
            List<String> result =
                    stream(aemHotels)
                            .distinct()
                            .filter(aemhotel -> Objects.nonNull(aemhotel.getTitle()))
                            .sorted(AEM_HOTEL_BASIC_DESCRIPTION_CODE_COMPARATOR)
                            .map(this::convert)
                            .collect(toList());

            if(!CollectionUtils.isEmpty(result)) {
                setLastSuccessResult(result, country, language);
            }
            return result;
        }
        catch(RuntimeException e) {

            log.error("An error occured while trying to get all hotels from AEM for country {} and language {}.", country, language, e);

            List<String> lastSuccessResult = getLastSuccessResult(country, language);
            if(!CollectionUtils.isEmpty(lastSuccessResult)) {
                log.debug("Returning locally cached last successful result");
                return lastSuccessResult;
            }

            throw e;
        }
    }

    protected String convert(AEMHotelBasicDescription aemHotel) {

        try {
            return jsonMapper.writeValueAsString(aemHotel);
        }
        catch(JacksonException e) {
            log.error("Error while trying to convert eam hotel to string", e);
            throw new RuntimeException(e);
        }

    }

    protected List<String> getLastSuccessResult(Country country, Language language) {

        String key = getLocalCacheKey(country, language);
        List<String> result = getLastSuccessResults().get(key);
        return result;
    }

    protected void setLastSuccessResult(List<String> lastSuccessResult, Country country, Language language) {

        String key = getLocalCacheKey(country, language);
        getLastSuccessResults().put(key, lastSuccessResult);
    }

    protected String getLocalCacheKey(Country country, Language language) {
        return format("%s-%s", country, language);
    }
}
