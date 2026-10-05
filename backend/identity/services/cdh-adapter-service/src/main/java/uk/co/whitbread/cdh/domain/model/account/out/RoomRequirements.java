package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequirements {

  @JsonProperty("Adults")
  private Integer adults;
  @JsonProperty("Children")
  private Integer children;
  @JsonProperty("CotRequired")
  private boolean cotRequired;
  @JsonProperty("HotelBrand")
  private String hotelBrand;
  @JsonProperty("LettingType")
  private String lettingType;
  @JsonProperty("Type")
  private String type;

}
