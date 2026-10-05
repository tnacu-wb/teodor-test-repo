package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class DeletePayApplicationRequest extends DomainValidator<DeletePayApplicationRequest> {
  Scheme scheme;
  @NotEmpty
  String applicationId;
  @NotEmpty
  String applicationGuid;

  public DeletePayApplicationRequest(Scheme scheme, String applicationId, String applicationGuid) {
    this.scheme = scheme;
    this.applicationId = applicationId;
    this.applicationGuid = applicationGuid;
    this.validateSelf();
  }
}
