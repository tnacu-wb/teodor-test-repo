package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class UpdateAppContactDetailsRequest extends DomainValidator<UpdateAppContactDetailsRequest> {

  @NotEmpty
  String applicationGuid;
  @NotEmpty
  String title;
  @NotEmpty
  String foreName;
  @NotEmpty
  String lastName;
  @NotEmpty
  String position;
  String telephone;
  String mobile;
  @NotEmpty
  String email;

  Scheme scheme;

  @NotEmpty
  String applicationId;
  @NotEmpty
  String resumeUrl;

  @SuppressWarnings("java:S107")
  public UpdateAppContactDetailsRequest(String applicationGuid, String title, String foreName,
      String lastName, String position, String telephone, String mobile, String email,
      Scheme scheme, String applicationId, String resumeUrl) {
    this.applicationGuid = applicationGuid;
    this.title = title;
    this.foreName = foreName;
    this.lastName = lastName;
    this.position = position;
    this.telephone = telephone;
    this.mobile = mobile;
    this.email = email;
    this.scheme = scheme;
    this.applicationId = applicationId;
    this.resumeUrl = resumeUrl;
    this.validateSelf();
  }
}
