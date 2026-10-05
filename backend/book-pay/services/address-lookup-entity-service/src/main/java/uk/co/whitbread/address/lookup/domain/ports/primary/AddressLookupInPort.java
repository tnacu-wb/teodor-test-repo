package uk.co.whitbread.address.lookup.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.address.lookup.domain.model.in.AddressSearchRequest;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;

public interface AddressLookupInPort {

  List<AddressSearchResponse> getAddressesByPostcode(AddressSearchRequest addressSearchRequest);

  AddressFormatResponse getFormattedAddress(String monikerId);

}
