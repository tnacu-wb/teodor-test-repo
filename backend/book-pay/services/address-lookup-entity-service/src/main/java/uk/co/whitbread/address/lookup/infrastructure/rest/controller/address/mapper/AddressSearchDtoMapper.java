package uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.address.lookup.domain.model.in.AddressSearchRequest;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.in.AddressSearchRequestDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressSearchResponseDto;

@Mapper(componentModel = "spring")
public interface AddressSearchDtoMapper {

  AddressSearchRequest toModel(AddressSearchRequestDto addressSearchRequestDto);

  List<AddressSearchResponseDto> toDto(List<AddressSearchResponse> addressSearchResponse);

  @Mapping(target = "id", source = "monikerId")
  @Mapping(target = "addressText", source = "address")
  AddressSearchResponseDto toDto(AddressSearchResponse addressSearchResponse);

}
