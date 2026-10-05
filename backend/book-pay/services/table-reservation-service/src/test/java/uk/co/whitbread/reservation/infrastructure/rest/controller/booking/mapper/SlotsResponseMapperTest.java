package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.slots.Session;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionDates;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Times;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionDatesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionResponseDto;

@ExtendWith(MockitoExtension.class)
public class SlotsResponseMapperTest {

  @InjectMocks
  SlotsResponseMapperImpl mapper;


  @Test
  void testToSlotsResponseDto() {
    SessionDates sessionDates = new SessionDates();
    Session session = new Session();
    session.setDinner(asList(Times.builder().available(true).time("10:15").build()));
    session.setLunch(new ArrayList<>());
    session.setBreakFast(new ArrayList<>());
    sessionDates.setDinnerAvailable(true);
    sessionDates.setDate("12-10-2023");
    sessionDates.setSession(session);
    List<SessionDates> sessionDatesList = new ArrayList<>();
    sessionDatesList.add(sessionDates);
    SessionResponse sessionResponse = SessionResponse.builder().dates(sessionDatesList).build();

    SessionResponseDto result = mapper.toDto(sessionResponse);
    // Assert
    assertNotNull(result);
    assertEquals(1, result.getDates().size());
  }
  @Test
  void testToDtoWithNullInput(){
    SessionResponseDto result = mapper.toDto(null);
    assertNull(result);

  }

  @Test
  void testToResponseDtoWithNullInput() {
    SessionResponse sessionResponse = new SessionResponse(null);
    List<SessionDatesDto> result = mapper.toResponseDto(sessionResponse);
    assertNotNull(result);
    assertEquals(0, result.size());


  }


}
