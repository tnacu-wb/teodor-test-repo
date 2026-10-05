package uk.co.whitbread.basket.domain.ports.primary;

import java.util.Optional;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;

public interface RefundInPort {

  Optional<RefundResponse> refundPayment(final String basketReference, final RefundRequest refundRequest);

}
