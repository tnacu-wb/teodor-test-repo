package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;


public class SearchCriteriaTest {

  private static DateTimeFormatter iso_8601_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static String ARRIVAL = LocalDate.now().format(iso_8601_formatter);
  private static String DEPARTURE = LocalDate.now().plusDays(1).format(iso_8601_formatter);
  private static String LANGUAGE = "EN";
  private static String COUNTRY = "GB";
  private static String[] TYPE = new String[]{RoomType.DB.name()};
  private Validator validator;
  private ValidatorFactory validatorFactory;
  private SearchCriteria hotelSearchRequest;
  private int rooms;
  private int[] adults;
  private int[] children;

  @BeforeEach
  public void setUp() throws Exception {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterEach
  public void tearDown() throws Exception {
    validatorFactory.close();
  }

  @Test
  public void shouldHaveNoViolations() {
    //given:
    rooms = 1;
    adults = new int[]{1};
    children = new int[]{0};
    hotelSearchRequest = new SearchCriteria();
    hotelSearchRequest.setArrival(ARRIVAL);
    hotelSearchRequest.setDeparture(DEPARTURE);
    hotelSearchRequest.setRooms(rooms);
    hotelSearchRequest.setAdults(adults);
    hotelSearchRequest.setChildren(children);
    hotelSearchRequest.setType(TYPE);
    hotelSearchRequest.setSort(SortType.DISTANCE);
    hotelSearchRequest.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    hotelSearchRequest.setPage(1);
    hotelSearchRequest.setSize(40);
    //when:
    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(hotelSearchRequest);
    //then:
    assertTrue(violations.isEmpty());
  }

  @Test
  public void shouldDetectInvalidMinRooms() {
    //given:
    adults = new int[]{1};
    children = new int[]{0};
    SearchCriteria hotelSearchRequest = new SearchCriteria();
    //when:
    hotelSearchRequest.setArrival(ARRIVAL);
    hotelSearchRequest.setDeparture(DEPARTURE);
    hotelSearchRequest.setRooms(rooms);
    hotelSearchRequest.setAdults(adults);
    hotelSearchRequest.setChildren(children);
    hotelSearchRequest.setType(TYPE);
    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(hotelSearchRequest);
    //then:
    assertTrue(violations.size() >= 1);
  }

  @Test
  public void shouldDetectInvalidMaxRooms() {
    //given:
    rooms = 10;
    adults = new int[]{1};
    children = new int[]{0};
    SearchCriteria hotelSearchRequest = new SearchCriteria();
    //when:
    hotelSearchRequest.setArrival(ARRIVAL);
    hotelSearchRequest.setDeparture(DEPARTURE);
    hotelSearchRequest.setRooms(rooms);
    hotelSearchRequest.setAdults(adults);
    hotelSearchRequest.setChildren(children);
    hotelSearchRequest.setType(TYPE);
    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(hotelSearchRequest);
    //then:
    assertTrue(violations.size() >= 1);
    Iterator<ConstraintViolation<SearchCriteria>> it = violations.iterator();
    boolean isViolationFound = false;
    while (it.hasNext()) {
      ConstraintViolation<SearchCriteria> violation = it.next();
      if (violation.getPropertyPath().toString().equalsIgnoreCase("rooms")) {
        assertEquals("Unable to add more rooms, If you’d like to book ten rooms or more, please call us and "
            + "we’ll be happy to help", violation.getMessage());
        assertEquals("rooms", violation.getPropertyPath().toString());
        assertEquals(10, violation.getInvalidValue());
        isViolationFound = true;
      }
    }
    if (!isViolationFound) {
      fail();
    }
  }

  @Test
  public void shouldDetectInvalidMinAdults() {
    //given:
    rooms = 4;
    adults = new int[0];
    children = new int[]{0};
    SearchCriteria hotelSearchRequest = new SearchCriteria();
    //when:
    hotelSearchRequest.setArrival(ARRIVAL);
    hotelSearchRequest.setDeparture(DEPARTURE);
    hotelSearchRequest.setRooms(rooms);
    hotelSearchRequest.setAdults(adults);
    hotelSearchRequest.setChildren(children);
    hotelSearchRequest.setType(TYPE);
    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(hotelSearchRequest);
    System.out.println("****Violation Size****" + violations.size());
    //then:
    assertTrue(violations.size() >= 1);
    Iterator<ConstraintViolation<SearchCriteria>> it = violations.iterator();
    boolean isViolationFound = false;
    while (it.hasNext()) {
      ConstraintViolation<SearchCriteria> violation = it.next();
      if (violation.getPropertyPath().toString().equalsIgnoreCase("adults")) {
        assertEquals("Adults array size must be equal to the number of rooms", violation.getMessage());
        assertEquals("adults", violation.getPropertyPath().toString());
        assertEquals(adults, violation.getInvalidValue());
        isViolationFound = true;
      }
    }
    if (!isViolationFound) {
      fail();
    }
  }

  @Test
  public void shouldDetectInvalidMaxAdults() {
    //given:
    rooms = 1;
    adults = new int[]{1, 2};
    children = new int[]{0};
    SearchCriteria hotelSearchRequest = new SearchCriteria();
    //when:
    hotelSearchRequest.setArrival(ARRIVAL);
    hotelSearchRequest.setDeparture(DEPARTURE);
    hotelSearchRequest.setRooms(rooms);
    hotelSearchRequest.setAdults(adults);
    hotelSearchRequest.setChildren(children);
    hotelSearchRequest.setType(TYPE);
    //hotelSearchRequest.setSort(SortType.DISTANCE);
    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(hotelSearchRequest);
    //then:
    assertTrue(violations.size() >= 1);
    Iterator<ConstraintViolation<SearchCriteria>> it = violations.iterator();
    boolean isViolationFound = false;
    while (it.hasNext()) {
      ConstraintViolation<SearchCriteria> violation = it.next();
      final String expectedMsg = "adults, children and type must have an array of length defined by the value in rooms";
      final String actualMsg = violation.getMessage();
      if (StringUtils.isNotEmpty(actualMsg) && actualMsg.contains(expectedMsg)) {
        assertEquals(expectedMsg, violation.getMessage());
        isViolationFound = true;
      }
    }
    if (!isViolationFound) {
      fail();
    }
  }

  @Test
  public void shouldDetectInvalidMaxAdultsInMultipleRooms() {
    //given:
    rooms = 2;
    adults = new int[]{2, 3};
    children = new int[]{0, 0};
    String type[] = new String[]{RoomType.SB.name(), RoomType.DB.name()};
    SearchCriteria hotelSearchRequest = new SearchCriteria();
    //when:
    hotelSearchRequest.setArrival(ARRIVAL);
    hotelSearchRequest.setDeparture(DEPARTURE);
    hotelSearchRequest.setRooms(rooms);
    hotelSearchRequest.setAdults(adults);
    hotelSearchRequest.setChildren(children);
    hotelSearchRequest.setType(type);
    hotelSearchRequest.setHotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"));
    hotelSearchRequest.setPage(1);
    hotelSearchRequest.setSize(40);
    Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(hotelSearchRequest);
    //then:
    assertTrue(violations.size() >= 1);
    Iterator<ConstraintViolation<SearchCriteria>> it = violations.iterator();
    boolean isViolationFound = false;
    while (it.hasNext()) {
      ConstraintViolation<SearchCriteria> violation = it.next();
      final String actualMsg = violation.getMessage();
      if (StringUtils.isNotEmpty(actualMsg)) {
        assertEquals("Invalid number of adults/children in one of the room/rooms chosen", actualMsg);
        isViolationFound = true;
      }
    }
    if (!isViolationFound) {
      fail();
    }
  }
}