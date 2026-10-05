package uk.co.whitbread.hotel.card.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldlineCardHolderUserRequest {

  @JsonProperty("Title")
  private String title;
  @JsonProperty("Forename")
  private String forename;
  @JsonProperty("LastName")
  private String lastName;
  @JsonProperty("EmailAddress")
  private String emailAddress;
  @JsonProperty("Mobile")
  private String mobile;
  @JsonProperty("IsConsentGiven")
  private boolean isConsentGiven;
}
