package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.in;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUdfc20Request {
  private Set<String> reservationIds;
  private String hotelId;
  private List<CharacterUdf> udfs;
}