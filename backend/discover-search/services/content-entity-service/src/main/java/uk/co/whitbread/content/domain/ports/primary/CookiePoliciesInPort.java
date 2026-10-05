package uk.co.whitbread.content.domain.ports.primary;

import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;

public interface CookiePoliciesInPort {

  CookiePoliciesInformation getCookiePolicies(CookiePoliciesRequest cookiePoliciesRequest);

}
