package uk.co.whitbread.basket.domain.model.payments.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class BusinessSite implements SelfValidation<BusinessSite> {

  @NotEmpty
  private String identifier;
  private String name;
  @NotEmpty
  private String type;
  private String location;

  public BusinessSite(String identifier, String name, String type, String location) {
    this.identifier = identifier;
    this.name = name;
    this.type = type;
    this.location = location;
    this.validateSelf();
  }
}
