package uk.co.whitbread.hotel.account.client.cdh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CdhClientFallbackFactory implements FallbackFactory<CdhClient> {

  @Override
  public CdhClient create(Throwable throwable) {

    return cdhReservationSearchCriteriaDto -> {
      log.warn("Failed to call cdh. {}", cdhReservationSearchCriteriaDto.getCustomerAccountId(), throwable);
      return null;
    };
  }

}
