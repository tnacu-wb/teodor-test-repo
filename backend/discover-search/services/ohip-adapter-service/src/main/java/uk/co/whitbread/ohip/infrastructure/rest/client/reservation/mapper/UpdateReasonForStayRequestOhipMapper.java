package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestAdditionalInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateReasonForStayRequestOhipMapper {

  @Mapping(expression = "java(injectReservationDetails(updateReasonForStayRequest))",
      target = "reservations")
  public abstract ChangeReservation toDto(UpdateReasonForStayRequest updateReasonForStayRequest);

  protected List<HotelReservationInstructionType> injectReservationDetails(
      UpdateReasonForStayRequest updateReasonForStayRequest) {

    final var resGuestAdditionalInfoType = new ResGuestAdditionalInfoType();
    resGuestAdditionalInfoType.setPurposeOfStay(updateReasonForStayRequest.getReasonForStay());

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setHotelId(updateReasonForStayRequest.getHotelId());
    hotelReservation.setAdditionalGuestInfo(resGuestAdditionalInfoType);

    return List.of(hotelReservation);
  }
}
