package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsReservations;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingReservation;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingStayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.PhoneTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public interface SearchBookingsResponseOhipMapper {

  @Mapping(expression = "java(toReservationDetailsResponseForModel(reservationsByBasketRef, profilesByIds))",
      target = "bookings")
  SearchBookingsResponse toBookingSearchResponseModel(
      ReservationsDetailsReservations reservationsDetailsOhip,
      Map<String, List<ReservationInfoType>> reservationsByBasketRef,
      Map<String, Profile> profilesByIds);

  @Named("toReservationDetailsResponseForModel")
  default List<SearchBooking> toReservationDetailsResponseForModel(
      Map<String, List<ReservationInfoType>> reservationsByBasketRef,
      Map<String, Profile> profilesByIds) {

    var bookingSearchReservationDetails = new ArrayList<SearchBooking>();

    reservationsByBasketRef.forEach((key, reservationInfoTypeList) -> {

      var reservations = new ArrayList<SearchBookingReservation>();

      reservationInfoTypeList.forEach(reservationInfoType -> {
        SearchBookingStayingGuest stayingGuest = SearchBookingStayingGuest.builder()
            .profileId(reservationInfoType.getReservationGuest().getId())
            .title(reservationInfoType.getReservationGuest().getNameTitle() != null
                ? reservationInfoType.getReservationGuest().getNameTitle() : "")
            .firstName(reservationInfoType.getReservationGuest().getGivenName())
            .lastName(reservationInfoType.getReservationGuest().getSurname())
            .build();

        var bookingSearchReservation = SearchBookingReservation.builder()
            .reservationId(extractReservationId(reservationInfoType.getReservationIdList()))
            .confirmationId(extractConfirmationId(reservationInfoType.getReservationIdList()))
            .cancellationId(extractCancellationId(reservationInfoType.getReservationIdList()))
            .stayingGuest(stayingGuest)
            .build();

        reservations.add(bookingSearchReservation);
      });

      Optional<String> profileIdForBooker = reservationInfoTypeList.get(0).getAttachedProfiles()
          .stream()
          .filter(profile -> profile.getReservationProfileType().getValue()
              .equalsIgnoreCase(OhipConstants.RESERVATION_CONTACT))
          .findFirst().map(profile -> profile.getProfileIdList().get(0).getId());

      Profile bookerProfile =
          profileIdForBooker.map(profilesByIds::get).orElse(null);

      SearchBookingBooker booker;
      if (bookerProfile != null && bookerProfile.getProfileDetails() != null) {

        final var email = bookerProfile.getProfileDetails().getEmails() != null && !CollectionUtils.isEmpty(
            bookerProfile.getProfileDetails().getEmails().getEmailInfo()) ? bookerProfile.getProfileDetails()
            .getEmails().getEmailInfo().get(0).getEmail().getEmailAddress() : null;
        final var company =
            bookerProfile.getProfileDetails().getCompany() != null ? bookerProfile.getProfileDetails().getCompany()
                .getCompanyName() : null;
        final var mobile = bookerProfile.getProfileDetails().getTelephones() != null && !CollectionUtils.isEmpty(
            bookerProfile.getProfileDetails().getTelephones().getTelephoneInfo()) ? bookerProfile.getProfileDetails()
            .getTelephones().getTelephoneInfo().stream()
            .filter(telephoneInfoType -> PhoneTypeEnumDto.MOBILE.name().equals(telephoneInfoType.getType())).findFirst()
            .map(telephoneInfoType -> telephoneInfoType.getTelephone().getPhoneNumber()).orElse(null) : null;
        final var landline = bookerProfile.getProfileDetails().getTelephones() != null && !CollectionUtils.isEmpty(
            bookerProfile.getProfileDetails().getTelephones().getTelephoneInfo()) ? bookerProfile.getProfileDetails()
            .getTelephones().getTelephoneInfo().stream()
            .filter(telephoneInfoType -> PhoneTypeEnumDto.HOME.name().equals(telephoneInfoType.getType())).findFirst()
            .map(telephoneInfoType -> telephoneInfoType.getTelephone().getPhoneNumber()).orElse(null) : null;
        final var postcode = bookerProfile.getProfileDetails().getAddresses() != null && !CollectionUtils.isEmpty(
            bookerProfile.getProfileDetails().getAddresses().getAddressInfo()) ? bookerProfile.getProfileDetails()
            .getAddresses().getAddressInfo().get(0).getAddress().getPostalCode() : null;

        booker = SearchBookingBooker.builder()
            .profileId(bookerProfile.getProfileIdList().get(0).getId())
            .title(bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getNameTitle() != null
                ? bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getNameTitle() : "")
            .firstName(bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getGivenName())
            .lastName(bookerProfile.getProfileDetails().getCustomer().getPersonName().get(0).getSurname())
            .email(email)
            .company(company)
            .landline(landline)
            .mobile(mobile)
            .postcode(postcode)
            .build();
      } else {
        booker = null;
      }

      var reservationDetails = SearchBooking.builder()
          .bookingReference(key)
          .hotelId(reservationInfoTypeList.get(0).getHotelId())
          .hotelName(reservationInfoTypeList.get(0).getHotelName())
          .status(reservationInfoTypeList.get(0).getReservationStatus().getValue())
          .booker(booker)
          .arrivalDate(reservationInfoTypeList.get(0).getRoomStay().getArrivalDate())
          .departureDate(reservationInfoTypeList.get(0).getRoomStay().getDepartureDate())
          .reservations(reservations)
          .build();

      bookingSearchReservationDetails.add(reservationDetails);
    });

    bookingSearchReservationDetails.sort(
        Comparator.comparing(SearchBooking::getArrivalDate).reversed());
    return bookingSearchReservationDetails;
  }

  private String extractReservationId(List<UniqueIDType> reservationIdList) {
    Optional<String> reservationId = reservationIdList.stream()
        .filter(uniqueIDType -> uniqueIDType.getType().equalsIgnoreCase(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
        .map(UniqueIDType::getId).findFirst();

    return reservationId.orElse(null);
  }

  private String extractConfirmationId(List<UniqueIDType> reservationIdList) {
    Optional<String> confirmationId = reservationIdList.stream()
        .filter(uniqueIDType -> uniqueIDType.getType().equalsIgnoreCase(UniqueIdTypeEnumDto.CONFIRMATION_TYPE.value()))
        .map(UniqueIDType::getId).findFirst();

    return confirmationId.orElse(null);
  }

  private String extractCancellationId(List<UniqueIDType> reservationIdList) {
    Optional<String> cancellationId = reservationIdList.stream()
        .filter(uniqueIDType -> uniqueIDType.getType().equalsIgnoreCase(UniqueIdTypeEnumDto.CANCELLATION_TYPE.value()))
        .map(UniqueIDType::getId).findFirst();

    return cancellationId.orElse(null);
  }

}
