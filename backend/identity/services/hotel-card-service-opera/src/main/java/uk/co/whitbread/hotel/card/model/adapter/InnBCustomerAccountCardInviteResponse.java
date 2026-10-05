package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteCardholderResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteResponse;

@RequiredArgsConstructor
public class InnBCustomerAccountCardInviteResponse extends CustomerAccountCardInviteResponse
    implements WorldlineResponseAdapter {

  private final CustomerAccountCardInviteResponse customerAccountCardInviteResponse;

  @Override
  public CustomerAccountCardInviteCardholderResponseType getResponse() {
    return this.customerAccountCardInviteResponse.getResponse();
  }

}
