package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomStayByIdDto {

  private Integer adultsNumber;
  private Integer childrenNumber;
  private Boolean cot;
  private String roomType;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private String checkInTime;
  private String checkOutTime;
  private BigDecimal roomPrice;
  private List<RatePerNightDto> ratesPerNight;
  private String cellCode;
  private String roomNumber;
  private String bookingChannel;
  private String sourceCode;
  private String promotionCode;

}
