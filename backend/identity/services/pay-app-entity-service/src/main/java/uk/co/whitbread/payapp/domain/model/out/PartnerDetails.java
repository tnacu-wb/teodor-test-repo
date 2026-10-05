package uk.co.whitbread.payapp.domain.model.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDetails {

  private String title;
  private String foreName;
  private String lastName;
  private LocalDateTime dateOfBirth;
  private int numberOfPartners;

}
