package uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.generated.models.payments.RoomDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomTypeResponseMapper {

  @Mapping(target = "adultsNumber", source = "adults")
  RoomType toModel(RoomDto room);
}
