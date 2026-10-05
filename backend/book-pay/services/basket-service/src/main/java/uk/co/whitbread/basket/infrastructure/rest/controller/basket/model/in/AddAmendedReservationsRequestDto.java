package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class AddAmendedReservationsRequestDto implements
    SelfValidation<AddAmendedReservationsRequestDto> {

  @NotNull
  private String channel;
  @NotNull
  Map<String, String> linkAmendReservations;

  public AddAmendedReservationsRequestDto(String channel,
      Map<String, String> linkAmendReservations) {
    this.channel = channel;
    this.linkAmendReservations = linkAmendReservations;
    this.validateSelf();
  }
}
