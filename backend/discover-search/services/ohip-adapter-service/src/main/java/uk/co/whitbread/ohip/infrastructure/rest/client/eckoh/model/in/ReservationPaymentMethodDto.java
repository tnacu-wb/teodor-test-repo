package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.LinksDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPaymentMethodDto {

  @JsonProperty("reservationPaymentMethods")
  private List<ReservationPaymentMethodType> reservationPaymentMethods;

  @JsonProperty("links")
  private LinksDto links;
}
