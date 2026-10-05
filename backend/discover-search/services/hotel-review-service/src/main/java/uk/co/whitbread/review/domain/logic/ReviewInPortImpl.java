package uk.co.whitbread.review.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.domain.ports.primary.ReviewInPort;
import uk.co.whitbread.review.domain.ports.secondary.ReviewOutPort;

@Slf4j
@RequiredArgsConstructor
public class ReviewInPortImpl implements ReviewInPort {

  private final ReviewOutPort reviewOutPort;

  @Override
  public ReviewResponse getReviewResponse(String hotelCode, String lang, Integer limit) {
    log.debug("Invoked ReviewInPortImpl getReviewResponse : hotelCode={}, lang={}, limit={}", hotelCode, lang, limit);
    return reviewOutPort.getTripAdvisorResponse(hotelCode, lang, limit);
  }
}
