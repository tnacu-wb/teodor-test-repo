package uk.co.whitbread.reservation.domain.ports.primary;

import java.util.List;
import java.util.Map;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;

public interface AmendDistributionLogicInPort {

  BasketResponse validateBasketByReference(String basketReference);

  void validateDistributionReservations(ReservationRequest reservationRequest, String hotelId);

  List<String> extractRemovedRoomsIds(List<Reservation> reservations, String tempBasketRef);

  List<String> extractRemovedRoomsIds(List<Reservation> reservations,
                                      List<String> initialOriginalReservationIds);

  List<Reservation> extractNewRooms(List<Reservation> reservations);

  List<AmendStayDatesRequest> extractStayDates(String arrivalDate, String departureDate,
      String tempBookingRef, ReservationRequest reservations);

  void setRoomsSelections(UpdateReservationPackagesByIdRequest updateReservationPackagesRequest,
      String tempBasketRef, ReservationByBasketRefResponse originalReservation, List<String> newIds);

  List<UpdateReservationsRequest> extractUpdatedReservations(List<ReservationByIdResponse> reservationByIdResponses,
      List<UpdateReservationsRequest> updatedReservations, String tempBasketRef);

  Map<String, String> extractLinkAmendReservations(String tempBasketRef);

  List<SpecialRequests> extractSpecialRequests(List<Reservation> reservations,
      List<String> reservationIds, String hotelId, List<String> bookingNotes);

  List<SpecialRequests> setSpecialRequestsForNewAmendRooms(List<String> newIds,
      List<String> bookingNotes, String hotelId);

  void validateDistributionIataNumber(ReservationRequest amendDistributionRequest);
}