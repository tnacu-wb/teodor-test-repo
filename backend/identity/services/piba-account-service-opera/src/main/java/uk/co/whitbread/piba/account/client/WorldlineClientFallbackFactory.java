package uk.co.whitbread.piba.account.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;

@Slf4j
@Component
public class WorldlineClientFallbackFactory implements FallbackFactory<WorldlineClient> {

  @Override
  public WorldlineClient create(Throwable cause) {
    return (companyNumber, trustedPartnerCredentials, cultureCode, ipAddress, tetheredUserGuid) -> {

      log.warn("Failed to call Worldline when retrieving Account info for "
              + "companyNumber={} cultureCode={} ipAddress={} tetheredUserGuid={}"
              + ", returning fallback.",
          companyNumber, cultureCode, ipAddress, tetheredUserGuid, cause);

      return null;
    };
  }
}
