package uk.co.whitbread.content.domain.model.hotel.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelShortInformationRequest {
  @NotEmpty
  private String country;
  @NotEmpty
  private String language;
}
