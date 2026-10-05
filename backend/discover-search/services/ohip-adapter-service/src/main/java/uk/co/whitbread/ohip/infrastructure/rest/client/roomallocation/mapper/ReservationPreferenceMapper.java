package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskPreference;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskPreferenceCollection;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class ReservationPreferenceMapper {

  @Mapping(target = "kioskPreferenceCollection", expression = "java(injectKioskPreferenceCollection(reservation))")
  public abstract KioskReservationPreferences toModel(Reservation reservation);


  protected List<KioskPreferenceCollection> injectKioskPreferenceCollection(
      Reservation reservation) {

    List<PreferenceTypeType> preferenceCollection = reservation.getReservations().getReservation()
        .get(0).getPreferenceCollection();
    return preferenceCollection.stream()
        .filter(preferenceTypeType -> null != preferenceTypeType.getPreference())
        .map(preferenceTypeType -> KioskPreferenceCollection.builder()
            .kioskPreference(preferenceTypeType.getPreference().stream()
                .filter(Objects::nonNull)
                .map(preferenceType -> KioskPreference.builder()
                    .preferenceValue(preferenceType.getPreferenceValue())
                    .description(preferenceType.getDescription())
                    .build()).toList())
            .preferenceType(preferenceTypeType.getPreferenceType())
            .preferenceTypeDescription(preferenceTypeType.getPreferenceTypeDescription())
            .build()).toList();
  }

}
