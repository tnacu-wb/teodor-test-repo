package uk.co.whitbread.ocd.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;
import uk.co.whitbread.ocd.domain.ports.primary.TaxInfoInPort;
import uk.co.whitbread.ocd.domain.ports.secondary.TaxInfoOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class TaxInfoInPortImpl implements TaxInfoInPort {

  private final TaxInfoOutPort taxInfoOutPort;

  @Override
  public TaxResponse getTaxDetails(TaxRequest taxRequest) {
    log.info("Request to get Tax details from OCD for hotel: {}", taxRequest.getHotelId());
    return taxInfoOutPort.getTaxDetails(taxRequest);
  }
}
