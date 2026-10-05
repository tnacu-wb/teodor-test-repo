package uk.co.whitbread.avail.business.events.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class OperaRestClient {

  private final RestTemplate authorizationRestTemplate;

  public ResponseEntity<String> getOauthToken(String url,
      HttpEntity<MultiValueMap<String, String>> entity) {
    return authorizationRestTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        String.class);
  }
}
