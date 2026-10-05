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
public class EckohAgentDto {

  @NotEmpty
  private String email;
  @NotEmpty
  private String name;
}
