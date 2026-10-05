package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class InitializeApplicationRequest extends DomainValidator<InitializeApplicationRequest> {

  @NotEmpty
  String email;

  Scheme scheme;
  String campaignCode;
  String incentiveCode;

  public InitializeApplicationRequest(String email, Scheme scheme, String campaignCode,
      String incentiveCode) {
    this.email = email;
    this.scheme = scheme;
    this.campaignCode = campaignCode;
    this.incentiveCode = incentiveCode;
    this.validateSelf();
  }
}
