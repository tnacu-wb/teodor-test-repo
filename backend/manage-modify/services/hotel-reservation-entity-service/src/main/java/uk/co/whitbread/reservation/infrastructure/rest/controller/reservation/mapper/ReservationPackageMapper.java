package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ConsumptionDetails;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackages;
import uk.co.whitbread.reservation.domain.model.in.ScheduleList;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPackageDto;

@Mapper(componentModel = "spring")
public interface ReservationPackageMapper {

  default ReservationPackages toModel(ReservationPackageDto reservationPackageDto) {
    ReservationPackages reservationPackage = new ReservationPackages();
    reservationPackage.setPackageCode(reservationPackageDto.getPackageCode());

    List<ScheduleList> schedule = new ArrayList<>();
    reservationPackageDto.getStartDate().datesUntil(reservationPackageDto.getEndDate()).forEach(date -> {
      ScheduleList scheduleList = new ScheduleList();
      scheduleList.setUnitPrice(reservationPackageDto.getUnitPrice());
      scheduleList.setReservationDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
      scheduleList.setConsumptionDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
      scheduleList.setTotalQuantity(reservationPackageDto.getQuantity());
      schedule.add(scheduleList);
    });
    reservationPackage.setScheduleList(schedule);

    ConsumptionDetails consumptionDetails = new ConsumptionDetails();
    consumptionDetails.setTotalQuantity(reservationPackageDto.getQuantity());
    consumptionDetails.setDefaultQuantity(1);
    reservationPackage.setConsumptionDetails(consumptionDetails);
    reservationPackage.setStartDate(
        reservationPackageDto.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
    reservationPackage.setEndDate(
        reservationPackageDto.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE));

    return reservationPackage;
  }

}
