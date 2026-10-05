package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_08;

import java.util.List;
import java.util.StringJoiner;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateReservationOverrideReasonsRequestOhipMapper {

  @Mapping(expression = "java(injectReservationDetails(updateReservationOverrideReasonsRequest))",
      target = "reservations")
  public abstract ChangeReservation toDto(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest);

  protected List<HotelReservationInstructionType> injectReservationDetails(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest) {

    final var characterUDF = new CharacterUDFType();
    characterUDF.setName(UDFC_08);

    final var valueJoiner = new StringJoiner(",");
    valueJoiner.add(updateReservationOverrideReasonsRequest.getReasonCode());
    valueJoiner.add(updateReservationOverrideReasonsRequest.getReasonName());
    valueJoiner.add(updateReservationOverrideReasonsRequest.getCallerName());

    if (StringUtils.isNotBlank(updateReservationOverrideReasonsRequest.getManagerName())) {
      valueJoiner.add(updateReservationOverrideReasonsRequest.getManagerName());
    }

    characterUDF.setValue(valueJoiner.toString());

    final var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(List.of(characterUDF));

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setHotelId(updateReservationOverrideReasonsRequest.getHotelId());
    hotelReservation.setUserDefinedFields(userDefinedFields);

    return List.of(hotelReservation);
  }

}
