package uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in.CookiePoliciesInformationDto;

@Mapper(componentModel = "spring")
public interface CookiePoliciesMapper {

  CookiePoliciesInformation toDomainModel(CookiePoliciesInformationDto cookiePoliciesInformationDto);

}
