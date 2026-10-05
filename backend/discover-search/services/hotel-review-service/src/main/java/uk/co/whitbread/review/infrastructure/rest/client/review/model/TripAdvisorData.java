package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class TripAdvisorData {
  private String name;
  private List<TripAdvisorAward> awards;
  @JsonProperty("address_obj")
  private TripAdvisorAddress addressObj;
  @JsonProperty("num_reviews")
  private Integer numReviews;
  @JsonProperty("write_review")
  private String writeReview;
  @JsonProperty("web_url")
  private String webUrl;
  private Double rating;
  private List<TripAdvisorSubRating> subratings;
  @JsonProperty("location_id")
  private String locationId;
}