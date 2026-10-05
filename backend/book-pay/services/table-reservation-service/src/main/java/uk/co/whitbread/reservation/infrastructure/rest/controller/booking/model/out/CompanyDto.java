package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDto {
  private String id;
  private String name;
  private String termsAndConditions;
  private ConsentDto consent;
  private List<SiteDto> sites;

}
