package uk.co.whitbread.payments.infrastructure.rest.client.customers.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.payments.domain.model.out.CompanyDetailsResponse;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.CompanyDetailsResponseDto;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

  CompanyDetailsResponse toCompanyModel(CompanyDetailsResponseDto companyDto);
}
