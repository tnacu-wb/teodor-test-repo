package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ocd.adapter.service.generated.models.PriceInfoDto;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxResponseDto;
import uk.co.whitbread.ondemandrefreshservice.domain.ports.secondary.OcdAdapterOutPort;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.ocd.service.OcdAdapterClient;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OcdAdapterOutPortImpl implements OcdAdapterOutPort {

  private final OcdAdapterClient ocdAdapterClient;

  @Override
  public BigDecimal getAmountAfterTax(String hotelId, String arrivalDate, String departureDate,
      Integer adults, String ratePlanCode, String roomType) {
    var taxResponse = ocdAdapterClient.getTax(hotelId, arrivalDate, departureDate,
        adults, ratePlanCode, roomType);
    return Optional.ofNullable(taxResponse)
        .map(TaxResponseDto::getTotal)
        .map(PriceInfoDto::getAmountAfterTax)
        .orElse(null);
  }
}