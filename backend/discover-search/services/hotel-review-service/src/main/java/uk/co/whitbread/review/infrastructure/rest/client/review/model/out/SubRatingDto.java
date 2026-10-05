package uk.co.whitbread.review.infrastructure.rest.client.review.model.out;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubRatingDto implements Serializable {
  private String ratingImageUrl;
  private Double value;
  private String localisedName;
}