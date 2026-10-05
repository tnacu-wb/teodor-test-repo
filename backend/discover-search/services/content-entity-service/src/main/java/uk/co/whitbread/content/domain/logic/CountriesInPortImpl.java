package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;
import uk.co.whitbread.content.domain.ports.primary.CountriesInPort;
import uk.co.whitbread.content.domain.ports.secondary.CountriesOutPort;

@Slf4j
@RequiredArgsConstructor
public class CountriesInPortImpl implements CountriesInPort {

  private final CountriesOutPort countriesOutPort;

  @Override
  public CountriesInformation getCountries(CountriesRequest countriesRequest) {
    return countriesOutPort.getCountries(countriesRequest);
  }
}
