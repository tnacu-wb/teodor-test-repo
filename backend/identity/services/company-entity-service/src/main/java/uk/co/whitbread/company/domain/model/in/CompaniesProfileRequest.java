package uk.co.whitbread.company.domain.model.in;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.company.domain.model.validation.DomainValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CompaniesProfileRequest extends DomainValidator<CompaniesProfileRequest> {
  @NotEmpty
  String hotelId;
  String arNumber;
  String companyName;
  @Min(1)
  int limit;

  public CompaniesProfileRequest(String hotelId, String arNumber, String companyName, int limit) {
    this.hotelId = hotelId;
    this.arNumber = arNumber;
    this.companyName = companyName;
    this.limit = limit;
    this.validateSelf();
  }
}
