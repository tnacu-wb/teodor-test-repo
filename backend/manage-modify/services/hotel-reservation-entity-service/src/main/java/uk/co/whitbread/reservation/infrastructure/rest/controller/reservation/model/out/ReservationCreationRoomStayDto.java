package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationCreationRoomStayDto {

  private Integer adultsNumber;
  private Integer childrenNumber;
  private Boolean cot;
  private String pmsRoomType;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private MealsIncludedDto mealsIncluded;

}
