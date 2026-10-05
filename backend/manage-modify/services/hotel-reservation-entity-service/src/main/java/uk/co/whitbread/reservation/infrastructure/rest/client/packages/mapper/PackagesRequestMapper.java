package uk.co.whitbread.reservation.infrastructure.rest.client.packages.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.PackagesRequest;
import uk.co.whitbread.reservation.infrastructure.rest.client.packages.model.in.PackagesRequestDto;

@Mapper(componentModel = "spring")
public interface PackagesRequestMapper {

  PackagesRequestDto toDto(PackagesRequest packagesRequest);
}
