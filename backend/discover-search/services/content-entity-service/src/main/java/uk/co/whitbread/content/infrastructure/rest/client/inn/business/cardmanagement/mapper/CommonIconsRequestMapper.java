package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CommonIconsRequest;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CommonIconsRequestAemDto;

@Mapper(componentModel = "spring")
public interface CommonIconsRequestMapper {

  CommonIconsRequestAemDto toDto(CommonIconsRequest commonIconsRequest);

}
