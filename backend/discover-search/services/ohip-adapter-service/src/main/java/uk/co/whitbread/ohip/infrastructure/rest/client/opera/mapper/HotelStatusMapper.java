package uk.co.whitbread.ohip.infrastructure.rest.client.opera.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.model.opera.out.OperaHotelDetails;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelStatusMapper {

  @Mapping(target = "pmsSource", source = "code")
  HotelStatus toDomainModel(OperaHotelDetails hotelInfo);
}
