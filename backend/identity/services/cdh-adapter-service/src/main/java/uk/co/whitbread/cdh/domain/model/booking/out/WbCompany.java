package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WbCompany {

  @JsonProperty("Name")
  private String name;

  @JsonProperty("LegalOwnerText")
  private List<String> legalOwnerText;

  @JsonProperty("RegNumber")
  private String regNumber;

  @JsonProperty("VatRegNumber")
  private String vatRegNumber;
}

