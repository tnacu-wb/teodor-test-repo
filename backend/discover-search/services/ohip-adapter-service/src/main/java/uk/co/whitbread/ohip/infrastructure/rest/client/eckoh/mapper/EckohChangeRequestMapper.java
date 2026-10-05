package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in.EckohChangeRequestDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
    EckohWebhookOhipMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EckohChangeRequestMapper {

  private EckohWebhookOhipMapper eckohWebhookOhipMapper;

  @Autowired
  public void toChangeOhipMapperForModel(
      final EckohWebhookOhipMapper eckohWebhookOhipMapper) {
    this.eckohWebhookOhipMapper = eckohWebhookOhipMapper;
  }

  @Mapping(expression = "java(mapCangeReservationTypes(eckohChangeRequest))", target = "reservations")
  public abstract ChangeReservation toChangeReservationModel(
      EckohChangeRequestDto eckohChangeRequest);

  protected List<HotelReservationInstructionType> mapCangeReservationTypes(
      EckohChangeRequestDto eckohChangeRequest) {
    var reservationType = eckohWebhookOhipMapper.fromDto(eckohChangeRequest);

    return List.of(reservationType);
  }


}
