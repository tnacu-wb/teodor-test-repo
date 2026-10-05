package uk.co.whitbread.reservation.domain.model.payment.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessSite {

  @NotEmpty
  private String identifier;
  private String name;
  @NotEmpty
  private String type;
  private String location;

}
