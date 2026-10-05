package uk.co.whitbread.hotel.card.model.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddRequestType;

public class InnBCustomerAccountCardAddRequestTypeBuilder extends
    CustomerAccountCardAddRequestType implements WorldlineRequestTypeAdapter<CustomerAccountCardAddRequestType> {

  @Override
  @JsonIgnore
  public CustomerAccountCardAddRequestType getRequestType() { return this; }

}
