package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;


@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class AddApplicationCardRequest extends DomainValidator<AddApplicationCardRequest> {

  @NotBlank
  private String applicationGuid;
  @NotBlank
  private String applicationId;
  private Scheme scheme;
  private Integer employeeId;

  private AddApplicationCardDetails cardDetails;

  public AddApplicationCardRequest(String applicationGuid, String applicationId, Scheme scheme,
      Integer employeeId, AddApplicationCardDetails cardDetails) {
    this.applicationGuid = applicationGuid;
    this.applicationId = applicationId;
    this.scheme = scheme;
    this.employeeId = employeeId;
    this.cardDetails = cardDetails;
    this.validateSelf();
  }

}
