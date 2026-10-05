package uk.co.whitbread.infrastructure.rest.client.companyentity.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.booking.infrastructure.rest.client.companyentity.generated.models.AddressDto;
import uk.co.whitbread.booking.infrastructure.rest.client.companyentity.generated.models.CompanyResponseDto;
import uk.co.whitbread.domain.model.company.out.Address;
import uk.co.whitbread.domain.model.company.out.Company;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CompanyEntityServiceMapper {

  Company toModel(CompanyResponseDto companyResponseDto);

  Address toModel(AddressDto addressDto);
}

