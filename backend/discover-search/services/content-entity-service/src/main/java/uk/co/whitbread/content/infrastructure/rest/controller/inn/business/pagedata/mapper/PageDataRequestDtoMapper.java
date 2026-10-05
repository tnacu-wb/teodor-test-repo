package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.DictionaryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.PageDataRequestDto;

@Mapper(componentModel = "spring", imports = DictionaryEnumDto.class)
public interface PageDataRequestDtoMapper {

  PageDataRequest toDomainModel(PageDataRequestDto pageDataRequestDto);
}
