package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;

@RequiredArgsConstructor
public class InnBCustomerAccountCardListResponse extends
    CustomerAccountCardListResponse implements WorldlineResponseAdapter{

  private final CustomerAccountCardListResponse customerAccountCardListResponse;

  @Override
  public CustomerAccountCardListResponseType getResponse(){
    return this.customerAccountCardListResponse.getResponse();
  }

}
