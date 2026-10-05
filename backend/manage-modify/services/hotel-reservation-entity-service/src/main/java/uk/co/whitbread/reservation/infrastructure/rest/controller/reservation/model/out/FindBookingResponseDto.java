package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindBookingResponseDto {

  private String sourcePms;
  private String cookieName;
  private String ref;
  private String basketReference;
  private String token;
  private String redirectBase;
  private String minutesTillExpiry;
  private String operaConfNumber;
  private String hotelId;
  private String idContext;
}
