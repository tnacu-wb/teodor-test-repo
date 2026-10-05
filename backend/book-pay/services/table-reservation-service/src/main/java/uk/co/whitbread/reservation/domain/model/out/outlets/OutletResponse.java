package uk.co.whitbread.reservation.domain.model.out.outlets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutletResponse {

  private List<Company> companies;

  public OutletResponse(List<Company> companies) {
    this.companies = companies;
  }

}
