package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStayByIdResponse {

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
  private String sourceCode;
  private List<RatePerNight> ratesPerNight;
  private String cellCode;
  private String roomNumber;
  private String bookingChannel;
  private String promotionCode;
}