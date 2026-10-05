package uk.co.whitbread.review.infrastructure.rest.client.review.model;

import java.util.List;
import lombok.Data;

@Data
public class TripAdvisorReviews {
  private List<TripAdvisorReview> data;
}
