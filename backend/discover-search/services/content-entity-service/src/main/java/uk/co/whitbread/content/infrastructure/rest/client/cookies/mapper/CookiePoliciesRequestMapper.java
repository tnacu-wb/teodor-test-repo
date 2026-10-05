package uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.out.CookiePoliciesRequestAemDto;

@Mapper(componentModel = "spring")
public interface CookiePoliciesRequestMapper {

  CookiePoliciesRequestAemDto toDto(CookiePoliciesRequest cookiePoliciesRequest);

}
