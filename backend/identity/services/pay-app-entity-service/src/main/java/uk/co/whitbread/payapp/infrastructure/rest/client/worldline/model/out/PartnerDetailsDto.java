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
public class PartnerDetailsDto {

  private String title;
  private String foreName;
  private String lastName;
  private LocalDateTime dateOfBirth;
  private int numberOfPartners;

}
