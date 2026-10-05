package uk.co.whitbread.basket.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ohip.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.generated.models.ohip.NegotiatedRatesResponseDto;

public interface BasketOhipOutPort {

  void updateReservationBillingAddress(PaymentRequest paymentRequest,
                                       List<String> reservationIds,
                                       boolean updateGuestProfile,
                                       boolean updateCompanyProfile,
                                       boolean updateContactProfile);

  void updateReservationBillingAddressCcui(CcuiPaymentRequest ccuiPaymentRequest, List<String> reservationIds);

  void updateCustomReferenceNumber(UpdateCustomReferenceNumberRequest customReferenceNumberRequest);

  NegotiatedRatesResponseDto getNegotiatedRates(String profileId);
}
