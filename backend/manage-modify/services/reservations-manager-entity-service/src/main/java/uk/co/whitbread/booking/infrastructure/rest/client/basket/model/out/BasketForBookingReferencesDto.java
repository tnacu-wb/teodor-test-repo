package uk.co.whitbread.booking.infrastructure.rest.client.basket.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BasketForBookingReferencesDto {
  private BasketStatus status;
  private String bookingReference;
  private List<BasketItemDto> items;
}
