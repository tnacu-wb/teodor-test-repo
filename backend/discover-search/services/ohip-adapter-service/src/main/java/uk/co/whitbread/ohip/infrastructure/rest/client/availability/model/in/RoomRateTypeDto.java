package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RoomRateTypeDto {

  @JsonProperty("total")
  private TotalTypeDto total;
  @JsonProperty("rates")
  private RatesTypeDto rates;
  @JsonProperty("roomType")
  private String roomType;
  @JsonProperty("ratePlanCode")
  private String ratePlanCode;
  @JsonProperty("start")
  private String start;
  @JsonProperty("end")
  private String end;
  @JsonProperty("suppressRate")
  private Boolean suppressRate;
  @JsonProperty("marketCode")
  private String marketCode;
  @JsonProperty("numberOfUnits")
  private Integer numberOfUnits;
  @JsonProperty("ratePlanSet")
  private String ratePlanSet;
  @JsonProperty("promotionCode")
  private String promotionCode;
}
