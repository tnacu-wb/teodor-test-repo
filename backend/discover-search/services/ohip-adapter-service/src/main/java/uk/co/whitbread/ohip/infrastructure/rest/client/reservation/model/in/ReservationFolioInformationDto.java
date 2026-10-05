package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationFolioInformationDto {

  @JsonProperty("folioWindows")
  private List<FolioWindowsDto> folioWindowType;

}