package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomStayTypeDto {

  @JsonProperty("roomRates")
  @Valid
  private List<RoomRateTypeDto> roomRates;

}
