package uk.co.whitbread.ohip.domain.model.profile;

import java.util.Collections;
import java.util.List;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.KioskProfileInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationGuests;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservations;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.out.CreateProfileResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileLinks;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationDetailsResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.Reservation;

public class ProfileTestUtils {

  public static CreateProfileResponse mockCreateProfileResponse() {
    return CreateProfileResponse.builder().links(List.of(ProfileLinks.builder().href(
            "https://whitbce4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com/crm/v1/profiles/112")
        .build())).build();
  }

  public static AddProfileRequest mockAddProfileRequest() {
    return AddProfileRequest.builder()
        .reservations(List.of(ProfileReservations.builder().reservationIdList(List.of(
            ProfileReservationIdList.builder().id("12345").build())).reservationGuests(List.of(
            ProfileReservationGuests.builder().profileInfo(
                KioskProfileInfo.builder().profileIdList(List.of(ProfileIdList.builder().id("112")
                    .build())).build()).build())).build())).build();
  }

  public static ProfileReservationDetailsResponse mockgetProfileIdByReservation() {

    return ProfileReservationDetailsResponse.builder()
        .reservations(
            uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationsOut.builder()
                .reservation(
                    Collections.singletonList(Reservation.builder().reservationGuests(
                        Collections.singletonList(
                            uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationGuestsOut.builder()
                                .profileInfo(
                                    uk.co.whitbread.ohip.domain.model.profile.out.KioskProfileInfoOut.builder()
                                        .profileIdList(Collections.singletonList(
                                            uk.co.whitbread.ohip.domain.model.profile.out.ProfileIdList.builder()
                                                .id("12345").type("Primary").build())).build())
                                .build())).build()))
                .build()).
        build();
  }

  public static ProfileStayingGuestDetails mockStayingGuestDetails() {
    return ProfileStayingGuestDetails.builder().guestDetails(Collections.singletonList(
            GuestDetails.builder().givenName("Test").surname("Name").nameTitle("Mr").build()))
        .build();
  }

  public static GuestDetails mockGuestDetailsRequest() {
    return GuestDetails.builder().givenName("Test").nameTitle("Mr").surname("Name").build();
  }
}
