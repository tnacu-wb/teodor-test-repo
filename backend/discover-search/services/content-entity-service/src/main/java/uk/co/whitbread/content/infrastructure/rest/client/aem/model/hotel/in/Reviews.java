package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.hotel.out.User;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Reviews {

  @NotNull
  private String publishedDate;
  @NotNull
  private BigDecimal rating;
  @NotNull
  private String tripType;
  @NotNull
  private String title;
  @NotNull
  private String text;
  @NotNull
  private User user;
}
