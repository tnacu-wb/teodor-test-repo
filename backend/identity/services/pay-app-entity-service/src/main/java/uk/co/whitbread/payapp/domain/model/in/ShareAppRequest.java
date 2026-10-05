package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class ShareAppRequest extends DomainValidator<ShareAppRequest> {

  @NotBlank
  String applicationId;

  @NotBlank
  String applicationGuid;

  @NotNull
  int employeeId;

  @NotBlank
  String email;

  @NotBlank
  String fullName;

  public ShareAppRequest(String applicationId, String applicationGuid, int employeeId, String email,
      String fullName) {
    this.applicationId = applicationId;
    this.applicationGuid = applicationGuid;
    this.employeeId = employeeId;
    this.email = email;
    this.fullName = fullName;
    this.validateSelf();
  }
}
