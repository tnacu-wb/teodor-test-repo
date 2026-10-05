package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponseType;

@RequiredArgsConstructor
public class InnBCustomerAccountCardViewResponse extends CustomerAccountCardViewResponse
    implements WorldlineResponseAdapter {

  private final CustomerAccountCardViewResponse customerAccountCardViewResponse;

  @Override
  public CustomerAccountCardViewResponseType getResponse() {
    return this.customerAccountCardViewResponse.getResponse();
  }

}
