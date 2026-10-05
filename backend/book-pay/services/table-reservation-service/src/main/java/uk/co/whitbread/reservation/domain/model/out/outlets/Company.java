package uk.co.whitbread.reservation.domain.model.out.outlets;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Company {
  private String id;
  private String name;
  private String termsAndConditions;
  private Consent consent;
  private List<Site> sites;

}
