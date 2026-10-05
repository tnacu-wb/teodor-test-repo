package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Menu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.events.Consent;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.events.Menus;
import uk.co.whitbread.reservation.domain.model.out.events.OrderMenu;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResp;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EventsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenusDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OrderMenuDto;

@ExtendWith(MockitoExtension.class)
class EventResponseMapperTest {

  @InjectMocks
  EventResponseMapperImpl eventResponseMapper;
  @Test
  void testToDto() {
    EventsResponse eventsResponse = mockEventResponse();
    EventsResponseDto result = eventResponseMapper.toDto(eventsResponse);
    assertNotNull(result);
  }
  @Test
  void testToDto_WithNullData() {
    EventsResponseDto result = eventResponseMapper.toDto(null);
    assertNull(result);


  }
  @Test
  void testToDto_WithNullMenusData() {

    List<MenusDto> result = eventResponseMapper.toMenusDto(Collections.emptyList());
    assertEquals(Collections.emptyList(), result);

  }
  @Test
  void testToDto_WithNullIOrderData() {

    List<OrderMenuDto> result = eventResponseMapper.toOrderMenuDto(Collections.emptyList());
    assertEquals(Collections.emptyList(), result);

  }


  @Test
  void testToDtoWithNullInput() {
    EventsResponseDto result = eventResponseMapper.toDto(null);
    assertNull(result);
  }

  @Test
  void testToConsentDto() {
    Consent consent = mockConsent();
    ConsentDto result = eventResponseMapper.toConsentDto(consent);
    assertNotNull(result);


  }

  @Test
  void testToConsentDtoWithNullInput() {
    ConsentDto result = eventResponseMapper.toConsentDto(null);
    assertNull(result);
  }

  private EventsResponse mockEventResponse() {
    EventsResponse eventsResponse = new EventsResponse();
    eventsResponse.setAdults(2);
    eventsResponse.setAreaId("area123");
    eventsResponse.setAreaName("AreaName");
    eventsResponse.setBookingReference("ABCD1234");
    eventsResponse.setCancelLink("http://example.com/cancel");
    eventsResponse.setChildren(1);
    eventsResponse.setConsent(new Consent());
    eventsResponse.setDate("2023-09-08");
    eventsResponse.setEditLink("http://example.com/edit");
    eventsResponse.setEmailAddress("test@example.com");
    eventsResponse.setFirstname("John");
    eventsResponse.setId("123");
    eventsResponse.setLastname("Doe");
    eventsResponse.setName("EventName");
    eventsResponse.setOccasionId("456");
    eventsResponse.setOccasionName("Birthday");
    eventsResponse.setSiteId("789");
    eventsResponse.setSiteName("SiteName");
    eventsResponse.setSpecialRequest("SpecialRequest");
    eventsResponse.setTelephoneNumber("123-456-7890");
    eventsResponse.setSiteTimezone("GMT");
    eventsResponse.setTime("14:30");
    eventsResponse.setTurnTimeMinutes(60);
    Menus menu = new Menus();
    menu.setName("abc");
    menu.setId("123");
    OrderMenu orderMenu = new OrderMenu();
    orderMenu.setId(12);
    menu.setIOrderMenus(List.of(orderMenu));
    eventsResponse.setMenus(List.of(menu));


    return eventsResponse;

  }


  private EventsResponseDto mockEventResponseDto() {
    EventsResponseDto eventsResponseDto = new EventsResponseDto();
    eventsResponseDto.setAdults(2);
    eventsResponseDto.setAreaId("area123");
    eventsResponseDto.setAreaName("AreaName");
    eventsResponseDto.setBookingReference("ABCD1234");
    eventsResponseDto.setCancelLink("http://example.com/cancel");
    eventsResponseDto.setChildren(1);
    eventsResponseDto.setConsent(new ConsentDto());
    eventsResponseDto.setDate("2023-09-08");
    eventsResponseDto.setEditLink("http://example.com/edit");
    eventsResponseDto.setEmailAddress("test@example.com");
    eventsResponseDto.setFirstname("John");
    eventsResponseDto.setId("123");
    eventsResponseDto.setLastname("Doe");
    eventsResponseDto.setName("EventName");
    eventsResponseDto.setOccasionId("456");
    eventsResponseDto.setOccasionName("Birthday");
    eventsResponseDto.setSiteId("789");
    eventsResponseDto.setSiteName("SiteName");
    eventsResponseDto.setSpecialRequest("SpecialRequest");
    eventsResponseDto.setTelephoneNumber("123-456-7890");
    eventsResponseDto.setSiteTimezone("GMT");
    eventsResponseDto.setTime("14:30");
    eventsResponseDto.setTurnTimeMinutes(60);
    return eventsResponseDto;

  }
  private Consent mockConsent(){
    Consent consent = new Consent();
    consent.setEmail(true);
    consent.setPhone(false);
    consent.setSms(true);
    consent.setPostal(false);
    consent.setPushNotification(true);
    consent.setProfiling(false);
    consent.setPrivacyStatement(true);
    consent.setConsentStatement(false);
    consent.setTermsAndConditions(true);
    return consent;
  }
}
