package uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.KioskProfileInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationGuests;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservations;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.RawRequestDto;

@Mapper(componentModel = "spring")
public interface AddProfileRequestMapper {

  @Mapping(target = "reservations", source = "rawRequestDto", qualifiedByName = "reservationsMapping")
  AddProfileRequest toAddProfileRequestModel(
      RawRequestDto rawRequestDto);

  @Named("reservationsMapping")
  default List<ProfileReservations> toReservationsMappingModel(RawRequestDto rawRequestDto) {
    List<ProfileReservationGuests> reservationGuests = new ArrayList<>();
    boolean primary = true;
    for (String profileId : rawRequestDto.getProfileId()) {
      reservationGuests.add(ProfileReservationGuests.builder()
          .profileInfo(KioskProfileInfo.builder()
              .profileIdList(Collections.singletonList(
                  ProfileIdList.builder()
                      .type("Profile")
                      .id(profileId).build())).build())
          .primary(primary).build());
      primary = false;
    }
    return Collections.singletonList(ProfileReservations.builder()
        .reservationIdList(Collections.singletonList(
            ProfileReservationIdList.builder()
                .id(rawRequestDto.getReservationId())
                .type("Reservation").build()))
        .reservationGuests(reservationGuests).build());

  }

}
