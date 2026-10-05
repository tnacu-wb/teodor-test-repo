package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StayInfoRequestDto implements Serializable {

  private String arrival;
  private String surname;
}
