package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.security;

import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.BB_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.DISTR_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.PI_BOOKING_CHANNEL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Component("reservationPermissionEvaluator")
@RequiredArgsConstructor
public class PermissionEvaluator {
  private final AuthenticatedUserService authenticatedUserService;

  public boolean hasAccess(ReservationRequestDto request) {
    return !BB_BOOKING_CHANNEL.equals(request.getBookingChannel().getChannel())
        || (authenticatedUserService.isUserAuthenticated() && hasAccessLevel(request));
  }

  public boolean hasAccess(String channel) {
    return (PI_BOOKING_CHANNEL.equals(channel) || DISTR_BOOKING_CHANNEL.equals(channel))
        || authenticatedUserService.isUserAuthenticated();
  }

  private boolean hasAccessLevel(ReservationRequestDto request) {
    return authenticatedUserService.getCurrentUserAccount()
        .flatMap(account -> AccessLevel.lookupByName(account.getAccessLevel()))
        .map(accessLevel -> request.getReservations().size() <= accessLevel.getMaxReservations())
        .orElse(false);
  }
}
