package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.header.in.LayoutRequest;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.out.LayoutRequestAemDto;

@Mapper(componentModel = "spring")
public interface LayoutRequestMapper {

  LayoutRequestAemDto toDto(LayoutRequest layoutRequest);

}
