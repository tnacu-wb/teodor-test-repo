package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.reservation.domain.model.in.BookerAddress;
import uk.co.whitbread.reservation.domain.model.in.GuestAddress;
import uk.co.whitbread.reservation.domain.model.in.LeadGuest;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.AddNewRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.GuestAddressDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AddNewRoomRequestMapper {

  @Mapping(target = "reservations", source = "reservationRequestDto",
          qualifiedByName = "toReservationRequest")
  ReservationRequest toModel(AddNewRoomRequestDto reservationRequestDto);

  @Named("toReservationRequest")
  default List<Reservation> toReservationListRequestModel(AddNewRoomRequestDto addNewRoomRequestDto) {
    List<Reservation> reservationList = new ArrayList<>();
    reservationList.add(Reservation.builder()
                     .externalReferenceId(addNewRoomRequestDto.getTempBookingRef())
                     .roomRates(RoomRate.builder().pmsRoomType(addNewRoomRequestDto.getRoomType())
                         .specialRequests(addNewRoomRequestDto.getSpecialRequests()).build())
                     .adultsNumber(addNewRoomRequestDto.getRoomOccupancy().getAdultsNumber())
                     .childrenNumber(addNewRoomRequestDto.getRoomOccupancy().getChildrenNumber())
                     .cotRequired(addNewRoomRequestDto.getRoomOccupancy().getCotRequired())
                     .leadGuest(LeadGuest.builder()
                         .title(addNewRoomRequestDto.getLeadGuest().getTitle())
                         .firstName(addNewRoomRequestDto.getLeadGuest().getFirstName())
                         .lastName(addNewRoomRequestDto.getLeadGuest().getLastName())
                         .emailAddress(addNewRoomRequestDto.getLeadGuest().getEmailAddress())
                         .language(addNewRoomRequestDto.getBookingChannel().getLanguage())
                         .address(toAddressModel(addNewRoomRequestDto.getLeadGuest().getAddress()))
                         .build())
             .build());

    return reservationList;
  }

  GuestAddress toAddressModel(GuestAddressDto bookerAddressDto);
}
