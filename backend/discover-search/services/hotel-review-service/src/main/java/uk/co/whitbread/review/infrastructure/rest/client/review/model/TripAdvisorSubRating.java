package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAdvisorSubRating {
  @JsonProperty("rating_image_url")
  private String ratingImageUrl;
  private String name;
  private Double value;
  @JsonProperty("localized_name")
  private String localizedName;
}
