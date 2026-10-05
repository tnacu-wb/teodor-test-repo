package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAdvisorReview {
  @JsonProperty("published_date")
  private String publishedDate;
  private double rating;
  @JsonProperty("trip_type")
  private String tripType;
  private String title;
  private String text;
  private TripAdvisorUser user;
}