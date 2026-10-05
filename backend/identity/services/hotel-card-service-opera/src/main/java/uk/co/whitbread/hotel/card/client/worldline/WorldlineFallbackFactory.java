package uk.co.whitbread.hotel.card.client.worldline;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCostCentreDetailsResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardResponse;
import uk.co.whitbread.hotel.card.exceptions.WorldlineAuthenticationException;
import uk.co.whitbread.hotel.card.exceptions.WorldlineClientException;

@Slf4j
@Component
public class WorldlineFallbackFactory implements FallbackFactory<WorldlineClient> {

  @Override
  public WorldlineClient create(Throwable throwable) {
    return new WorldlineClient() {
      @Override
      public WorldlineCardHolderUserResponse createCardHolderUser(String trustedPartnerCredentials,
          String tetheredUserGuid, String cultureCode, String companyNumber, String ipAddress,
          WorldlineCardHolderUserRequest worldlineCardHolderUserRequest) {
        log.warn("Fallback for createCardHolderUser due to exception: {}", throwable.getMessage());
        throw mapException(throwable);
      }

      @Override
      public WorldlineCostCentreDetailsResponse costCentreDetails(String trustedPartnerCredentials,
          String tetheredUserGuid, String cultureCode, String companyNumber, String ipAddress) {
        log.warn("Fallback for costCentreDetails due to exception: {}", throwable.getMessage());
        throw mapException(throwable);
      }

      @Override
      public WorldlineReplaceCardResponse replaceCard(String trustedPartnerCredentials,
          String tetheredUserGuid, String cultureCode, String companyNumber, String ipAddress,
          WorldlineReplaceCardRequest worldlineReplaceCardRequest) {
        log.warn("Fallback for replaceCard due to exception: {}", throwable.getMessage());
        throw mapException(throwable);
      }
    };
  }

  private RuntimeException mapException(Throwable throwable) {
    if (throwable instanceof FeignException feignException) {
      int status = feignException.status();
      if (status == 401 || status == 403) {
        return new WorldlineAuthenticationException(throwable.getMessage(), throwable);
      }
    }
    return new WorldlineClientException(throwable.getMessage());
  }
}
