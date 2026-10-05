package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ScheduleList;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ScheduleListOhipMapper {

  default List<ReservationPackageScheduleType> toDto(List<ScheduleList> scheduleList) {
    return null;
  }
}
