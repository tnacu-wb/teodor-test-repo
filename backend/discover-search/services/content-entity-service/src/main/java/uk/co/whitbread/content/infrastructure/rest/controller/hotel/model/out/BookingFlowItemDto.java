package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlowItemDto {

  private String rateCode;
  private String rateCategory;
  private String bookingFlowPath;
  private String bookingId;
  private String bookingIdBB;
}
