package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCodePricingRequestDto {

  private String arrivalDate;

  private String departureDate;

  private String ratePlanCode;

  private List<String> roomTypes;

  private List<Integer> adultsNo;

  private List<Integer> childrenNo;

  private String reservationRatePlanCode;
}
