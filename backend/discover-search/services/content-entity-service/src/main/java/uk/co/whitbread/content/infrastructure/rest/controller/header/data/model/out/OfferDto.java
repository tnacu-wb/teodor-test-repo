package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
  private Integer maxRooms;
  private String cellCode;
  private String page;
  private Integer numberOfNights;
  @JsonProperty("corporateId")
  private String corpId;
  private String ratePlanCode;
}
