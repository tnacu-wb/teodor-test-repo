package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class AemCookieContentResponseDto {

  private CookiePoliciesDto cookiePolicies;

  public AemCookieContentResponseDto(CookiePoliciesDto cookiePolicies) {
    this.cookiePolicies = cookiePolicies;
  }

}
