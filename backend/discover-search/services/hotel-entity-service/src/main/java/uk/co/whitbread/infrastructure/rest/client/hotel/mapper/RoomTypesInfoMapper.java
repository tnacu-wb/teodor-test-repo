package uk.co.whitbread.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypesInfoDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomTypesInfoMapper {

  RoomTypesInfo toDomainModel(RoomTypesInfoDto roomTypesInfoDto);

}
