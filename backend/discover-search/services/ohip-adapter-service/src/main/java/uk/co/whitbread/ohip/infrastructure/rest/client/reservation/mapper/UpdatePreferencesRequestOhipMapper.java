package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdatePreferencesRequestOhipMapper {
  public static final String RESERVATION_TYPE = "Reservation";

  @Mapping(expression = "java(injectReservationDetails(reservationPreferencesRequest, currentReservationId))",
      target = "reservations")
  public abstract ChangeReservation toDto(ReservationPreferencesRequest reservationPreferencesRequest,
      String currentReservationId);

  protected List<HotelReservationInstructionType> injectReservationDetails(
      ReservationPreferencesRequest reservationPreferencesRequest, String currentReservationId) {

    final List<PreferenceTypeType> preferencesCollections = reservationPreferencesRequest
        .getPreferencesCollections()
        .stream()
        .map(preferencesCollection -> {
          final PreferenceTypeType preferenceTypeType = new PreferenceTypeType();
          preferenceTypeType.setPreferenceType(preferencesCollection.getPreferenceType());
          preferenceTypeType.setPreference(preferencesCollection.getPreferences().stream().map(preference -> {
            final PreferenceType preferenceType = new PreferenceType();
            preferenceType.setPreferenceValue(preference);
            return preferenceType;
          }).toList());
          return preferenceTypeType;
        }).toList();

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setHotelId(reservationPreferencesRequest.getHotelId());
    List<UniqueIDType> reservationIdList = reservationPreferencesRequest.getReservationsIds()
        .stream()
        .filter(reservationId -> reservationId.equals(currentReservationId))
        .map(reservationId -> {
          final UniqueIDType uniqueIDType = new UniqueIDType();
          uniqueIDType.setId(reservationId);
          uniqueIDType.setType(RESERVATION_TYPE);
          return uniqueIDType;
        }).toList();
    hotelReservation.setReservationIdList(reservationIdList);
    hotelReservation.setPreferenceCollection(preferencesCollections);

    return List.of(hotelReservation);
  }
}
