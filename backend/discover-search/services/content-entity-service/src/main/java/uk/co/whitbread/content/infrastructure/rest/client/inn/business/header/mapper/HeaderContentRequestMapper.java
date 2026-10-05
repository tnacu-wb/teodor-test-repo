package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.header.in.HeaderRequest;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.HeaderRequestAemDto;

@Mapper(componentModel = "spring")
public interface HeaderContentRequestMapper {

  HeaderRequestAemDto toDto(HeaderRequest headerRequest);


}
