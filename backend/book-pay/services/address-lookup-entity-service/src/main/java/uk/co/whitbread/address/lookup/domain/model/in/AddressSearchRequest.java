package uk.co.whitbread.address.lookup.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.address.lookup.domain.model.validation.SelfValidation;

@Data
@Builder
public class AddressSearchRequest implements SelfValidation<AddressSearchRequest> {

  @NotEmpty
  private String searchTerm;
  private String countryCode;

  public AddressSearchRequest(String searchTerm, String countryCode) {
    this.searchTerm = searchTerm;
    this.countryCode = countryCode;
    this.validateSelf();
  }
}
