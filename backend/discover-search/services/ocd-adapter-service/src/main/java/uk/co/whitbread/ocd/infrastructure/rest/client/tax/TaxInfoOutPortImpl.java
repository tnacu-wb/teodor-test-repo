package uk.co.whitbread.ocd.infrastructure.rest.client.tax;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsResponse;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;
import uk.co.whitbread.ocd.domain.ports.secondary.TaxInfoOutPort;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.OcdClient;
import uk.co.whitbread.ocd.infrastructure.rest.controller.tax.mapper.OfferDetailsResponseMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaxInfoOutPortImpl implements TaxInfoOutPort {

  private final OcdClient ocdClient;

  private final OfferDetailsResponseMapper offerDetailsResponseMapper;

  @Override
  public TaxResponse getTaxDetails(TaxRequest taxRequest) {
    OfferDetailsResponse  offerDetailsResponse = ocdClient.getTaxDetails(taxRequest);
    if (Objects.isNull(offerDetailsResponse) || Objects.isNull(offerDetailsResponse.getOffer())) {
      log.warn("No tax details found for request: {}", taxRequest);
      return TaxResponse.builder().build();
    }
    log.debug("Received offer details from OCD: {}", offerDetailsResponse.getOffer());
    return offerDetailsResponseMapper.toModel(offerDetailsResponse.getOffer());
  }
}
