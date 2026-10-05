package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_08;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UpdateReservationCcAgentIdRequestOhipMapper {

  @Mapping(expression = "java(injectReservationDetails(updateReservationCcAgentIdRequest))",
      target = "reservations")
  public abstract ChangeReservation toDto(UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest);

  protected List<HotelReservationInstructionType> injectReservationDetails(
      UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest) {

    final var characterUDF = new CharacterUDFType();
    characterUDF.setName(UDFC_08);
    characterUDF.setValue(updateReservationCcAgentIdRequest.getCcAgentId());

    final var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(List.of(characterUDF));

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setHotelId(updateReservationCcAgentIdRequest.getHotelId());
    hotelReservation.setUserDefinedFields(userDefinedFields);

    return List.of(hotelReservation);
  }
}
