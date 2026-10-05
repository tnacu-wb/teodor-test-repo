package uk.co.whitbread.content.infrastructure.rest.client.booking.model.out;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateInformationRequestAemDto {

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  @NotEmpty
  private String brand;

  @NotEmpty
  private List<String> ratePlans;

  private String hotelId;

  private String channel;
}
