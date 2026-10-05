package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class SubmitApplicationRequest extends DomainValidator<SubmitApplicationRequest> {

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
  public SubmitApplicationRequest(String applicationGuid, String applicationId, Scheme scheme,
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
    this.validateSelf();
  }
}
