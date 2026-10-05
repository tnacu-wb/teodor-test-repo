package uk.co.whitbread.payapp.domain.model.in.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenClaims {

  private String companyId;
  private String employeeId;
  private String email;
}

