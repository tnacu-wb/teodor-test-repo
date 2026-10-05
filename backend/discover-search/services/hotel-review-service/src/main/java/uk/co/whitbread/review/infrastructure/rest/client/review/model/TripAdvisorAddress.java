package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAdvisorAddress {
  @JsonProperty("address_string")
  private String addressString;
}
