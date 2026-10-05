package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.reservation.domain.model.in.AddressInfoType;
import uk.co.whitbread.reservation.domain.model.in.AddressType;
import uk.co.whitbread.reservation.domain.model.in.Country;
import uk.co.whitbread.reservation.domain.model.in.CustomerType;
import uk.co.whitbread.reservation.domain.model.in.EmailInfoType;
import uk.co.whitbread.reservation.domain.model.in.EmailType;
import uk.co.whitbread.reservation.domain.model.in.PersonNameType;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeAddresses;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeEmails;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomOccupancyRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomRateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomStayRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.EditRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.GuestAddressDto;


@Mapper(componentModel = "spring")
public interface EditRoomRequestMapper {

  @Mapping(target = "reservations", source = "editRoomRequestDto",
          qualifiedByName = "toReservationDetailsModel")
  UpdateReservationsRequest toModel(EditRoomRequestDto editRoomRequestDto);

  @Named("toReservationDetailsModel")
  default List<UpdateReservationRequest> toReservationDetailsModel(EditRoomRequestDto editRoomRequestDto) {
    var updateReservationRequest =
            UpdateReservationRequest.builder()
                    .reservationId(editRoomRequestDto.getReservationId())
                    .roomStay(mapRoomStay(editRoomRequestDto))
                    .reservationGuests(buildReservationGuests(editRoomRequestDto))
                    .build();

    return List.of(updateReservationRequest);
  }

  private UpdateRoomStayRequest mapRoomStay(EditRoomRequestDto editRoomRequestDto) {
    return UpdateRoomStayRequest.builder()
                    .roomRates(buildRoomRates(editRoomRequestDto))
                    .roomOccupancy(buildRoomOccupancy(editRoomRequestDto))
                    .build();
  }

  private UpdateRoomOccupancyRequest buildRoomOccupancy(EditRoomRequestDto editRoomRequestDto) {
    return UpdateRoomOccupancyRequest.builder()
            .adultCount(editRoomRequestDto.getRoomOccupancy().getAdultsNumber())
            .childCount(editRoomRequestDto.getRoomOccupancy().getChildrenNumber())
            .build();
  }

  private List<UpdateRoomRateRequest> buildRoomRates(EditRoomRequestDto editRoomRequestDto) {
    List<UpdateRoomRateRequest> roomRates = new ArrayList<>();
    var roomRate = UpdateRoomRateRequest.builder()
            .roomOccupancy(buildRoomOccupancy(editRoomRequestDto))
            .roomType(editRoomRequestDto.getRoomType())
            .build();

    roomRates.add(roomRate);

    return roomRates;
  }

  private List<ReservationGuests> buildReservationGuests(EditRoomRequestDto editRoomRequestDto) {
    var reservationGuest = ReservationGuests.builder()
            .profileInfo(buildProfileInfo(editRoomRequestDto))
            .build();

    return List.of(reservationGuest);
  }

  private ProfileInfo buildProfileInfo(EditRoomRequestDto editRoomRequestDto) {
    ProfileType profile = new ProfileType();
    profile.setCustomer(buildCustomerType(editRoomRequestDto));

    if (editRoomRequestDto.getLeadGuest().getEmailAddress() != null) {
      if (!editRoomRequestDto.getLeadGuest().getEmailAddress().isEmpty()) {
        profile.setEmails(buildProfileTypeEmails(editRoomRequestDto));
      }
    }
    if (editRoomRequestDto.getLeadGuest().getAddress() != null) {
      profile.setAddresses(
          buildProfileTypeAddresses(editRoomRequestDto));
    }

    ProfileInfo profileInfo = new ProfileInfo();
    profileInfo.setProfile(profile);

    return profileInfo;
  }

  private ProfileTypeAddresses buildProfileTypeAddresses(EditRoomRequestDto editRoomRequestDto) {
    var addressInfoType = AddressInfoType.builder()
        .address(buildAddressType(editRoomRequestDto.getLeadGuest().getAddress())).build();
    return ProfileTypeAddresses.builder()
        .addressInfo(List.of(addressInfoType)).build();
  }

  private AddressType buildAddressType(GuestAddressDto guestAddress) {
    List<String> addressLines = new ArrayList<>();
    Optional.ofNullable(guestAddress.getAddressLine1())
        .ifPresent(addressLine1 -> addressLines.add(addressLine1));
    Optional.ofNullable(guestAddress.getAddressLine2())
        .ifPresent(addressLine2 -> addressLines.add(addressLine2));
    Optional.ofNullable(guestAddress.getAddressLine3())
        .ifPresent(addressLine3 -> addressLines.add(addressLine3));
    Optional.ofNullable(guestAddress.getAddressLine4())
        .ifPresent(addressLine4 -> addressLines.add(addressLine4));
    return AddressType.builder()
        .type(guestAddress.getAddressType())
        .addressLine(addressLines)
        .cityName(guestAddress.getCityName())
        .postalCode(guestAddress.getPostalCode())
        .country(new Country(guestAddress.getCountryCode()))
        .build();
  }

  private CustomerType buildCustomerType(EditRoomRequestDto editRoomRequestDto) {
    return CustomerType.builder()
            .personName(List.of(PersonNameType.builder()
                    .givenName(editRoomRequestDto.getLeadGuest().getFirstName())
                    .surname(editRoomRequestDto.getLeadGuest().getLastName())
                    .nameTitle(editRoomRequestDto.getLeadGuest().getTitle())
                    .build()))
            .build();
  }

  private ProfileTypeEmails buildProfileTypeEmails(EditRoomRequestDto editRoomRequestDto) {
    return  ProfileTypeEmails.builder()
            .emailInfo(List.of(EmailInfoType.builder()
                            .email(EmailType.builder()
                                    .emailAddress(editRoomRequestDto.getLeadGuest().getEmailAddress())
                                    .build())
                            .build()))
                    .build();
  }
}
