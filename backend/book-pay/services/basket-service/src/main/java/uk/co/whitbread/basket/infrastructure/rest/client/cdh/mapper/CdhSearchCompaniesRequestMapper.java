package uk.co.whitbread.basket.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchCriteriaDto;


@Mapper(componentModel = "spring")
public interface CdhSearchCompaniesRequestMapper {

  CompanySearchCriteriaDto toDto(CdhSearchCompaniesRequest cdhSearchBookingsRequest);

}
