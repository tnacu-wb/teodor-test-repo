package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;


@Data
public class ReservationListDto {
  private List<ReservationDto> reservationByIdList;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private String policyCode;
  private String hotelId;
  private String currencyCode;
  private String channel;
  private BigDecimal discount;
  private String bookingReference;
  private String companyId;
}
