package uk.co.whitbread.hotel.card.model;

import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import worldline.mst.bsm.api.b2b.pi.data.HeaderType;
import worldline.mst.bsm.api.b2b.pi.data.TrustedPartnerCredentialsType;

/**
 * Data holder for Worldline header data to pass on each request.
 *
 * @param scheme - gb, de supported
 * @param headerType - headers to pass on each request
 * @param trustedPartnerCredentialsType - credentials needed on each Worldline request
 */
public record WorldlineHeadersData(Scheme scheme,
                                   HeaderType headerType, TrustedPartnerCredentialsType trustedPartnerCredentialsType) {


}
