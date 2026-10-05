package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressFormatResponseDto;

@Mapper(componentModel = "spring")
public interface AddressFormatDtoMapper {

  AddressFormatResponseDto toDto(AddressFormatResponse addressFormatResponse);

}
