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
public class UpdateAppContactDetailsRequestDto extends
    ModelValidator<UpdateAppContactDetailsRequestDto> {

  @NotEmpty
  private String applicationGuid;
  @NotEmpty
  private String title;
  @NotEmpty
  private String foreName;
  @NotEmpty
  private String lastName;
  @NotEmpty
  private String position;
  private String telephone;
  private String mobile;
  @NotEmpty
  private String email;

  private Scheme scheme;

  @NotEmpty
  private String applicationId;
  @NotEmpty
  private String resumeUrl;

  @SuppressWarnings("java:S107")
  public UpdateAppContactDetailsRequestDto(String applicationGuid, String title, String foreName,
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
    this.scheme = Optional.ofNullable(scheme).isPresent() ? scheme : Scheme.GB;
    this.applicationId = applicationId;
    this.resumeUrl = resumeUrl;
    this.validate();
  }

}
