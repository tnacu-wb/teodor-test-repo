package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateDiscountOhipMapper {

  private UpdateDiscountRoomStayOhipMapper updateDiscountRoomStayOhipMapper;

  @Autowired
  public void toUpdateDiscountRoomStayOhipMapperForModel(
      final UpdateDiscountRoomStayOhipMapper mapper) {
    this.updateDiscountRoomStayOhipMapper = mapper;
  }

  @Mapping(expression =
      "java(injectRoomStay(updateDiscountRequest,reservationType,discountAmounts,lastVisited))",
      target = "roomStay")
  @Mapping(expression = "java(injectReservationIds(reservationType))", target = "reservationIdList")
  @Mapping(source = "updateDiscountRequest.hotelId", target = "hotelId")
  abstract HotelReservationInstructionType fromDto(
      UpdateDiscountRequest updateDiscountRequest,
      HotelReservationType reservationType,
      List<BigDecimal> discountAmounts,
      int[] lastVisited);

  protected List<UniqueIDType> injectReservationIds(
      HotelReservationType reservationType) {
    return reservationType.getReservationIdList();
  }

  protected RoomStayType injectRoomStay(
      UpdateDiscountRequest updateDiscountRequest,
      HotelReservationType reservationType,
      List<BigDecimal> discountAmounts,
      int[] lastVisited) {
    return updateDiscountRoomStayOhipMapper
        .fromDto(updateDiscountRequest, reservationType, discountAmounts, lastVisited);
  }
}
