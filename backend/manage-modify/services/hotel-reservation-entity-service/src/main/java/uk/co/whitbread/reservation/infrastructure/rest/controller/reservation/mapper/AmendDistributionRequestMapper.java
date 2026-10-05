package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CustomerType;
import uk.co.whitbread.reservation.domain.model.in.EmailInfoType;
import uk.co.whitbread.reservation.domain.model.in.EmailType;
import uk.co.whitbread.reservation.domain.model.in.LeadGuest;
import uk.co.whitbread.reservation.domain.model.in.PersonNameType;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeEmails;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomOccupancyRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomRateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomStayRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdatedReservationsDistribution;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AmendDistributionReservationDto;

@Mapper(componentModel = "spring")
public interface AmendDistributionRequestMapper {

  @Mapping(target = "reservations", source = "amendDistributionRequestDto",
      qualifiedByName = "toReservationRequest")
  ReservationRequest toReservationRequestModel(
      AmendDistributionRequestDto amendDistributionRequestDto);

  @Named("toReservationRequest")
  default List<Reservation> toReservationListRequestModel(
      AmendDistributionRequestDto amendDistributionRequestDto) {
    List<Reservation> reservationList = new ArrayList<>();
    amendDistributionRequestDto.getReservations().forEach(reservationById ->
        reservationList.add(Reservation.builder()
            .externalReferenceId(reservationById.getReservationId())
            .hotelId(reservationById.getHotelId())
            .roomRates(RoomRate.builder()
                .pmsRoomType(reservationById.getRoomStay().getRoomType())
                .operaRoomType(reservationById.getRoomStay().getOperaRoomType())
                .startDate(reservationById.getRoomStay().getArrivalDate())
                .endDate(reservationById.getRoomStay().getDepartureDate())
                .ratePlanCode(reservationById.getRoomStay().getRatePlanCode())
                .specialRequests(reservationById.getRoomStay().getSpecialRequests())
                .build())
            .adultsNumber(reservationById.getRoomStay().getAdultsNumber())
            .childrenNumber(reservationById.getRoomStay().getChildrenNumber())
            .cotRequired(reservationById.getRoomStay().isCot())
            .leadGuest(injectLeadGuest(reservationById))
            .build())
    );

    return reservationList;
  }

  private LeadGuest injectLeadGuest(AmendDistributionReservationDto reservation) {
    if (Objects.isNull(reservation.getReservationGuestList())
        || reservation.getReservationGuestList().isEmpty()) {
      return null;
    }
    return LeadGuest.builder()
        .title(reservation.getReservationGuestList().get(0).getNameTitle())
        .firstName(reservation.getReservationGuestList().get(0).getGivenName())
        .lastName(reservation.getReservationGuestList().get(0).getSurName())
        .emailAddress(reservation.getReservationGuestList().get(0).getEmail())
        .build();
  }

  @Mapping(expression = "java(toUpdateReservationsRequestModel(amendDistributionRequestDto))",
      target = "updatedReservations")
  UpdatedReservationsDistribution toUpdateReservationsDistrModel(
      AmendDistributionRequestDto amendDistributionRequestDto);

  default List<UpdateReservationsRequest> toUpdateReservationsRequestModel(
      AmendDistributionRequestDto amendDistributionRequestDto) {
    List<UpdateReservationsRequest> updateReservationsRequests = new ArrayList<>();
    amendDistributionRequestDto.getReservations().forEach(reservation ->
        updateReservationsRequests.add(
            UpdateReservationsRequest.builder()
                .reservations(injectUpdateReservationRequestList(reservation))
                .token(amendDistributionRequestDto.getToken())
                .bookingChannel(BookingChannel.builder()
                    .channel(amendDistributionRequestDto.getBookingChannel().getChannel())
                    .subchannel(amendDistributionRequestDto.getBookingChannel().getSubchannel())
                    .language(amendDistributionRequestDto.getBookingChannel().getLanguage())
                    .build())
                .distributionIATANumber(amendDistributionRequestDto.getDistributionIATANumber())
                .build()));

    return updateReservationsRequests;
  }

  private List<UpdateReservationRequest> injectUpdateReservationRequestList(
      AmendDistributionReservationDto amendDistributionReservationDto) {
    List<UpdateReservationRequest> updateReservationRequests = new ArrayList<>();
    updateReservationRequests.add(
        UpdateReservationRequest.builder()
            .reservationId(amendDistributionReservationDto.getReservationId())
            .roomStay(mapRoomStay(amendDistributionReservationDto))
            .reservationGuests(buildReservationGuests(amendDistributionReservationDto))
            .build());

    return updateReservationRequests;
  }

  private UpdateRoomStayRequest mapRoomStay(
      AmendDistributionReservationDto amendDistributionReservationDto) {
    return UpdateRoomStayRequest.builder()
        .arrivalDate(amendDistributionReservationDto.getRoomStay().getArrivalDate())
        .departureDate(amendDistributionReservationDto.getRoomStay().getDepartureDate())
        .roomRates(buildRoomRates(amendDistributionReservationDto))
        .roomOccupancy(buildRoomOccupancy(amendDistributionReservationDto))
        .build();
  }

  private UpdateRoomOccupancyRequest buildRoomOccupancy(
      AmendDistributionReservationDto reservationDto) {
    return UpdateRoomOccupancyRequest.builder()
        .adultCount(reservationDto.getRoomStay().getAdultsNumber())
        .childCount(reservationDto.getRoomStay().getChildrenNumber())
        .build();
  }

  private List<UpdateRoomRateRequest> buildRoomRates(
      AmendDistributionReservationDto reservationDto) {
    List<UpdateRoomRateRequest> roomRates = new ArrayList<>();
    var roomRate = UpdateRoomRateRequest.builder()
        .roomOccupancy(buildRoomOccupancy(reservationDto))
        .roomType(reservationDto.getRoomStay().getRoomType())
        .operaRoomType(reservationDto.getRoomStay().getOperaRoomType())
        .startDate(reservationDto.getRoomStay().getArrivalDate())
        .endDate(reservationDto.getRoomStay().getDepartureDate())
        .build();

    roomRates.add(roomRate);

    return roomRates;
  }

  private List<ReservationGuests> buildReservationGuests(
      AmendDistributionReservationDto reservationDto) {
    if (Objects.isNull(reservationDto.getReservationGuestList())
        || reservationDto.getReservationGuestList().isEmpty()) {
      return new ArrayList<>();
    }

    var reservationGuest = ReservationGuests.builder()
        .profileInfo(buildProfileInfo(reservationDto))
        .build();

    return List.of(reservationGuest);
  }

  private ProfileInfo buildProfileInfo(AmendDistributionReservationDto reservationDto) {
    ProfileType profile = new ProfileType();
    profile.setCustomer(buildCustomerType(reservationDto));

    if (reservationDto.getReservationGuestList().get(0).getEmail() != null
        && !reservationDto.getReservationGuestList().get(0).getEmail().isEmpty()) {
      profile.setEmails(buildProfileTypeEmails(reservationDto));
    }

    ProfileInfo profileInfo = new ProfileInfo();
    profileInfo.setProfile(profile);

    return profileInfo;
  }

  private CustomerType buildCustomerType(AmendDistributionReservationDto reservationDto) {
    return CustomerType.builder()
        .personName(List.of(PersonNameType.builder()
            .givenName(reservationDto.getReservationGuestList().get(0).getGivenName())
            .surname(reservationDto.getReservationGuestList().get(0).getSurName())
            .nameTitle(reservationDto.getReservationGuestList().get(0).getNameTitle())
            .build()))
        .build();
  }

  private ProfileTypeEmails buildProfileTypeEmails(AmendDistributionReservationDto reservationDto) {
    return  ProfileTypeEmails.builder()
        .emailInfo(List.of(EmailInfoType.builder()
            .email(EmailType.builder()
                .emailAddress(reservationDto.getReservationGuestList().get(0).getEmail())
                .build())
            .build()))
        .build();
  }
}