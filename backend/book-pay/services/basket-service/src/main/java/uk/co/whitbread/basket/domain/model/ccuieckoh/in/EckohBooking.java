package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohBooking {

  private EckohAgent agent;
  @NotEmpty
  private String type;
  private String language;
  @NotEmpty
  private String journey;
  @NotEmpty
  private String channel;
  @NotNull
  private EckohBusinessSite businessSite;
}
