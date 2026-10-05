package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDto {
  private Integer maxRooms;
  private Integer maxRoomsAmend;
  private String page;
  private Integer numberOfNights;
  private String promoCode;
  private Boolean enabled;
}
