package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SubRatingsDto {

  @NotNull
  private String ratingImageUrl;
  @NotNull
  private BigDecimal value;
  @NotNull
  private String localisedName;
}
