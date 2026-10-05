package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddressSearchResponseDto {

  String id;
  String addressText;

}
