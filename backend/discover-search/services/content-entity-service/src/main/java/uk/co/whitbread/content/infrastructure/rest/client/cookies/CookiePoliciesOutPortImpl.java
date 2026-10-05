package uk.co.whitbread.content.infrastructure.rest.client.cookies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;
import uk.co.whitbread.content.domain.ports.secondary.CookiePoliciesOutPort;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.adapter.CookiePoliciesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper.CookiePoliciesMapper;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper.CookiePoliciesRequestMapper;

@Slf4j
@RequiredArgsConstructor
public class CookiePoliciesOutPortImpl implements CookiePoliciesOutPort {

  private final CookiePoliciesAemClient cookiePoliciesAemClient;
  private final CookiePoliciesRequestMapper cookiePoliciesRequestMapper;
  private final CookiePoliciesMapper cookiePoliciesMapper;

  @Override
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager7Days",
      value = "CookiePoliciesCache")
  public CookiePoliciesInformation getCookiePolicies(CookiePoliciesRequest cookiePoliciesRequest) {
    var request = cookiePoliciesRequestMapper.toDto(cookiePoliciesRequest);
    var cookiePoliciesAem = cookiePoliciesAemClient.getCookiePolicies(request);
    return cookiePoliciesMapper.toDomainModel(cookiePoliciesAem);
  }
}
