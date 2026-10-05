package uk.co.whitbread.hotel.card.model.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateRequestType;

public class InnBCustomerAccountCardUpdateRequestTypeBuilder extends
    CustomerAccountCardUpdateRequestType implements WorldlineRequestTypeAdapter<CustomerAccountCardUpdateRequestType> {

  @Override
  @JsonIgnore
  public CustomerAccountCardUpdateRequestType getRequestType() { return this; }

}
