package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class CheckInReservationIdListDto {

  private String type;
  private String id;

}
