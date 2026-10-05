package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.in.CardManagementRequest;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.out.CardManagementRequestAemDto;

@Mapper(componentModel = "spring")
public interface CardManagementRequestMapper {

  CardManagementRequestAemDto toDto(CardManagementRequest cardManagementRequest);

}
