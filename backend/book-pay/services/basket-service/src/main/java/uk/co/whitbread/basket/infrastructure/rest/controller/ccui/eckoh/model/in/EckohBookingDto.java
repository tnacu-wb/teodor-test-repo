package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

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
public class EckohBookingDto {

  @NotEmpty
  private String type;
  @NotEmpty
  private String language;
  @NotEmpty
  private String journey;
  @NotEmpty
  private String channel;
  @NotNull
  private EckohBusinessSiteDto businessSite;
  @NotNull
  private EckohAgentDto agent;
}
