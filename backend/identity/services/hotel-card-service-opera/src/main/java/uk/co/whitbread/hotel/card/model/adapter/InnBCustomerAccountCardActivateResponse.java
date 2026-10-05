package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateResponseType;

@RequiredArgsConstructor
public class InnBCustomerAccountCardActivateResponse extends CustomerAccountCardActivateResponse
    implements WorldlineResponseAdapter {

  private final CustomerAccountCardActivateResponse customerAccountCardActivateResponse;

  @Override
  public CustomerAccountCardActivateResponseType getResponse() {
    return this.customerAccountCardActivateResponse.getResponse();
  }

}
