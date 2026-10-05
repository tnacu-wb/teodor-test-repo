package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.parV0.AvailResponseSegmentType;
import uk.co.whitbread.ohip.domain.model.availability.out.HotelAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomRateInfo;
import uk.co.whitbread.ohip.domain.model.availability.out.RoomType;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MultiAvailabilityResultMapper {

  @Mapping(target = "arrivalDate", source = "availResponse", qualifiedByName = "arrivalDate")
  @Mapping(target = "departureDate", source = "availResponse", qualifiedByName = "departureDate")
  @Mapping(target = "roomTypes", source = "availResponse", qualifiedByName = "roomRates")
  HotelAvailabilityResult toResultsModel(AvailResponseSegmentType availResponse);

  @Named("arrivalDate")
  default String toArrivalDateForModel(AvailResponseSegmentType availResponse) {
    if (!availResponse.getRoomStays().get(0).getRoomRates().isEmpty()) {
      return availResponse.getRoomStays().get(0).getRoomRates().get(0).getStart().toString();
    }
    return "";
  }

  @Named("departureDate")
  default String toDepartureDateForModel(AvailResponseSegmentType availResponse) {
    if (!availResponse.getRoomStays().get(0).getRoomRates().isEmpty()) {
      return availResponse.getRoomStays().get(0).getRoomRates().get(0).getEnd().toString();
    }
    return "";
  }

  @Named("roomRates")
  default List<RoomType> toRoomRatesForModel(AvailResponseSegmentType availResponse) {
    var roomRateInfos = availResponse.getRoomStays().get(0).getRoomRates().stream()
        .map(roomRateType -> RoomRateInfo.builder()
            .ratePlan(roomRateType.getRatePlanCode())
            .roomType(roomRateType.getRoomType())
            .currency(roomRateType.getTotal().getCurrencyCode())
            .totalPrice(roomRateType.getTotal().getAmountBeforeTax()
                .multiply(BigDecimal.valueOf(roomRateType.getNumberOfUnits())))
            .build()).toList();
    return List.of(RoomType.builder()
        .roomRates(roomRateInfos)
        .build());
  }
}
