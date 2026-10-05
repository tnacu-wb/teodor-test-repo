package uk.co.whitbread.shared.cdh.model.spending.application;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationParticipant {
  @JsonProperty("Initiator")
  private boolean initiator;

  @JsonProperty("ParticipantId")
  private Integer participantId;

  @JsonProperty("Delegated")
  private boolean delegated;

  @JsonProperty("Terms")
  private boolean terms;

  @JsonProperty("Directdebit")
  private boolean directDebit;

  @JsonProperty("Email")
  private String email;

  @JsonProperty("Shared")
  private String shared;

  @JsonProperty("Name")
  private String name;
}

