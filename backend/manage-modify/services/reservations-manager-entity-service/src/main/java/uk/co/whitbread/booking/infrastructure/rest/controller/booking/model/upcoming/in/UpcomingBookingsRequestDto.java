package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingBookingsRequestDto {

  @NotNull
  private String language;
  @NotNull
  private String country;
  private String channel;
  private String subchannel;
}
