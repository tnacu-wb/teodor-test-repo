package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
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
public class SubmitApplicationRequestDto extends ModelValidator<SubmitApplicationRequestDto> {

  @NotBlank
  private String applicationGuid;
  @NotBlank
  private String applicationId;
  private Scheme scheme;
  private String hostedPageGuid;
  @NotBlank
  private String registrationQuestion;
  @NotBlank
  private String registrationAnswer;
  @NotBlank
  private String termsAndConditionAccepted;
  private Boolean isDirectDebit;

  @SuppressWarnings("java:S107")
  public SubmitApplicationRequestDto(String applicationGuid, String applicationId, Scheme scheme,
      String hostedPageGuid, String registrationQuestion, String registrationAnswer,
      String termsAndConditionAccepted, Boolean isDirectDebit) {
    this.applicationGuid = applicationGuid;
    this.applicationId = applicationId;
    this.scheme = scheme;
    this.hostedPageGuid = hostedPageGuid;
    this.registrationQuestion = registrationQuestion;
    this.registrationAnswer = registrationAnswer;
    this.termsAndConditionAccepted = termsAndConditionAccepted;
    this.isDirectDebit = isDirectDebit;
    this.validate();
  }
}
