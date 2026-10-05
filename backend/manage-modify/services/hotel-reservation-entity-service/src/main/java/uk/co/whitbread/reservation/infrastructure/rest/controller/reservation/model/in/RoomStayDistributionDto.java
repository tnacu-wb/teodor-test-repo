package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomStayDistributionDto {

  @NotEmpty
  private String arrivalDate;
  @NotEmpty
  private String departureDate;
  private int adultsNumber;
  private int childrenNumber;
  private String roomType;
  private String operaRoomType;
  private String ratePlanCode;
  private boolean cot;
  private List<String> specialRequests;

}
