package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripAdvisor {

  private String rateUpperRange;
  private String rating;
  private String linkUrl;
  private String hotelId;
  private String ratingImageUrl;
  private String tripAdvisorId;
  private String sampleSize;
  private String rateLowerRange;
  private String stepSize;
  private String altText;
}