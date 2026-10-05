package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDetailsDto {

  private String applicationGuid;
  private String status;
  private String campaignCode;
  private int incentiveId;
  private String incentiveCode;
  private LocalDateTime dateReceived;
  private String termsAndConditionsAccepted;
  private String registrationQuestion;
  private String registrationAnswer;
  private LocalDateTime submittedDate;

}