package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;

@Mapper(componentModel = "spring")
public abstract class PackagesRequestDtoOhipMapper {

  @Mapping(target = "adults", constant = "1")
  @Mapping(target = "children", constant = "0")
  @Mapping(target = "startDate", source = "arrival")
  @Mapping(target = "endDate", source = "departure")
  @Mapping(expression = "java(mapCalculateNumberOfNights(reservationPackagesRequest))", target = "nrNights")
  public abstract PackagesRequestOhipDto toDto(ReservationPackagesRequest reservationPackagesRequest);

  protected Integer mapCalculateNumberOfNights(ReservationPackagesRequest reservationPackagesRequest) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    LocalDate startDate = LocalDate.parse(reservationPackagesRequest.getArrival(), formatter);
    LocalDate endDate = LocalDate.parse(reservationPackagesRequest.getDeparture(), formatter);
    Period period = Period.between(endDate, startDate);

    return Math.abs(period.getDays());
  }
}
