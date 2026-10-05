package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;

@Mapper(componentModel = "spring", uses = {
    ReservationOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {Arrays.class})
public abstract class SpecialReqRequestOhipMapper {

  private SpecialRequestOhipMapper specialRequestOhipMapper;
  private SpecialRequestsRemoveCommentsMapper specialRequestsRemoveCommentsMapper;

  @Autowired
  public void toSpecialRequestOhipMapperForModel(
      final SpecialRequestOhipMapper specialRequestOhipMapper,
      final SpecialRequestsRemoveCommentsMapper specialRequestsRemoveCommentsMapper) {
    this.specialRequestOhipMapper = specialRequestOhipMapper;
    this.specialRequestsRemoveCommentsMapper = specialRequestsRemoveCommentsMapper;
  }

  @Mapping(expression =
      "java(injectHotelReservations(hotelId,reservationId,specialRequests, bookingNotes))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationDto(
      String hotelId,
      String reservationId,
      List<String> specialRequests,
      List<String> bookingNotes
  );

  @Mapping(expression =
      "java(injectHotelReservationsToRemoveExtsComment(hotelId,reservationId,commentId))",
      target = "reservations")
  public abstract ChangeReservation toChangeReservationRemoveExstCommentDto(String hotelId,
      String reservationId, List<String> commentId);


  protected List<HotelReservationInstructionType> injectHotelReservations(
      String hotelId,
      String reservationId,
      List<String> specialRequests,
      List<String> bookingNotes) {

    return Collections.singletonList(
        specialRequestOhipMapper.fromDto(hotelId, reservationId, specialRequests, bookingNotes));

  }

  protected List<HotelReservationInstructionType> injectHotelReservationsToRemoveExtsComment(
      String hotelId,
      String reservationId,
      List<String> commentId) {

    return Collections.singletonList(
        specialRequestsRemoveCommentsMapper.removeCommentfromDto(hotelId, reservationId, commentId));

  }
}
