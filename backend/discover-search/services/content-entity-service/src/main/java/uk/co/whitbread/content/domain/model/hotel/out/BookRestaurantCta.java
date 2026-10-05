package uk.co.whitbread.content.domain.model.hotel.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRestaurantCta {

  private String bookingCardCtaText;
  private String bookingCardCtaLink;
  private String bookingCardTrackingId;
}
