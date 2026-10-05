package uk.co.whitbread.content.infrastructure.rest.client.countries;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.domain.ports.secondary.CountriesOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.countries.aem.adapter.CountriesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.countries.mapper.CountriesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.countries.mapper.CountriesResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class CountriesOutPortImpl implements CountriesOutPort {

  private final CountriesAemClient countriesAemClient;

  private final CountriesRequestMapper countriesRequestMapper;

  private final CountriesResponseMapper countriesResponseMapper;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "CountriesCache")
  public CountriesInformation getCountries(CountriesRequest countriesRequest) {
    var request = countriesRequestMapper.toDto(countriesRequest);

    log.debug("Trying to fetch countries from AEM - reached the out port with request {} {} {}",
        request.getCountry(), request.getLanguage(), request.getSite());

    var countriesAem = countriesAemClient.getCountries(request);

    log.debug("Fetched countries from AEM.");

    return countriesResponseMapper.toModel(countriesAem);
  }
}
