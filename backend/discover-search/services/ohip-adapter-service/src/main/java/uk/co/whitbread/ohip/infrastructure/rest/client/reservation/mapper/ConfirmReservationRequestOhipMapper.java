package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
    ConfirmReservationOhipMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ConfirmReservationRequestOhipMapper {

  private ConfirmReservationOhipMapper confirmReservationOhipMapper;

  @Autowired
  public void toConfirmReservationOhipMapperForModel(final ConfirmReservationOhipMapper confirmReservationOhipMapper) {
    this.confirmReservationOhipMapper = confirmReservationOhipMapper;
  }

  @Mapping(expression = "java(mapConfirmReservationTypes(confirmationRequest))", target = "reservations")
  public abstract ChangeReservation toChangeReservationModel(
      ConfirmReservationRequest confirmationRequest);

  protected List<HotelReservationInstructionType> mapConfirmReservationTypes(
      ConfirmReservationRequest confirmationRequest) {
    var reservationType = confirmReservationOhipMapper.fromDto(confirmationRequest);

    return List.of(reservationType);
  }
}
