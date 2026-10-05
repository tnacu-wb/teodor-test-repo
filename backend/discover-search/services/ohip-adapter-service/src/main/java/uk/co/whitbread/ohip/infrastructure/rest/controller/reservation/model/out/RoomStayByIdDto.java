package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomStayByIdDto {

  private int adults;
  private int children;
  private Boolean cot;
  private String roomType;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private String checkInTime;
  private String checkOutTime;
  private BigDecimal roomPrice;
  private String sourceCode;
  private List<RatePerNightDto> ratesPerNight;
  private String cellCode;
  private String roomNumber;
  private String bookingChannel;
  private String promotionCode;
}
