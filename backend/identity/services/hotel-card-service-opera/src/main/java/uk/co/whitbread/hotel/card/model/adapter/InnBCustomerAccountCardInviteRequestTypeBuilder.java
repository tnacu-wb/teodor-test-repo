package uk.co.whitbread.hotel.card.model.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteCardHolderRequestType;

public class InnBCustomerAccountCardInviteRequestTypeBuilder extends
    CustomerAccountCardInviteCardHolderRequestType
      implements WorldlineRequestTypeAdapter<CustomerAccountCardInviteCardHolderRequestType> {

  @Override
  @JsonIgnore
  public CustomerAccountCardInviteCardHolderRequestType getRequestType() {
    return this;
  }

}
