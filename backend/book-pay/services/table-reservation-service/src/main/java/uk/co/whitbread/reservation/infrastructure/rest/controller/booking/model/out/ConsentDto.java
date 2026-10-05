package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsentDto {
  private boolean email;
  private boolean phone;
  private boolean sms;
  private boolean postal;
  private boolean pushNotification;
  private boolean profiling;
  private boolean privacyStatement;
  private boolean consentStatement;
  private boolean termsAndConditions;

}
