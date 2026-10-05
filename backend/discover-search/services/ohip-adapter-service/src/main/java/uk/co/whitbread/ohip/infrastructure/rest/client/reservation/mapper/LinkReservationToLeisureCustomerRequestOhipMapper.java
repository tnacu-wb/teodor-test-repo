package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PI_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_09;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_35;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.LinkReservationToLeisureCustomerRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class LinkReservationToLeisureCustomerRequestOhipMapper {

  @Mapping(expression = "java(injectReservationDetails(linkReservationToLeisureCustomerRequest))",
      target = "reservations")
  public abstract ChangeReservation toDto(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest);

  protected List<HotelReservationInstructionType> injectReservationDetails(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest) {

    final var customerAccountIdUDF = new CharacterUDFType();
    customerAccountIdUDF.setName(UDFC_35);
    customerAccountIdUDF.setValue(linkReservationToLeisureCustomerRequest.getCustomerAccountId());

    final var bookingTypeUDF = new CharacterUDFType();
    bookingTypeUDF.setName(UDFC_09);
    bookingTypeUDF.setValue(PI_CHANNEL);

    final var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(List.of(customerAccountIdUDF, bookingTypeUDF));

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setHotelId(linkReservationToLeisureCustomerRequest.getHotelId());
    hotelReservation.setUserDefinedFields(userDefinedFields);
    return List.of(hotelReservation);
  }
}
