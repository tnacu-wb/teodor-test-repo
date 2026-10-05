package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.in.HeaderRequestDto;

@Mapper(componentModel = "spring")
public interface HeaderRequestDtoMapper {

  HeaderRequest toModel(HeaderRequestDto headerRequestDto);

}
