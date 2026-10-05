package uk.co.whitbread.basket.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.basket.generated.models.hotel.HotelInfoDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelInfoMapper {

  HotelInfo toDomainModel(HotelInfoDto hotelInfo);

}
