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
public class OperaHotelDetails implements Serializable {

  @JsonProperty("code")
  private String code;

  @JsonProperty("description")
  private String description;

  @JsonProperty("category")
  private String category;

  @JsonProperty("sequence")
  private int sequence;

  @JsonProperty("hotelId")
  private String hotelId;

  private boolean onSale = true;

  @JsonProperty("hotelDetailValues")
  private List<OperaHotelDetailsValues> hotelDetailsValues;
}


