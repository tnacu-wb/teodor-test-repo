package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohBusinessSiteDto {
  @NotEmpty
  private String identifier;
  @NotEmpty
  private String type;
  @NotEmpty
  private String name;
  @NotEmpty
  private String location;
  @NotEmpty
  private String country;
}
