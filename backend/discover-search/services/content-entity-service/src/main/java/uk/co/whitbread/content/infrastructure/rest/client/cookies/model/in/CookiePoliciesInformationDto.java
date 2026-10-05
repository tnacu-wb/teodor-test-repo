package uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookiePoliciesInformationDto {

  private CookiePoliciesDto cookiePolicies;

}
