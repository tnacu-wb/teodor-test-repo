package uk.co.whitbread.hotel.card.model.adapter;

import lombok.RequiredArgsConstructor;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponseType;

@RequiredArgsConstructor
public class InnBTetheredUserDetailsGetResponse extends TetheredUserDetailsGetResponse implements WorldlineResponseAdapter{

  private final TetheredUserDetailsGetResponse tetheredUserDetailsGetResponse;

  @Override
  public TetheredUserDetailsGetResponseType getResponse(){
    return this.tetheredUserDetailsGetResponse.getResponse();
  }

}
