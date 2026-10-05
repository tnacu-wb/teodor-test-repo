package uk.co.whitbread.ocd.domain.ports.primary;

import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.domain.model.tax.out.TaxResponse;

public interface TaxInfoInPort {

  TaxResponse getTaxDetails(TaxRequest taxRequest);

}
