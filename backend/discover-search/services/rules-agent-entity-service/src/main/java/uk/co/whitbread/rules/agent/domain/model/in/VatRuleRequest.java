package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class VatRuleRequest extends ModelValidator<VatRuleRequest> {

  @NotEmpty
  String vatRegion;
  @NotEmpty
  List<String> pkgCodeArr;

  public VatRuleRequest(String vatRegion,
      List<String> pkgCodeArr) {
    this.vatRegion = vatRegion;
    this.pkgCodeArr = pkgCodeArr;
    this.validateSelf();
  }

}
