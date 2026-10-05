package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;

public interface CookiePoliciesOutPort {

  CookiePoliciesInformation getCookiePolicies(CookiePoliciesRequest cookiePoliciesRequest);

}
