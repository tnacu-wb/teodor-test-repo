package uk.co.whitbread.cdh.infrastructure.rest.client.oauth;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.model.CdhToken;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdhTokenRefresher {

  private final CdhToken cdhToken;
  private final OAuthProvider oauthProvider;

  @Scheduled(fixedRate = 59, timeUnit = TimeUnit.MINUTES)
  public void refreshValue() {
    cdhToken.setValue(oauthProvider.getNewBearerToken());
  }
}