package uk.co.whitbread.ocd.domain.ports.secondary;

import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;

public interface TaxInfoOutPort {

  TaxResponse getTaxDetails(TaxRequest taxRequest);
}
