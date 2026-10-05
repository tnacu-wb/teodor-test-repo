package uk.co.whitbread.basket.infrastructure.rest.client.ohip.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.CcuiPaymentRequest;
import uk.co.whitbread.basket.domain.model.ohip.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOhipOutPort;
import uk.co.whitbread.basket.generated.models.ohip.NegotiatedRatesResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper.BillingAddressRequestOhipMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.mapper.CustomReferenceNumberRequestOhipMapper;

@Slf4j
@RequiredArgsConstructor
public class BasketOhipOutPortImpl implements BasketOhipOutPort {

  private final OhipAdapterClient ohipAdapterClient;
  private final BillingAddressRequestOhipMapper billingAddressRequestOhipMapper;
  private final CustomReferenceNumberRequestOhipMapper customReferenceNumberRequestOhipMapper;

  @Override
  public void updateReservationBillingAddress(PaymentRequest paymentRequest,
                                              List<String> reservationIds,
                                              boolean updateGuestProfile,
                                              boolean updateCompanyProfile,
                                              boolean updateContactProfile) {
    log.info("Entered updateReservationBillingAddress");

    ohipAdapterClient.sendReservationBillingAddress(
        billingAddressRequestOhipMapper.toDto(
                paymentRequest, reservationIds, updateGuestProfile, updateCompanyProfile, updateContactProfile));

  }

  @Override
  public void updateReservationBillingAddressCcui(CcuiPaymentRequest ccuiPaymentRequest,
      List<String> reservationIds) {

    ohipAdapterClient.sendReservationBillingAddress(
        billingAddressRequestOhipMapper.toDto(ccuiPaymentRequest, reservationIds));
  }

  @Override
  public void updateCustomReferenceNumber(
      UpdateCustomReferenceNumberRequest customReferenceNumberRequest) {

    ohipAdapterClient.sendUpdateCustomReferenceNumber(
        customReferenceNumberRequestOhipMapper.toDto(customReferenceNumberRequest));
  }

  @Override
  public NegotiatedRatesResponseDto getNegotiatedRates(String profileId) {
    return ohipAdapterClient.getNegotiatedRates(profileId);
  }
}
