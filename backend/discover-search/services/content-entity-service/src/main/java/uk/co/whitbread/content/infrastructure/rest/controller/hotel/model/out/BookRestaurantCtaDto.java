package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRestaurantCtaDto {

  private String bookingCardCtaText;
  private String bookingCardCtaLink;
  private String bookingCardTrackingId;
}
