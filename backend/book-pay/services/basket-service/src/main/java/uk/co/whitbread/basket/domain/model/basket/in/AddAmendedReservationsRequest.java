package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class AddAmendedReservationsRequest implements
    SelfValidation<AddAmendedReservationsRequest> {

  @NotNull
  private String channel;
  @NotNull
  Map<String, String> linkAmendReservations;

  public AddAmendedReservationsRequest(String channel,
      Map<String, String> linkAmendReservations) {
    this.channel = channel;
    this.linkAmendReservations = linkAmendReservations;
    this.validateSelf();
  }
}
