package uk.co.whitbread.review.domain.logic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.domain.ports.secondary.ReviewOutPort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewInPortImplTest {

  @InjectMocks
  private ReviewInPortImpl reviewInPort;
  @Mock
  private ReviewOutPort reviewOutPort;


  @Test
  void testGetReviewResponse() {
    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;

    ReviewResponse reviewResponse =  ReviewResponse.builder().build();
    reviewResponse.setHotelCode(hotelCode);

    // Arrange
    when(reviewOutPort.getTripAdvisorResponse(anyString(), anyString(), any())).thenReturn(reviewResponse);

    // Act
    var response = reviewInPort.getReviewResponse(hotelCode, lang, limit);
    // Assert
    assertThat(response.getHotelCode(), is(hotelCode));
  }


}
