package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;

public interface CountriesInPort {

  CountriesInformation getCountries(CountriesRequest countriesRequest);

}
