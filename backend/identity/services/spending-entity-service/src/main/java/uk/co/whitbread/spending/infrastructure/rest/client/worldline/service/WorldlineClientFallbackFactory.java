package uk.co.whitbread.spending.infrastructure.rest.client.worldline.service;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;

@Slf4j
@Component
public class WorldlineClientFallbackFactory implements FallbackFactory<WorldlineClient> {

  @Override
  public WorldlineClient create(Throwable cause) {
    return new WorldlineClient() {
      @Override
      public AccountInfoResponse getAccountInfo(
          String companyNumber,
          String trustedPartnerCredentials,
          String cultureCode,
          String ipAddress,
          String tetheredUserGuid) {

        log.warn("Failed to call Worldline when retrieving Account info for "
                + "companyNumber={} cultureCode={} ipAddress={} tetheredUserGuid={}"
                + ", returning fallback.",
            companyNumber, cultureCode, ipAddress, tetheredUserGuid, cause);

        return null;
      }

      @Override
      public PaymentInfoResponse getPaymentInfo(Map<String, String> headers,
          Map<String, String> queryParams) {
        log.warn("Failed to call Worldline when retrieving Payment info, returning fallback.",
            cause);
        return null;
      }
    };
  }
}
