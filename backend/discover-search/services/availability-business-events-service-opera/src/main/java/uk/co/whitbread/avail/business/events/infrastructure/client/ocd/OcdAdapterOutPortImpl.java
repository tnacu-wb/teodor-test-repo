package uk.co.whitbread.avail.business.events.infrastructure.client.ocd;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OcdAdapterOutPort;
import uk.co.whitbread.avail.business.events.infrastructure.client.ocd.service.OcdAdapterClient;
import uk.co.whitbread.ocd.adapter.service.generated.models.PriceInfoDto;
import uk.co.whitbread.ocd.adapter.service.generated.models.TaxResponseDto;

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