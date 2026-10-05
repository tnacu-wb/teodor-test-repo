package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.countries.in.CountriesRequest;
import uk.co.whitbread.content.domain.model.countries.out.CountriesInformation;

public interface CountriesOutPort {

  CountriesInformation getCountries(CountriesRequest countriesRequest);

}
