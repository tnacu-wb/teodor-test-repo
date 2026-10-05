package uk.co.whitbread.hotel.card.model.adapter;

import uk.co.whitbread.hotel.card.model.WorldlineHeadersData;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

public interface WorldlineRequestTypeAdapter<T> {

  T getRequestType();

  void setHeader(HeaderType value);

  void setTrustedPartnerCredentials(TrustedPartnerCredentialsType value);

  void setTetheredUserGuid(String value);

  default void populateRequestHeaders(WorldlineHeadersData worldlineHeadersData, String accountId) {
    setHeader(worldlineHeadersData.headerType());
    setTrustedPartnerCredentials(worldlineHeadersData.trustedPartnerCredentialsType());
    setTetheredUserGuid(accountId);
  }

}
