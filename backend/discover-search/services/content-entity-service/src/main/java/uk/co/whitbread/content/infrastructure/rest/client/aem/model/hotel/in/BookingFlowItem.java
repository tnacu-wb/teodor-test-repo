package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFlowItem {

  private String rateCode;
  private String rateCategory;
  private String bookingFlowPath;
  private String bookingId;
  private String bookingBusinessId;
}