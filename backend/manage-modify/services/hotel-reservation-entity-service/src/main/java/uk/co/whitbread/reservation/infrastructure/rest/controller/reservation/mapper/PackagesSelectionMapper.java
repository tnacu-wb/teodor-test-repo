package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendPackagesDistributionDto;

@Mapper(componentModel = "spring")
public interface PackagesSelectionMapper {

  List<PackagesSelection> toModel(List<AmendPackagesDistributionDto> dtoList);

  @Mapping(source = "packageCode", target = "id")
  @Mapping(source = "totalQuantity", target = "noSelections")
  PackagesSelection toModel(AmendPackagesDistributionDto dto);
}
