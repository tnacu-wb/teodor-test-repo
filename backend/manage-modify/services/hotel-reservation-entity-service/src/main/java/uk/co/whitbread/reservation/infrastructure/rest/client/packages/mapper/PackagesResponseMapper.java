package uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackagesResponseDto;
import uk.co.whitbread.reservation.domain.model.out.PackagesResponse;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PackagesResponseMapper {

  PackagesResponse toModel(PackagesResponseDto packagesResponse);
}
