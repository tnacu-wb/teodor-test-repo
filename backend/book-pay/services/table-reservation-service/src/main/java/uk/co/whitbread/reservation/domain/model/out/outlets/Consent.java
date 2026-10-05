package uk.co.whitbread.reservation.domain.model.out.outlets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Consent {
  private ConsentType email;
  private ConsentType phone;
  private ConsentType sms;
  private ConsentType postal;
  private ConsentType pushNotification;
  private ConsentType profiling;
  private ConsentStatement privacyStatement;
  private ConsentStatement consentStatement;
  private String termsAndConditions;

}
