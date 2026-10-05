package uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.RoomTypesDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomTypesInfoMapper {

  @Mapping(target = "roomType", source = "roomTypeSummary")
  RoomTypesInfo toDomainModel(RoomTypesDto roomTypesDto);
}
