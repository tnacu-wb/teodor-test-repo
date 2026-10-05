package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.hotel.out.Awards;
import uk.co.whitbread.content.domain.model.hotel.out.Reviews;
import uk.co.whitbread.content.domain.model.hotel.out.SubRatings;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TripAdvisorReviewsDto {

  @NotNull
  private List<@NotNull Awards> awards;
  @NotNull
  private BigDecimal rating;
  @NotNull
  private List<@NotNull SubRatings> subRatings;
  @NotNull
  private String name;
  @NotNull
  private String address;
  @NotNull
  private List<@NotNull Reviews> reviews;
  @NotNull
  private Integer numberOfReviews;
  @NotNull
  private String writeReview;
  @NotNull
  private String webUrl;
  @NotNull
  private String locationId;
  @NotNull
  private String hotelCode;
}
