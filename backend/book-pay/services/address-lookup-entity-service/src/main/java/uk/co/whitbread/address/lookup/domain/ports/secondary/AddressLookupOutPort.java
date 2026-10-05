package uk.co.whitbread.address.lookup.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;

public interface AddressLookupOutPort {

  List<AddressSearchResponse> getAddressesByPostcode(String postCode);

  AddressFormatResponse getFormattedAddress(String monikerId);

}
