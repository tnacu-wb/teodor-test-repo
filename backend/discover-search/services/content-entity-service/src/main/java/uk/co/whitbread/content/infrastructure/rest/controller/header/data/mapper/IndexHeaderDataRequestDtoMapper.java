package uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.in.IndexHeaderDataRequestDto;

@Mapper(componentModel = "spring")
public interface IndexHeaderDataRequestDtoMapper {

  IndexHeaderDataRequest toDomainModel(IndexHeaderDataRequestDto indexHeaderDataRequestDto);

}
