package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelResponseType;

@RequiredArgsConstructor
public class InnBCustomerAccountCardCancelResponse extends CustomerAccountCardCancelResponse
    implements WorldlineResponseAdapter {

  private final CustomerAccountCardCancelResponse customerAccountCardCancelResponse;

  @Override
  public CustomerAccountCardCancelResponseType getResponse() {
    return this.customerAccountCardCancelResponse.getResponse();
  }

}
