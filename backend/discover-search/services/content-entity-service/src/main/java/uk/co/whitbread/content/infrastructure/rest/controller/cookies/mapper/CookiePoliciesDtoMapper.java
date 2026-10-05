package uk.co.whitbread.content.infrastructure.rest.controller.cookies.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.CookiePoliciesInformationDto;

@Mapper(componentModel = "spring")
public interface CookiePoliciesDtoMapper {

  CookiePoliciesInformationDto toDto(CookiePoliciesInformation cookiePoliciesInformation);

}
