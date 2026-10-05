package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;


import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.slots.Session;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Times;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionDatesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.TimesDto;

@Mapper(componentModel = "spring")
public interface SlotsResponseMapper {

  @Mapping(target = "dates", expression = "java(toResponseDto(sessionResponse))")
  SessionResponseDto toDto(SessionResponse sessionResponse);

  default List<SessionDatesDto> toResponseDto(SessionResponse sessionResponse) {
    if (sessionResponse.getDates() == null) {
      return new ArrayList<>();
    }
    return sessionResponse.getDates().stream().map(
        sessionDates -> SessionDatesDto.builder().date(sessionDates.getDate())
            .lunchAvailable(sessionDates.isLunchAvailable())
            .breakFastAvailable(sessionDates.isBreakFastAvailable())
            .dinnerAvailable(sessionDates.isDinnerAvailable())
            .sessionDto(toSessionDto(sessionDates.getSession())).build()).toList();
  }

  default SessionDto toSessionDto(Session session) {
    return SessionDto.builder()
        .breakFast(toTimesListDto(session.getBreakFast()))
        .lunch(toTimesListDto(session.getLunch()))
        .dinner(toTimesListDto(session.getDinner()))
        .build();
  }

  default List<TimesDto> toTimesListDto(List<Times> timesList) {
    return timesList.stream()
        .map(times -> TimesDto.builder().time(times.getTime())
            .canEnquire(times.isCanEnquire())
            .isClosed(times.isClosed())
            .remainingCapacity(times.getRemainingCapacity())
            .totalCapacity(times.getTotalCapacity())
            .available(times.isAvailable()).build())
        .toList();
  }

}

