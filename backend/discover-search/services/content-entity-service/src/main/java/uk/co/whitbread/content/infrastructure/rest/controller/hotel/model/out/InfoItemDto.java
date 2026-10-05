package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoItemDto {

  private String text;
  private String htmlText;
  private String priority;
  private String startDate;
  private String endDate;
  private boolean hideOnHdp;
  private boolean hideOnBookingFlow;

}
