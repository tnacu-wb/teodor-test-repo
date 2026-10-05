package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementResponseDto;

@Mapper(componentModel = "spring")
public interface CardManagementResponseDtoMapper {

  CardManagementResponseDto toDto(CardManagementResponse cardManagementResponse);

}
