package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class InitializeApplicationRequestDto extends
    ModelValidator<InitializeApplicationRequestDto> {

  @NotEmpty
  private String email;

  private Scheme scheme;
  private String campaignCode;
  private String incentiveCode;

  public InitializeApplicationRequestDto(String email, Scheme scheme,
      String campaignCode,
      String incentiveCode) {
    this.email = email;
    this.scheme = Optional.ofNullable(scheme).isPresent() ? scheme : Scheme.GB;
    this.campaignCode = campaignCode;
    this.incentiveCode = incentiveCode;
    this.validate();
  }

}
