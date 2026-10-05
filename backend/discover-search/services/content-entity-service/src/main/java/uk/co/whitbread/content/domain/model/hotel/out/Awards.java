package uk.co.whitbread.content.domain.model.hotel.out;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Awards {

  @NotNull
  private String awardType;
  @NotNull
  private String year;
  @NotNull
  private String image;
}
