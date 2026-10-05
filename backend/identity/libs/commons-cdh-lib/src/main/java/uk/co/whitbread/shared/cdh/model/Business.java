package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class Business {

  @JsonProperty("DismissMPILink")
  private boolean dismissMPILink;

  @JsonProperty("MyPILink")
  private String myPILink;

  @JsonProperty("MiSetupRequired")
  private boolean miSetupRequired;

  @JsonProperty("Tethered")
  private boolean tethered;
}
