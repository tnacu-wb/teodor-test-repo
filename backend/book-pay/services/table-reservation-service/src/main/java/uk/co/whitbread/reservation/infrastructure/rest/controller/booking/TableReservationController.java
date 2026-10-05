package uk.co.whitbread.reservation.infrastructure.rest.controller.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.ports.primary.TableReservationInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.EnquiryResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.EventResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.MenuResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.OccasionResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.OutletResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.SlotsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EnquiryResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EventsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.OutletResponseDto;

@RequestMapping(value = "event/v1")
@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TableReservationController implements TableReservationApi {

  private final TableReservationInPort tableReservationInPort;

  private final SlotsResponseMapper slotsResponseMapper;
  private final OutletResponseMapper outletResponseMapper;
  private final EventResponseMapper eventResponseMapper;
  private final EnquiryResponseMapper enquiryResponseMapper;
  private final OccasionResponseMapper occasionResponseMapper;
  private final MenuResponseMapper menuResponseMapper;


  @GetMapping("/slots")
  public SessionResponseDto slots(@RequestParam String from,
      @RequestParam String until, @RequestParam String time, @RequestParam String adult,
      @RequestParam String children,
      @RequestParam String siteId) {
    SessionResponse sessionResponse = tableReservationInPort.slots(from, until, time,
        adult,
        children,
        siteId);
    log.info("SessionResponse :::::{}", sessionResponse);
    SessionResponseDto sessionResponseDto = slotsResponseMapper.toDto(sessionResponse);
    log.info("SessionResponseDto :::::{}", sessionResponseDto);
    return sessionResponseDto;
  }

  @GetMapping("/outlets")
  @Cacheable(value = "outletCache")
  public OutletResponseDto outlets(String location, String id) {
    OutletResponse outletResponse = tableReservationInPort.outlets(location, id);
    log.info("OutletResponse :::::{}", outletResponse);
    OutletResponseDto outletResponseDto = outletResponseMapper.toDto(outletResponse);
    log.info("OutletResponse :::::{}", outletResponseDto);
    return outletResponseDto;
  }

  @PostMapping("/events")
  public EventsResponseDto events(@RequestBody EventOrEquiryRequest eventRequest) {
    EventsResponse events = tableReservationInPort.events(eventRequest);
    return eventResponseMapper.toDto(events);
  }

  @GetMapping("/events/{eventId}")
  public EventsResponseDto getEvent(@PathVariable String eventId) {
    EventsResponse events = tableReservationInPort.getEvent(eventId);
    return eventResponseMapper.toDto(events);
  }

  @PostMapping("/enquiry")
  public EnquiryResponseDto enquiry(@RequestBody EventOrEquiryRequest eventOrEquiryRequest) {
    EnquiryResponse enquiry = tableReservationInPort.createEnquiry(eventOrEquiryRequest);
    return enquiryResponseMapper.toDto(enquiry);
  }

  @GetMapping("/enquiry/{enquiryId}")
  public EnquiryResponseDto getEnquiry(@PathVariable String enquiryId) {
    EnquiryResponse enquiry = tableReservationInPort.getEnquiry(enquiryId);
    return enquiryResponseMapper.toDto(enquiry);
  }

  @GetMapping("/occasions")
  public OccasionsResponseDto occasions(String from, String until,
      String siteId) {
    OccasionsResponse occasionsResponse = tableReservationInPort.occasions(from, until, siteId);
    return occasionResponseMapper.toDto(occasionsResponse);
  }


  @GetMapping("/menus")
  public MenuResponseDto getMenu(@RequestParam String siteId, @RequestParam String from,
      @RequestParam String until, @RequestParam String time,
      @RequestParam(required = false) String ocassionId) {
    MenuResponse menuResponse = tableReservationInPort.getMenu(siteId, from, until, time,
        ocassionId);
    return menuResponseMapper.toDto(menuResponse);
  }
}
