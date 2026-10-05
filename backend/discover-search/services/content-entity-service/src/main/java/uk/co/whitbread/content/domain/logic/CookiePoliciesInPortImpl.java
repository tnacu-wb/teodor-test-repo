package uk.co.whitbread.content.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;
import uk.co.whitbread.content.domain.ports.primary.CookiePoliciesInPort;
import uk.co.whitbread.content.domain.ports.secondary.CookiePoliciesOutPort;

@Slf4j
@RequiredArgsConstructor
public class CookiePoliciesInPortImpl implements CookiePoliciesInPort {

  private final CookiePoliciesOutPort cookiePoliciesOutPort;

  @Override
  public CookiePoliciesInformation getCookiePolicies(CookiePoliciesRequest cookiePoliciesRequest) {
    return cookiePoliciesOutPort.getCookiePolicies(cookiePoliciesRequest);
  }
}
