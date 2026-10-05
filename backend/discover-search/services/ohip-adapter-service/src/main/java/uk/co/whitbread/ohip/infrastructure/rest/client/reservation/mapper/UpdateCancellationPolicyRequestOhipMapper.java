package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Date;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCancellationPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rsvV0.ReservationCancellationPolicyCriteria;

@Mapper(componentModel = "spring")
public interface UpdateCancellationPolicyRequestOhipMapper {

  String RESERVATION = "Reservation";
  String INVALID_POLICY_ID = "-1";

  @Mapping(source = "resCancellationPolicyType", target = "policy")
  @Mapping(source = "hotelId", target = "policy.hotelId")
  @Mapping(source = "reservationId", target = "policy.reservationId.id")
  @Mapping(constant = RESERVATION, target = "policy.reservationId.type")
  @Mapping(constant = INVALID_POLICY_ID, target = "policy.policyId.id")
  @Mapping(source = "absoluteDeadline", target = "policy.policy.deadline.absoluteDeadline")
  ReservationCancellationPolicyCriteria toReservationCancellationPolicyCriteriaDto(
      ResCancellationPolicyType resCancellationPolicyType, String hotelId, String reservationId,
      Date absoluteDeadline);
}
