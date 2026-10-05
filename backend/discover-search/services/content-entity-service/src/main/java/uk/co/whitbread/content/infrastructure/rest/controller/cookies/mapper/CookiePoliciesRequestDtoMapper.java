package uk.co.whitbread.content.infrastructure.rest.controller.cookies.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.in.CookiePoliciesRequestDto;

@Mapper(componentModel = "spring")
public interface CookiePoliciesRequestDtoMapper {

  CookiePoliciesRequest toDomainModel(CookiePoliciesRequestDto cookiePoliciesRequestDto);

}
