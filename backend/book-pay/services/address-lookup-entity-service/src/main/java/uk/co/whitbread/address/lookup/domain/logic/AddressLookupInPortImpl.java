package uk.co.whitbread.address.lookup.domain.logic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.address.lookup.domain.model.in.AddressSearchRequest;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;
import uk.co.whitbread.address.lookup.domain.ports.primary.AddressLookupInPort;
import uk.co.whitbread.address.lookup.domain.ports.secondary.AddressLookupOutPort;

@RequiredArgsConstructor
public class AddressLookupInPortImpl implements AddressLookupInPort {

  private final AddressLookupOutPort addressLookupOutPort;

  @Override
  public List<AddressSearchResponse> getAddressesByPostcode(
      AddressSearchRequest addressSearchRequest) {
    return addressLookupOutPort.getAddressesByPostcode(addressSearchRequest.getSearchTerm());
  }

  @Override
  public AddressFormatResponse getFormattedAddress(String monikerId) {
    return addressLookupOutPort.getFormattedAddress(monikerId);
  }
}
