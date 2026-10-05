package uk.co.whitbread.hotel.card.model.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewRequestType;

public class InnBCustomerAccountCardViewRequestTypeBuilder extends
    CustomerAccountCardViewRequestType implements WorldlineRequestTypeAdapter<CustomerAccountCardViewRequestType> {

  @Override
  @JsonIgnore
  public CustomerAccountCardViewRequestType getRequestType() {
    return this;
  }

}
