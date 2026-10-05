package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAdvisorUser {
  private String username;
  @JsonProperty("user_location")
  private TripAdvisorUserLocation userLocation;
}
