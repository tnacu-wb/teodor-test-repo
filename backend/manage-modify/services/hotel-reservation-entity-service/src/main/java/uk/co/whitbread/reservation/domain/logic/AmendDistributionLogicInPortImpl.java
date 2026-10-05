package uk.co.whitbread.reservation.domain.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.BasketStatusEnum;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CustomerType;
import uk.co.whitbread.reservation.domain.model.in.EmailInfoType;
import uk.co.whitbread.reservation.domain.model.in.EmailType;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.PersonNameType;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeEmails;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AmendDistributionLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;

@Slf4j
@RequiredArgsConstructor
public class AmendDistributionLogicInPortImpl implements AmendDistributionLogicInPort {

  public static final int DISTRIBUTION_IATA_NUMBER_LENGTH = 8;
  private final BasketOutPort basketOutPort;
  private final AmendLogicInPort amendLogicInPort;

  @Override
  public BasketResponse validateBasketByReference(String basketReference) {
    BasketResponse basket = basketOutPort.getBasketById(basketReference);

    if (!basket.getStatus().equals(BasketStatusEnum.COMPLETED.name())) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_INCONSISTENT_BASKET_EXCEPTION,
          String.format("Inconsistent basket status: %s", basket.getStatus()));
      ExceptionLogger.log(log, ex);
      throw ex;
    }
    return basket;
  }

  @Override
  public void validateDistributionReservations(ReservationRequest reservationRequest,
      String hotelId) {
    var arrivalDate = reservationRequest.getReservations().get(0).getRoomRates().getStartDate();
    var departureDate = reservationRequest.getReservations().get(0).getRoomRates().getEndDate();

    boolean reservationsValid = reservationRequest.getReservations().stream()
        .allMatch(reservation -> reservation.getRoomRates().getStartDate().equals(arrivalDate)
            && reservation.getRoomRates().getEndDate().equals(departureDate)
            && reservation.getHotelId().equals(hotelId));

    if (!reservationsValid) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_INCONSISTENT_DATES_EXCEPTION,
          "All reservations must be from the same hotel and have the same arrival and departure dates!");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  @Override
  public List<String> extractRemovedRoomsIds(List<Reservation> reservations, String tempBasketRef) {
    Map<String, String> linkAmendReservations = extractLinkAmendReservations(tempBasketRef);

    List<String> newReservationIds = reservations.stream()
        .filter(isReservationNew().negate())
        .map(Reservation::getExternalReferenceId)
        .toList();

    return linkAmendReservations.keySet().stream()
        .filter(reservationId -> !newReservationIds.contains(reservationId))
        .map(linkAmendReservations::get)
        .toList();
  }

  @Override
  public List<String> extractRemovedRoomsIds(List<Reservation> amendReservations,
                                             List<String> initialOriginalReservationIds) {

    List<String> newReservationIds = amendReservations.stream()
            .filter(isReservationNew().negate())
            .map(Reservation::getExternalReferenceId)
            .toList();

    return initialOriginalReservationIds.stream()
            .filter(reservationId -> !newReservationIds.contains(reservationId))
            .toList();
  }

  @Override
  public List<Reservation> extractNewRooms(List<Reservation> newReservations) {
    // If the reservationId is null, it means that the reservation is new and needs to be added
    // to the original reservation
    return newReservations.stream().filter(isReservationNew()).toList();
  }

  @Override
  public List<AmendStayDatesRequest> extractStayDates(String arrivalDate, String departureDate,
      String tempBookingRef, ReservationRequest reservationRequest) {

    return reservationRequest.getReservations().stream()
        .filter(isReservationNew().negate())
        .filter(sameDatesReservations(arrivalDate, departureDate).negate())
        .map(
            reservation -> buildAmendStayDatesRequest(tempBookingRef, reservationRequest.getToken(),
                reservation, reservationRequest.getBookingChannel()))
        .toList();
  }

  @Override
  public void setRoomsSelections(UpdateReservationPackagesByIdRequest updateReservationPackagesRequest,
      String tempBasketRef, ReservationByBasketRefResponse reservations, List<String> newIds) {

    Map<Object, List<PackagesSelection>> newRoomsSelectionsMap;
    newRoomsSelectionsMap = getRoomSelectionMap(updateReservationPackagesRequest);

    var addedReservations = updateReservationPackagesRequest.getRoomsSelections().stream()
        .filter(roomSelection -> roomSelection.getReservationId() == null)
        .toList();
    IntStream.range(0, newIds.size())
        .forEach(index ->
            newRoomsSelectionsMap.put(newIds.get(index), addedReservations.get(index).getPackagesSelection()));

    var previousRoomsSelections = new ArrayList<RoomsSelectionsByReservationId>();
    var newRoomsSelections = new ArrayList<RoomsSelectionsByReservationId>();

    reservations.getReservationByIdList()
        .forEach(reservation -> {
          var packagesSelectionList = new ArrayList<PackagesSelection>();
          reservation.getReservationPackageList()
              .forEach(reservationPackage -> packagesSelectionList
                  .add(new PackagesSelection(reservationPackage.getPackageCode(),
                      reservationPackage.getTotalQuantity(), reservationPackage.getPackageGroup())));
          previousRoomsSelections
              .add(new RoomsSelectionsByReservationId(reservation.getReservationId(),
                  packagesSelectionList));

          newRoomsSelections
              .add(new RoomsSelectionsByReservationId(reservation.getReservationId(),
                  newRoomsSelectionsMap.get(reservation.getReservationId())));
        });

    newRoomsSelections.stream()
        .filter(e -> e.getPackagesSelection() == null)
        .forEach(e -> e.setPackagesSelection(Collections.emptyList()));

    updateReservationPackagesRequest.setPreviousRoomsSelections(previousRoomsSelections);
    updateReservationPackagesRequest.setRoomsSelections(newRoomsSelections);
  }

  @Override
  public List<UpdateReservationsRequest> extractUpdatedReservations(
      List<ReservationByIdResponse> reservationByIdResponses,
      List<UpdateReservationsRequest> updatedReservations,
      String tempBasketRef) {

    List<UpdateReservationsRequest> updatedResExclNewOnes = updatedReservations.stream()
        .filter(res -> Objects.nonNull(res.getReservations().get(0).getReservationId()))
        .toList();

    deleteRemovedRooms(reservationByIdResponses, updatedResExclNewOnes);

    // If updated reservation comes without a lead guest set the lead guest from
    // the original reservation
    if (!updatedResExclNewOnes.isEmpty()) {
      IntStream.range(0, updatedResExclNewOnes.size())
          .forEach(i -> {
            var guests = updatedResExclNewOnes.get(i).getReservations().get(0)
                .getReservationGuests();
            if (guests.isEmpty()) {
              var guest =
                  getReservationGuests(reservationByIdResponses.get(i).getReservationGuestList());
              guests.add(guest);
            }
          });
    }

    List<UpdateReservationsRequest> updated = IntStream.range(0, updatedResExclNewOnes.size())
        .mapToObj(i -> {
          if (amendLogicInPort.isLeadGuestUpdated(reservationByIdResponses.get(i),
              updatedResExclNewOnes.get(i).getReservations().get(0))
              || isRoomStayUpdated(reservationByIdResponses.get(i),
              updatedResExclNewOnes.get(i).getReservations().get(0))) {
            return updatedResExclNewOnes.get(i);
          }
          return null;
        })
        .filter(Objects::nonNull)
        .toList();

    updated.forEach(
        reservationsRequest ->
            reservationsRequest.setDistributionIATANumber(updatedReservations.get(0)
                .getDistributionIATANumber()));

    if (Boolean.TRUE.equals(updatedReservations.get(0).getBookingChannel().isDistr())) {
      return updated;
    }

    if (!updated.isEmpty()) {
      // Retrieve original reservations ids for the ones that have updates
      List<String> originalReservationIds = getReservationIds(updated);

      Map<String, String> linkAmendReservations = extractLinkAmendReservations(tempBasketRef);
      // Check link between original and updated reservations and retrieve the
      // corresponding temporary reservation ids
      List<String> tempReservationIds = linkAmendReservations.keySet().stream()
          .filter(originalReservationIds::contains)
          .map(linkAmendReservations::get)
          .toList();

      // Replace the original reservation ids with the temporary ones
      IntStream.range(0, updated.size())
          .forEach(i -> updated.get(i).getReservations().get(0)
              .setReservationId(tempReservationIds.get(i)));
    }
    return updated;
  }

  private ReservationGuests getReservationGuests(List<ReservationByIdGuestsResponse> guestResponse) {
    return ReservationGuests.builder()
        .profileInfo(getProfileInfo(guestResponse))
        .build();
  }

  private ProfileInfo getProfileInfo(List<ReservationByIdGuestsResponse> guestResponse) {
    return ProfileInfo.builder()
        .profile(ProfileType.builder()
            .customer(getCustomerType(guestResponse))
            .emails(getProfileTypeEmails(guestResponse))
            .build())
        .build();
  }

  private CustomerType getCustomerType(List<ReservationByIdGuestsResponse> guestResponse) {
    return CustomerType.builder()
        .personName(List.of(getPersonNameType(guestResponse)))
        .build();
  }

  private ProfileTypeEmails getProfileTypeEmails(List<ReservationByIdGuestsResponse> guestResponse) {
    return ProfileTypeEmails.builder()
        .emailInfo(List.of(EmailInfoType.builder()
            .email(EmailType.builder()
                .emailAddress(guestResponse.get(0).getEmail())
                .build())
            .build()))
        .build();
  }

  private PersonNameType getPersonNameType(List<ReservationByIdGuestsResponse> guestResponse) {
    return PersonNameType.builder()
        .givenName(guestResponse.get(0).getGivenName())
        .surname(guestResponse.get(0).getSurName())
        .nameTitle(guestResponse.get(0).getNameTitle())
        .build();
  }

  private Predicate<Reservation> sameDatesReservations(String arrivalDate,
      String departureDate) {
    return reservation -> reservation.getRoomRates().getStartDate().equals(arrivalDate)
        && reservation.getRoomRates().getEndDate().equals(departureDate);
  }

  private Predicate<Reservation> isReservationNew() {
    return reservation -> Objects.isNull(reservation.getExternalReferenceId());
  }

  @Override
  public Map<String, String> extractLinkAmendReservations(String tempBasketRef) {
    var tempBasket = basketOutPort.getBasketById(tempBasketRef);
    return tempBasket.getLinkAmendReservations();
  }

  @Override
  public List<SpecialRequests> extractSpecialRequests(List<Reservation> amendDistributionRequest,
      List<String> reservationIds, String hotelId, List<String> bookingNotes) {

    List<SpecialRequests> specialRequests = new ArrayList<>();
    var existingReservationRequestToAmend = amendDistributionRequest.stream()
        .filter(reservation ->
            Objects.nonNull(reservation.getExternalReferenceId()) && (
                CollectionUtils.isNotEmpty(amendDistributionRequest)
                    && reservationIds.contains(reservation.getExternalReferenceId())))
        .toList();

    if (Objects.nonNull(bookingNotes) || !existingReservationRequestToAmend.isEmpty()) {
      for (Reservation resReq : existingReservationRequestToAmend) {
        if (CollectionUtils.isNotEmpty(resReq.getRoomRates().getSpecialRequests())
            || Objects.nonNull(bookingNotes)) {
          var specialReq = createSpecialRequest(resReq.getExternalReferenceId(),
              resReq.getHotelId(),
              resReq.getRoomRates().getSpecialRequests(), bookingNotes);
          specialRequests.add(specialReq);
        }
      }
    }

    return specialRequests;
  }

  @Override
  public List<SpecialRequests> setSpecialRequestsForNewAmendRooms(List<String> newIds,
      List<String> bookingNotes, String hotelId) {
    List<SpecialRequests> specialRequests = new ArrayList<>();
    if (!newIds.isEmpty() && Objects.nonNull(bookingNotes)) {
      for (String reservationId : newIds) {
        var specialReq = createSpecialRequest(reservationId, hotelId, null, bookingNotes);
        specialRequests.add(specialReq);
      }
    }
    return specialRequests;
  }

  @Override
  public void validateDistributionIataNumber(ReservationRequest amendDistributionRequest) {
    if (!StringUtils.isBlank(amendDistributionRequest.getDistributionIATANumber())
        && amendDistributionRequest.getDistributionIATANumber().trim().length() != DISTRIBUTION_IATA_NUMBER_LENGTH) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_INVALID_IATA_NUMBER_EXCEPTION,
          String.format("Invalid IATA number: %s", amendDistributionRequest.getDistributionIATANumber()));
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private SpecialRequests createSpecialRequest(String externalReferenceId, String hotelId,
      List<String> specialRequest, List<String> bookingNotes) {
    return SpecialRequests.builder()
        .reservationIds(List.of(externalReferenceId))
        .hotelId(hotelId)
        .specialRequests(Objects.requireNonNullElseGet(specialRequest, Collections::emptyList))
        .bookingNotes(bookingNotes)
        .build();
  }

  private AmendStayDatesRequest buildAmendStayDatesRequest(String tempBookingRef, String token,
      Reservation reservation, BookingChannel bookingChannel) {
    return AmendStayDatesRequest.builder()
        .tempBookingRef(tempBookingRef)
        .newStartDate(reservation.getRoomRates().getStartDate())
        .newEndDate(reservation.getRoomRates().getEndDate())
        .bookingChannel(bookingChannel)
        .token(token)
        .build();
  }

  private Map<Object, List<PackagesSelection>> getRoomSelectionMap(
          UpdateReservationPackagesByIdRequest updateReservationPackagesRequest
  ) {
    Map<Object, List<PackagesSelection>> newRoomsSelectionsMap;
    var filteredReservationPackagesRequest = updateReservationPackagesRequest.getRoomsSelections().stream()
        .filter(roomSelection -> roomSelection.getReservationId() != null
            && roomSelection.getPackagesSelection() != null);
    newRoomsSelectionsMap = filteredReservationPackagesRequest
            .collect(Collectors.toMap(
                RoomsSelectionsByReservationId::getReservationId,
                    RoomsSelectionsByReservationId::getPackagesSelection));
    return newRoomsSelectionsMap;
  }

  private boolean isRoomStayUpdated(ReservationByIdResponse original,
      UpdateReservationRequest update) {
    var originalRoom = original.getRoomStay();
    var updatedRoom = update.getRoomStay();

    return !originalRoom.getAdultsNumber().equals(updatedRoom.getRoomOccupancy().getAdultCount())
        || !originalRoom.getChildrenNumber().equals(updatedRoom.getRoomOccupancy().getChildCount())
        || !originalRoom.getRoomType().equals(updatedRoom.getRoomRates().get(0).getOperaRoomType());
  }

  private void deleteRemovedRooms(List<ReservationByIdResponse> originalReservations,
      List<UpdateReservationsRequest> updatedReservations) {
    // Retrieve updated reservations ids
    List<String> updatedReservationsIds = getReservationIds(updatedReservations);

    // Check if the original reservations contain a reservation that was removed
    List<ReservationByIdResponse> removedReservations = originalReservations.stream()
        .filter(reservation -> !updatedReservationsIds.contains(reservation.getReservationId()))
        .toList();

    // Remove the reservation from the original ones
    removedReservations.forEach(originalReservations::remove);
  }

  private List<String> getReservationIds(List<UpdateReservationsRequest> updatedReservations) {
    return updatedReservations.stream()
        .map(UpdateReservationsRequest::getReservations)
        .flatMap(Collection::stream)
        .map(UpdateReservationRequest::getReservationId)
        .toList();
  }
}