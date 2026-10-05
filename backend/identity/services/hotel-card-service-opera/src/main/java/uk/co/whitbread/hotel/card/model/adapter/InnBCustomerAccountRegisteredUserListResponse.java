package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponseType;

@RequiredArgsConstructor
public class InnBCustomerAccountRegisteredUserListResponse extends
    CustomerAccountRegisteredUserListResponse implements WorldlineResponseAdapter{

  private final CustomerAccountRegisteredUserListResponse customerAccountRegisteredUserListResponse;

  @Override
  public CustomerAccountRegisteredUserListResponseType getResponse(){
    return this.customerAccountRegisteredUserListResponse.getResponse();
  }

}
