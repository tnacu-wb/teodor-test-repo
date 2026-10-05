package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.index.header.data.in.IndexHeaderDataRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.index.header.data.out.IndexHeaderDataRequestDto;

@Mapper(componentModel = "spring")
public interface IndexHeaderDataRequestMapper {

  IndexHeaderDataRequestDto toDto(IndexHeaderDataRequest indexHeaderDataRequest);

}
