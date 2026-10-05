package uk.co.whitbread.content.infrastructure.rest.controller.booking.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateInformationRequestDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String brand;

  private List<String> ratePlans;

  @NotEmpty
  private String hotelId;

  private String channel;

}
