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
public class PartnerDetails extends DomainValidator<PartnerDetails> {

  @NotBlank
  String title;

  @NotBlank
  String foreName;

  @NotBlank
  String lastName;

  @NotBlank
  String dateOfBirth;

  @NotNull
  Integer numberOfPartners;

  public PartnerDetails(String title, String foreName, String lastName, String dateOfBirth,
      Integer numberOfPartners) {
    this.title = title;
    this.foreName = foreName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.numberOfPartners = numberOfPartners;
    this.validateSelf();
  }

}
