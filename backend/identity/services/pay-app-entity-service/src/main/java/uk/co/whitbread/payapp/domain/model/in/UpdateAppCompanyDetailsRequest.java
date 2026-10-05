package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class UpdateAppCompanyDetailsRequest extends
    DomainValidator<UpdateAppCompanyDetailsRequest> {

  @NotBlank
  String applicationGuid;

  @NotBlank
  String applicationId;

  @NotBlank
  String resumeUrl;

  AppCompanyDetails appCompanyDetails;

  Scheme scheme;

  public UpdateAppCompanyDetailsRequest(String applicationGuid, String applicationId, String resumeUrl,
      AppCompanyDetails appCompanyDetails, Scheme scheme) {
    this.applicationGuid = applicationGuid;
    this.applicationId = applicationId;
    this.resumeUrl = resumeUrl;
    this.appCompanyDetails = appCompanyDetails;
    this.scheme = Optional.ofNullable(scheme).isPresent() ? scheme : Scheme.GB;
    this.validateSelf();
  }

}
