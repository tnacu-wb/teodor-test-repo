package uk.co.whitbread.content.domain.model.index.header.data.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Autocomplete {

  private String managedPlacesSize;
  private Boolean enabled;
  private String domain;
  private String method;
  private String googlePlacesSize;
  private String debounce;
  private String defaultResponseLength;
  private List<CountryRestriction> countryRestrictions;
  private String googlePlaces;
}
