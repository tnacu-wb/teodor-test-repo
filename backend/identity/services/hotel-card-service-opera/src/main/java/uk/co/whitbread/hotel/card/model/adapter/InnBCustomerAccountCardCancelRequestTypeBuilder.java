package uk.co.whitbread.hotel.card.model.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelRequestType;

public class InnBCustomerAccountCardCancelRequestTypeBuilder extends
    CustomerAccountCardCancelRequestType implements WorldlineRequestTypeAdapter<CustomerAccountCardCancelRequestType> {

  @Override
  @JsonIgnore
  public CustomerAccountCardCancelRequestType getRequestType() { return this; }

}
