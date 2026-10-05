package uk.co.whitbread.review.domain.ports.secondary;

import uk.co.whitbread.review.domain.model.out.ReviewResponse;

public interface ReviewOutPort {

  ReviewResponse getTripAdvisorResponse(String hotelCode, String lang, Integer limit);

}
