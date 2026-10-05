package uk.co.whitbread.infrastructure.rest.client.packages.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesRequestOhipDto {

  private String hotelId;
  private String startDate;
  private String endDate;
  private Integer adultsNumber;
  private Integer childrenNumber;
  private Integer nightsNumber;
  private String ratePlanCode;
  private Boolean mealInclusiveRate;
}

