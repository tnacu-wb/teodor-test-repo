package uk.co.whitbread.reservation.domain.model.payment.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.reservation.domain.model.in.BusinessItems;

@Data
@Builder
public class Payment {

  @NotEmpty
  private String type;
  @NotEmpty
  private String subType;
  private String environment;
  private Card card;
  private Amount amount;
  private Billing billing;
  private Sca sca;
  private BusinessItems businessItems;
  private Boolean pibaCardPresent;
}
