package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public abstract class CancelReservationRequestOhipMapper {

  private static final String CANCEL_REASON_CODE = "CXL";
  private static final String CANCEL_REASON_DESCRIPTION = "Trip Cancelled";

  @Mapping(expression = "java(mapCancelReservationDetails(cancelReservationRequest))", target = "reservations")
  @Mapping(expression = "java(mapReason(cancelReservationRequest))", target = "reason")
  public abstract CancelReservation toCancelReservationModel(
      String reservationId, CancelReservationRequest cancelReservationRequest);

  protected List<CancelReservationType> mapCancelReservationDetails(
      CancelReservationRequest cancelReservationRequest) {
    var cancelReservationType = new CancelReservationType();
    var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setId(cancelReservationRequest.getReservationIds().get(0));
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    cancelReservationType.setReservationIdList(Collections.singletonList(reservationUniqueIdType));
    cancelReservationType.setHotelId(cancelReservationRequest.getHotelId());

    return List.of(cancelReservationType);
  }

  protected CancellationReasonType mapReason(CancelReservationRequest cancelReservationRequest) {
    var cancelReservationType = new CancellationReasonType();
    if (nonNull(cancelReservationRequest.getReservationOverrideReason())) {
      cancelReservationType.setCode(
          cancelReservationRequest.getReservationOverrideReason().getReasonCode());

      String description = String.join(", ",
          cancelReservationRequest.getReservationOverrideReason().getReasonName(),
          cancelReservationRequest.getReservationOverrideReason().getCallerName(),
          cancelReservationRequest.getReservationOverrideReason().getManagerName());
      cancelReservationType.setDescription(description);
    } else {
      cancelReservationType.setCode(CANCEL_REASON_CODE);
      cancelReservationType.setDescription(CANCEL_REASON_DESCRIPTION);
    }
    return cancelReservationType;
  }

}
