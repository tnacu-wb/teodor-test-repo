package uk.co.whitbread.infrastructure.rest.controller.availability.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilityRequestDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Component("availabilityPermissionEvaluator")
@RequiredArgsConstructor
public class PermissionEvaluator {

  private final AuthenticatedUserService authenticatedUserService;
  private static final String BB_CHANNEL = "BB";

  public boolean hasAccess(HotelAvailabilityRequestDto request) {
    return !BB_CHANNEL.equals(request.getChannel())
        || (authenticatedUserService.isUserAuthenticated() && hasAccessLevel(request));
  }

  private boolean hasAccessLevel(HotelAvailabilityRequestDto request) {
    return authenticatedUserService.getCurrentUserAccount()
        .flatMap(account -> AccessLevel.lookupByName(account.getAccessLevel()))
        .map(accessLevel -> request.getRoomTypes().size() <= accessLevel.getMaxAvailabilities())
        .orElse(false);
  }

}
