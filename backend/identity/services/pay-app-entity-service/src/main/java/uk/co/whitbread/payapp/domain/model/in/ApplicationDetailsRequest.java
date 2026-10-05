package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class ApplicationDetailsRequest extends DomainValidator<ApplicationDetailsRequest> {

  @NotBlank
  private String applicationId;

  @NotBlank
  private String applicationGuid;

  private Scheme scheme;

  public ApplicationDetailsRequest(String applicationId, String applicationGuid, Scheme scheme) {
    this.applicationId = applicationId;
    this.applicationGuid = applicationGuid;
    this.scheme = scheme;
    this.validateSelf();
  }


}
