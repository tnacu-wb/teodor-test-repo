package uk.co.whitbread.ohip.domain.model.opera.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OperaHotelDetailsList implements Serializable {

  @JsonProperty("hotelDetails")
  private List<OperaHotelDetails> hotelDetails;
}
