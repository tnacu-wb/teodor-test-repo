package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsentDto {
  private ConsentTypeDto email;
  private ConsentTypeDto phone;
  private ConsentTypeDto sms;
  private ConsentTypeDto postal;
  private ConsentTypeDto pushNotification;
  private ConsentTypeDto profiling;
  private ConsentStatementDto privacyStatement;
  private ConsentStatementDto consentStatement;
  private String termsAndConditions;

}
