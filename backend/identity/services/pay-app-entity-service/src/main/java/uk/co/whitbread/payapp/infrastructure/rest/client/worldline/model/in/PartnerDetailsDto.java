package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in;

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

  private String dateOfBirth;

  private Integer numberOfPartners;

}
