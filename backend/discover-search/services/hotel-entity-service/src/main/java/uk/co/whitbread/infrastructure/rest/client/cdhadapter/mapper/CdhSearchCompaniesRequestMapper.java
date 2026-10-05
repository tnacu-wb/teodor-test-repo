package uk.co.whitbread.infrastructure.rest.client.cdhadapter.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchCriteriaDto;
import uk.co.whitbread.domain.model.cdh.CdhSearchCompaniesRequest;


@Mapper(componentModel = "spring")
public interface CdhSearchCompaniesRequestMapper {

  CompanySearchCriteriaDto toDto(CdhSearchCompaniesRequest cdhSearchBookingsRequest);

}