package uk.co.whitbread.reservation.domain.model.out.events;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Consent {

  private boolean email;
  private boolean phone;
  private boolean sms;
  private boolean postal;
  private boolean pushNotification;
  private boolean profiling;
  private boolean privacyStatement;

  @JsonInclude(JsonInclude.Include.NON_DEFAULT)
  private boolean consentStatement;
  private boolean termsAndConditions;

}
