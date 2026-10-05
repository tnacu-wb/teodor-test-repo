package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;

@Mapper(componentModel = "spring")
public interface PackagesSelectionMapper {

  PackagesSelection toModel(uk.co.whitbread.ohip.domain.model.reservation.out.PackagesSelection packagesSelection);
}
