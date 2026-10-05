package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateResponseType;

@RequiredArgsConstructor
public class InnBCustomerAccountCardUpdateResponse extends CustomerAccountCardUpdateResponse
    implements WorldlineResponseAdapter {

  private final CustomerAccountCardUpdateResponse customerAccountCardUpdateResponse;

  @Override
  public CustomerAccountCardUpdateResponseType getResponse() {
    return this.customerAccountCardUpdateResponse.getResponse();
  }

}
