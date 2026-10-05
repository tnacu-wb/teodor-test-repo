package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

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
