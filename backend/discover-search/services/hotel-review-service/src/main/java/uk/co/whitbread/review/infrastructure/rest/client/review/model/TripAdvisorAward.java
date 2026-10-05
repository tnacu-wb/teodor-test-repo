package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripAdvisorAward {
  @JsonProperty("award_type")
  private String awardType;
  private Integer year;
  private TripAdvisorAwardImages images;
}
