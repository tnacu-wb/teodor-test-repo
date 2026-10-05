package uk.co.whitbread.address.lookup.infrastructure.rest.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;
import uk.co.whitbread.address.lookup.domain.ports.secondary.AddressLookupOutPort;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.QasClient;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper.AddressFormatQasMapper;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper.AddressSearchQasMapper;

@Slf4j
@RequiredArgsConstructor
public class AddressLookupOutPortImpl implements AddressLookupOutPort {

  private final AddressSearchQasMapper addressSearchQasMapper;
  private final AddressFormatQasMapper addressFormatQasMapper;

  private final QasClient qasClient;

  @Override
  public List<AddressSearchResponse> getAddressesByPostcode(String postCode) {
    log.debug("Entering get address by postCode={}", postCode);
    var request = addressSearchQasMapper.toDto(postCode);

    return addressSearchQasMapper.toModel(qasClient.search(request));
  }

  @Override
  public AddressFormatResponse getFormattedAddress(String monikerId) {
    log.debug("Entering get formatted address with monikerId={}", monikerId);
    var request = addressFormatQasMapper.toDto(monikerId);

    return addressFormatQasMapper.toModel(qasClient.getFormattedAddress(request))
        .orElseGet(AddressFormatResponse::new);
  }
}
