package uk.co.whitbread.reservation.infrastructure.rest.client.packages.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesRequestDto {

  private String hotelId;
  private String startDate;
  private String endDate;
  private Integer adultsNumber;
  private Integer childrenNumber;
  private Integer nightsNumber;
}
