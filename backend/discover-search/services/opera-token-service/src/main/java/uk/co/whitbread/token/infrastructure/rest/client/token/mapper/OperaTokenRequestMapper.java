package uk.co.whitbread.token.infrastructure.rest.client.token.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.token.domain.model.out.AuthToken;
import uk.co.whitbread.token.domain.model.out.TokenResponse;

@Mapper(componentModel = "spring")
public interface OperaTokenRequestMapper {

  AuthToken toResultDto(TokenResponse tokenResponse);

}
