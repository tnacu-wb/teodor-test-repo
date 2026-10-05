package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
