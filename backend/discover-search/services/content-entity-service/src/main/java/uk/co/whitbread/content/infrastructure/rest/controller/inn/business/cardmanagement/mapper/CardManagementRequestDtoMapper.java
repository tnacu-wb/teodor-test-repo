package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.in.CardManagementRequestDto;

@Mapper(componentModel = "spring")
public interface CardManagementRequestDtoMapper {

  CardManagementRequest toModel(CardManagementRequestDto cardManagementRequestDto);

}
