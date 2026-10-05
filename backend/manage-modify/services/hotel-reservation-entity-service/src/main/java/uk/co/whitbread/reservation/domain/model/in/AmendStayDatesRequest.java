package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendStayDatesRequest {
  @NotNull
  private String tempBookingRef;
  @NotNull
  private String newStartDate;
  @NotNull
  private String newEndDate;
  @NotNull
  private BookingChannel bookingChannel;
  private String distributionIATANumber;

  private String token;
  private Boolean isOta;
  private List<String> wbRoomTypes;
}
