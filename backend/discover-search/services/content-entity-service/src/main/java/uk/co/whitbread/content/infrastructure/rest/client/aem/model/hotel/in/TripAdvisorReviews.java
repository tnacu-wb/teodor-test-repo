package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TripAdvisorReviews {

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
