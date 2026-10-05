package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BusinessSiteDto implements SelfValidation<BusinessSiteDto> {

  @NotEmpty
  private String identifier;
  @NotEmpty
  private String type;
  private String name;
  private String location;

  public BusinessSiteDto(String identifier, String type, String name, String location) {
    this.identifier = identifier;
    this.type = type;
    this.name = name;
    this.location = location;
    this.validateSelf();
  }
}
