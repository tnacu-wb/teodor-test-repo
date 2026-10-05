package uk.co.whitbread.review.domain.ports.primary;


import uk.co.whitbread.review.domain.model.out.ReviewResponse;

public interface ReviewInPort {

  ReviewResponse getReviewResponse(String hotelCode, String lang, Integer limit);
}
