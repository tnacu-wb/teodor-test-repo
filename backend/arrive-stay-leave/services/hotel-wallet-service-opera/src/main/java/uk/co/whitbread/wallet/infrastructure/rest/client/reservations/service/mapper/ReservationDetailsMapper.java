package uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.mapper;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.hotel.generated.models.reservation.RateInfoDetailsDto;
import uk.co.whitbread.hotel.generated.models.reservation.RateInfoDto;
import uk.co.whitbread.hotel.generated.models.reservation.RateInfoSummaryDto;
import uk.co.whitbread.hotel.generated.models.reservation.ReservationByIdDto;
import uk.co.whitbread.wallet.domain.model.out.ReservationDetails;

@Mapper(componentModel = "spring")
public abstract class ReservationDetailsMapper {

  private static final String RATE_FLEX = "FLEXRATE";

  private static final String CONTACT_CENTRE_FLEX = "0333 003 8101";
  private static final String CONTACT_CENTRE_NON_FLEX = "0333 003 0025";

  @Mapping(source = "reservationBooker.title", target = "title")
  @Mapping(source = "reservationBooker.firstName", target = "firstName")
  @Mapping(source = "reservationBooker.lastName", target = "lastName")
  @Mapping(source = "roomStay.arrivalDate", target = "arrivalDate")
  @Mapping(source = "roomStay.departureDate", target = "departureDate")
  @Mapping(source = "roomStay.checkInTime", target = "checkInTime")
  @Mapping(source = "roomStay.checkOutTime", target = "checkOutTime")
  @Mapping(source = ".", target = "contactCentre", qualifiedByName = "getContactCentre")
  public abstract ReservationDetails toModel(ReservationByIdDto reservationByIdDto);

  @Named("getContactCentre")
  protected String getContactCentre(ReservationByIdDto reservationByIdDto) {
    return Optional.ofNullable(reservationByIdDto.getRateInfo())
        .map(RateInfoDto::getSummary)
        .map(RateInfoSummaryDto::getDetails)
        .map(rateInfoDetailsDtos -> rateInfoDetailsDtos.get(0))
        .map(RateInfoDetailsDto::getRatePlanCode).orElse(EMPTY)
        .equals(RATE_FLEX) ? CONTACT_CENTRE_NON_FLEX : CONTACT_CENTRE_FLEX;
  }

}
