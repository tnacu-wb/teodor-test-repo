package uk.co.whitbread.token.infrastructure.rest.controller.token.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.token.domain.model.out.AuthToken;
import uk.co.whitbread.token.infrastructure.rest.controller.token.model.out.AuthTokenDto;


@Mapper(componentModel = "spring")
public interface TokenRequestDtoMapper {

  AuthTokenDto toDto(AuthToken authToken);
}
