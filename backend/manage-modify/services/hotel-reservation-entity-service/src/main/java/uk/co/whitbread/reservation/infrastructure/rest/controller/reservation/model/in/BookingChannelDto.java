package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BookingChannelDto {

  @NotEmpty
  @Schema(example = "PI", required = true)
  private String channel;

  @NotEmpty
  @Schema(example = "WEB", required = true)
  private String subchannel;

  @Schema(example = "EN", required = true)
  private String language;
}