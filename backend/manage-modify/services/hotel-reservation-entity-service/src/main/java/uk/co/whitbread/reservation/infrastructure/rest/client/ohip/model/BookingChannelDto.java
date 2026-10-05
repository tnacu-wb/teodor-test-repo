package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
