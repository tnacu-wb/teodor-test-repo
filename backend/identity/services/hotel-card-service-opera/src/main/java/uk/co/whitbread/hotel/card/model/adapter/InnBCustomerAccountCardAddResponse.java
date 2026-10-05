package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddResponse;

@RequiredArgsConstructor
public class InnBCustomerAccountCardAddResponse extends CustomerAccountCardAddResponse
    implements WorldlineResponseAdapter {

  private final CustomerAccountCardAddResponse customerAccountCardAddResponse;

  @Override
  public Response getResponse() {
    return this.customerAccountCardAddResponse.getResponse();
  }

}
