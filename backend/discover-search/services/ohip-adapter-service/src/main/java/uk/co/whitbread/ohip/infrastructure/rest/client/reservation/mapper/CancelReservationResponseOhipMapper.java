package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public interface CancelReservationResponseOhipMapper {

  default CancelReservationResponse toCancelReservationResponseModel(
      List<CancelReservationDetails> cancelReservationDetails,
      Map<String, DepositsResponse> refundedDeposits) {

    List<String> cancellationIds = cancelReservationDetails.stream()
        .flatMap(cancellationDetails ->
            cancellationDetails.getReservations().get(0).getReservationIdList().stream())
        .filter(uniqueIDType -> uniqueIDType.getType()
            .equalsIgnoreCase(UniqueIdTypeEnumDto.CANCELLATION_TYPE.value()))
        .map(UniqueIDType::getId)
        .toList();

    return CancelReservationResponse.builder().cancellationIds(cancellationIds)
        .refundedDeposits(refundedDeposits)
        .build();
  }
}
