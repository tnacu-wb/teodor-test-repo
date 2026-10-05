package uk.co.whitbread.shared.cdh.model.spending.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationResponse {
  @JsonProperty("ApplicationId")
  private String applicationId;

  @JsonProperty("ApplicationNumber")
  private String applicationNumber;

  @JsonProperty("ApplicationGuid")
  private String applicationGuid;

  @JsonProperty("CompanyId")
  private Integer companyId;

  @JsonProperty("StartedDate")
  private String startedDate;

  @JsonProperty("Scheme")
  private String scheme;

  @JsonProperty("UpdateDate")
  private String updateDate;

  @JsonProperty("AccountName")
  private String accountName;

  @JsonProperty("Stage")
  private String stage;

  @JsonProperty("ResumeUrl")
  private String resumeUrl;

  @JsonProperty("SubmittedDate")
  private String submittedDate;

  @JsonProperty("ActivatedDate")
  private String activatedDate;

  @JsonProperty("Participants")
  private List<ApplicationParticipant> participants;

  @JsonProperty("Cardholders")
  private List<CardHolder> cardHolders;

  @JsonProperty("HostedPageGuid")
  private String hostedPageGuid;

  @JsonProperty("DirectDebitOption")
  private String directDebitOption;

  @JsonProperty("Created")
  private String created;

  @JsonProperty("Modified")
  private String modified;
}
