package uk.co.whitbread.review.infrastructure.rest.controller.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.review.domain.ports.primary.ReviewInPort;
import uk.co.whitbread.review.infrastructure.rest.client.review.mapper.TripReviewMapper;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.ReviewResponseDto;


@RestController
@RequestMapping("/v1/hotel-review")
@Slf4j
@RequiredArgsConstructor
public class HotelReviewController implements HotelReviewControllerApiDocumentation {

  private final ReviewInPort reviewInPort;
  private final TripReviewMapper tripReviewMapper;

  @Override
  @GetMapping(value = "/reviews/{hotelCode}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ReviewResponseDto> getReviewsForSingleHotel(
      @PathVariable(name = "hotelCode") @NotNull String hotelCode,
      @Valid @RequestParam(name = "lang", defaultValue = "en_US") String lang,
      @RequestParam(name = "limit", defaultValue = "5") Integer limit) {

    log.info("Invoke request getReviewsForSingleHotel for HotelCode : {}", hotelCode);

    Integer updatedLimit = getLimit(limit);
    final var hotelReviewResponse =
        reviewInPort.getReviewResponse(hotelCode.toUpperCase(), lang, updatedLimit);
    ReviewResponseDto reviewResponseDto = tripReviewMapper.toDto(hotelReviewResponse);
    return ResponseEntity.status(HttpStatus.OK).body(reviewResponseDto);
  }


  private static Integer getLimit(Integer limit) {
    if (limit < 1) {
      limit = 1;
    }
    if (limit > 50) {
      limit = 50;
    }
    return limit;
  }

}
