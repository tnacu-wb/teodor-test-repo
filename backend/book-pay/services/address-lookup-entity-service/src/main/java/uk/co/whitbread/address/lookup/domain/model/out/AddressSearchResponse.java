package uk.co.whitbread.address.lookup.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddressSearchResponse {

  private String monikerId;
  private String address;

}
