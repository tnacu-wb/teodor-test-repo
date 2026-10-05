package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class GetUserPreferencesRequest extends DomainValidator<GetUserPreferencesRequest> {

  @NotEmpty
  private List<String> tetheredUserGuids;

  public GetUserPreferencesRequest(List<String> tetheredUserGuids) {
    this.tetheredUserGuids = tetheredUserGuids;
    this.validateSelf();
  }
}
