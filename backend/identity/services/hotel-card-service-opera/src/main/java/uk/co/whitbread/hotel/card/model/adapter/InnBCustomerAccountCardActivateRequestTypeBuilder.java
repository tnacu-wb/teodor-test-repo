package uk.co.whitbread.hotel.card.model.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateRequestType;

public class InnBCustomerAccountCardActivateRequestTypeBuilder extends CustomerAccountCardActivateRequestType
      implements WorldlineRequestTypeAdapter<CustomerAccountCardActivateRequestType> {

  @Override
  @JsonIgnore
  public CustomerAccountCardActivateRequestType getRequestType() {
    return this;
  }

}
