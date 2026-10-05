package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.PageDataRequestDto;

@Mapper(componentModel = "spring")
public interface PageDataRequestMapper {

  PageDataRequest toDomainModel(PageDataRequestDto pageDataRequestDto);

  PageDataRequestDto toDtoModel(PageDataRequest pageDataRequest);
}
