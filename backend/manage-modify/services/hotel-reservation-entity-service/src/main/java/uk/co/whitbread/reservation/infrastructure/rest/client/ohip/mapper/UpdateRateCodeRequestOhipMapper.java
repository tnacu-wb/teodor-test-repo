package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatePlanChangeRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;

@Mapper(componentModel = "spring")
public abstract class UpdateRateCodeRequestOhipMapper {

  @Mapping(expression = "java(mapReservationIdsToDto(basketDto))", target = "reservationIds")
  public abstract RatePlanChangeRequestDto toDto(UpdateRequest updateRateCodeRequest,
      @Context BasketDto basketDto);

  protected List<String> mapReservationIdsToDto(BasketDto basketDto) {
    List<String> reservationIds = new ArrayList<>();
    for (BasketItemDto basketItemDto : basketDto.getItems()) {
      reservationIds.add(basketItemDto.getSourceId());
    }
    return reservationIds;
  }

}
