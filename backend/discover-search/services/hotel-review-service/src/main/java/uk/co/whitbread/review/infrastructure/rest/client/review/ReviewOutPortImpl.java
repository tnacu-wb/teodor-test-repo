package uk.co.whitbread.review.infrastructure.rest.client.review;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.domain.ports.secondary.ReviewOutPort;
import uk.co.whitbread.review.infrastructure.rest.client.review.mapper.ReviewResponseMapper;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorData;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReviews;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@AllArgsConstructor
public class ReviewOutPortImpl implements ReviewOutPort {

  private final TripAdvisorClient tripAdvisorClient;
  private final ConcurrentTracer concurrentTracer;
  private final ReviewResponseMapper reviewResponseMapper;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1Day",
      value = "HotelInformationBySlugTripAdvisorCache")
  public ReviewResponse getTripAdvisorResponse(String hotelCode, String lang, Integer limit) {
    log.debug("Invoking OutPort getTripAdvisorData hotelCode {} : lang {} : limit {} :", hotelCode, lang, limit);
    var tripAdvisorData = CompletableFuture.supplyAsync(
            concurrentTracer.wrap((Supplier<TripAdvisorData>)
                    () -> tripAdvisorClient.getTripAdvisorData(hotelCode, lang, limit)));
    var tripAdvisorReview = CompletableFuture.supplyAsync(
            concurrentTracer.wrap((Supplier<TripAdvisorReviews>)
                    () -> tripAdvisorClient.getTripAdvisorReviews(hotelCode, lang, limit)));
    return CompletableFuture.allOf(tripAdvisorData, tripAdvisorReview)
            .thenApply(result -> {
              Objects.requireNonNull(tripAdvisorData.join());
              Objects.requireNonNull(tripAdvisorReview.join());
              return reviewResponseMapper.toReviewResponseDto(tripAdvisorData.join(), tripAdvisorReview.join(),
                      hotelCode);
            }).join();
  }

}

