package uk.co.whitbread.wallet.infrastructure.rest.client.reservations;

import static uk.co.whitbread.wallet.infrastructure.rest.client.config.WalletConstants.IDCONTEXT;
import static uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.Utils.capitalizeFirstLetter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByBasketRefResponseDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByIdDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.wallet.ErrorCode;
import uk.co.whitbread.wallet.domain.exception.ReservationNotFoundException;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.model.out.ReservationDetails;
import uk.co.whitbread.wallet.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.HotelReservationsClient;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.mapper.ReservationDetailsMapper;

@RequiredArgsConstructor
@Slf4j
public class HotelReservationOutPortImpl implements HotelReservationOutPort {

  private static final String HSCKIN = "HSCKIN";
  private final HotelReservationsClient hotelReservationsClient;
  private final ReservationDetailsMapper reservationDetailsMapper;


  @Override
  public FindBookingResponseDto findBooking(WalletRequest walletRequest) {
    return Optional.ofNullable(hotelReservationsClient.getBasketReference(walletRequest))
        .orElseThrow(() -> {
          ReservationNotFoundException reservationNotFoundException = new ReservationNotFoundException(
              ErrorCode.DIGITAL_BOOKING_NOT_FOUND_FOR_RESERVATION_EXCEPTION,
              String.format("Booking not found for wallet request %s", walletRequest));
          ExceptionLogger.log(log, reservationNotFoundException);
          return reservationNotFoundException;
        });
  }

  @Override
  public ReservationDetails getReservationDetails(String basketReference, String earlyCheckIn) {
    ReservationByBasketRefResponseDto reservationByBasketRefResponseDto = Optional.ofNullable(
        hotelReservationsClient.getReservationDetails(
            basketReference)).orElseThrow(() -> {
              ReservationNotFoundException reservationNotFoundException = new ReservationNotFoundException(
                  ErrorCode.DIGITAL_DETAILS_NOT_FOUND_FOR_BASKET_EXCEPTION,
                  String.format("Reservation details not found for basket reference %s",
                      basketReference));
              ExceptionLogger.log(log, reservationNotFoundException);
              return reservationNotFoundException;
            });

    if (reservationByBasketRefResponseDto.getReservationByIdList() == null) {
      return ReservationDetails.builder().build();
    }

    ReservationByIdDto reservationByIdDto = reservationByBasketRefResponseDto.getReservationByIdList()
        .stream()
        .findFirst().orElse(new ReservationByIdDto());

    ReservationDetails model = reservationDetailsMapper.toModel(reservationByIdDto);
    if (isBookingWithEco(reservationByBasketRefResponseDto.getReservationByIdList())) {
      model.setCheckInTime(earlyCheckIn);
    }

    model.setConfirmationNumber(reservationByBasketRefResponseDto.getBookingReference());
    model.setHotelId(reservationByBasketRefResponseDto.getHotelId());

    if (IDCONTEXT.equals(reservationByBasketRefResponseDto.getIdContext())
        && model.getTitle() == null && model.getLastName() == null) {
      var thirdPartyGuest = getThirdPartyGuest(reservationByBasketRefResponseDto);
      model.setTitle(thirdPartyGuest.firstName());
      model.setLastName(thirdPartyGuest.lastName());
    }
    model.setTitle(capitalizeFirstLetter(model.getTitle()));
    model.setLastName(capitalizeFirstLetter(model.getLastName()));

    return model;
  }

  private  boolean isBookingWithEco(List<ReservationByIdDto> reservations) {
    var earlyCheckInRes = reservations.stream()
        .map(ReservationByIdDto::getReservationPackageList)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(ReservationPackagesDetailsResponseDto::getPackageCode)
        .filter(Objects::nonNull)
        .filter(packageCode -> packageCode.equals(HSCKIN))
        .count();

    return earlyCheckInRes > 0 && earlyCheckInRes == reservations.size();
  }

  private GuestName getThirdPartyGuest(
      ReservationByBasketRefResponseDto reservationByBasketRefResponseDto) {
    List<ReservationByIdDto> reservationByIdList = reservationByBasketRefResponseDto.getReservationByIdList();
    var isGuestInfoAvailable = Optional.ofNullable(reservationByIdList)
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(0))
        .map(ReservationByIdDto::getReservationGuestList)
        .filter(guestList -> !guestList.isEmpty())
        .isPresent();
    if (isGuestInfoAvailable) {
      return new GuestName(
          reservationByIdList.get(0).getReservationGuestList().get(0).getGivenName(),
          reservationByIdList.get(0).getReservationGuestList().get(0).getSurName());
    }
    return new GuestName(null, null);
  }

}
