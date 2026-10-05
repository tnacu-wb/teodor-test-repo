package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitApplicationRequestDetailsDto {

  @JsonProperty("RegistrationQuestion")
  private String registrationQuestion;

  @JsonProperty("RegistrationAnswer")
  private String registrationAnswer;

  @JsonProperty("HotelBookingRole")
  private String hotelBookingRole;

  @JsonProperty("TermsAndConditionAccepted")
  private String termsAndConditionAccepted;

  @JsonProperty("HostedPageGuid")
  private String hostedPageGuid;

  @JsonProperty("ApplicationGuid")
  private String applicationGuid;

  @JsonProperty("IsDirectDebit")
  private boolean isDirectDebit;

}
