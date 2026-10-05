package uk.co.whitbread.hotel.card.client.piba;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.exceptions.PibaAccountClientException;

@Slf4j
@Component
public class PibaAccountClientFallbackFactory  implements FallbackFactory<PibaAccountClient> {

  @Override
  public PibaAccountClient create(Throwable throwable) {
    return (accessedBy, tetheredUserRequest) -> {
      log.warn("Fallback for registerTetheredUser due to exception: {}", throwable.getMessage());
      throw new PibaAccountClientException(throwable.getMessage());
    };
  }

}
