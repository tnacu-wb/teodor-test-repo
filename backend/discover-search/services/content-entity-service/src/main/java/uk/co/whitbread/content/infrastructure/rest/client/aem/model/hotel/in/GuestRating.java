package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestRating {

  private String rateUpperRange;
  private String rating;
  private String sampleSize;
  private String rateLowerRange;
  private String stepSize;
  private String likelihoodToRecommend;
}