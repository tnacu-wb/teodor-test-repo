package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CookiePoliciesDto {

  private String version;
  private String brand;
  private IntroViewDto introView;
  private ManageViewDto manageView;

}