package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateAppCompanyDetailsRequestDto extends ModelValidator<UpdateAppCompanyDetailsRequestDto> {

  @NotBlank
  private String applicationGuid;

  @NotBlank
  private String applicationId;

  @NotBlank
  private String resumeUrl;

  private AppCompanyDetailsDto appCompanyDetailsDto;

  private Scheme scheme;

  public UpdateAppCompanyDetailsRequestDto(String applicationGuid, String applicationId, String resumeUrl,
      AppCompanyDetailsDto appCompanyDetailsDto, Scheme scheme) {
    this.applicationGuid = applicationGuid;
    this.applicationId = applicationId;
    this.resumeUrl = resumeUrl;
    this.appCompanyDetailsDto = appCompanyDetailsDto;
    this.scheme = Optional.ofNullable(scheme).isPresent() ? scheme : Scheme.GB;
    this.validate();
  }

}
