package uk.co.whitbread.shared.auth.utils;

import java.util.Objects;
import java.util.Optional;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.constants.ClaimNames;

public class JwtParser {

  private JwtParser() {
  }

  public static Account toAccount(Jwt jwt, String namespace) {
    return Account.builder()
        .customerId(jwt.getClaimAsString(namespace + "/" + ClaimNames.CUSTOMER_ACCOUNT_ID))
        .employeeId(jwt.getClaimAsString(namespace + "/" + ClaimNames.EMPLOYEE_ACCOUNT_ID))
        .companyId(jwt.getClaimAsString(namespace + "/" + ClaimNames.COMPANY_ACCOUNT_ID))
        .operaCompanyId(jwt.getClaimAsString(namespace + "/" + ClaimNames.OPERA_COMPANY_ID))
        .email(Optional.ofNullable(jwt.getClaimAsString(namespace + "/" + ClaimNames.EMAIL)).orElse(jwt.getClaimAsString(ClaimNames.EMAIL)))
        .accessLevel(Optional.ofNullable(jwt.getClaimAsMap(ClaimNames.PROFILE))
            .map(profile -> Objects.toString(profile.get(ClaimNames.ACCESS_LEVEL), null))
            .orElse(null))
        .bartId(Optional.ofNullable(jwt.getClaimAsMap(ClaimNames.PROFILE))
            .map(profile -> Objects.toString(profile.get(ClaimNames.COMPANY_ID), null))
            .orElse(null))
        .bartEmployeeId(Optional.ofNullable(jwt.getClaimAsMap(ClaimNames.PROFILE))
            .map(profile -> Objects.toString(profile.get(ClaimNames.EMPLOYEE_ID), null))
            .orElse(null))
        .build();
  }
}
