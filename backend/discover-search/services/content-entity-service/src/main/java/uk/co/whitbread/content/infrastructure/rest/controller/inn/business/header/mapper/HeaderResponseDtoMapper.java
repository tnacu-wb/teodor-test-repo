package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.header.out.HeaderResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out.HeaderResponseDto;

@Mapper(componentModel = "spring")
public interface HeaderResponseDtoMapper {

  HeaderResponseDto toDto(HeaderResponse headerResponse);

}
