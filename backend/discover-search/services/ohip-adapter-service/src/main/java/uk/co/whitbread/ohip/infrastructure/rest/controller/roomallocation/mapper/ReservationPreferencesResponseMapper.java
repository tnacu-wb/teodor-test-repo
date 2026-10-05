package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.KioskReservationPreferencesDto;

@Mapper(componentModel = "spring")
public interface ReservationPreferencesResponseMapper {

  KioskReservationPreferencesDto toDto(KioskReservationPreferences kioskReservationPreferences);

}
