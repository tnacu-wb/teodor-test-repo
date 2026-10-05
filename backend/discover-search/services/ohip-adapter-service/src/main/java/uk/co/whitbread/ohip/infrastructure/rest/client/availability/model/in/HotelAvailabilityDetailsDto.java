package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelAvailabilityDetailsDto {

  @JsonProperty("hotelAvailability")
  @Valid
  private List<HotelAvailabilityDto> hotelAvailability;

  @JsonProperty("links")
  private LinksDto links;
}
