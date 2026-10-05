package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import mocks.HotelMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Room;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.model.los.rules.LosRestriction;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;

@ExtendWith(MockitoExtension.class)

class LosRestrictionProcessServiceTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";

  private List<Hotel> hotelList;
  @Mock
  private LosRestrictionRulesEvaluator losRestrictionRulesEvaluator;

  private LosRestrictionPort losRestrictionPort;

  private SearchCriteria searchCriteria;

  @BeforeEach
  void setup() {
    losRestrictionPort = new LosRestrictionProcessService(losRestrictionRulesEvaluator);
    hotelList = buildHotels();
    searchCriteria = buildSearchCriteria();
  }

  @Test
  void applyLosRestrictionsWithNoRestrictions() {
    final LosRestriction restriction = buildLosRestriction(LosRestrictionName.NO_RESTRICTIONS);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(0),
        searchCriteria)).thenReturn(restriction);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(1),
        searchCriteria)).thenReturn(restriction);
    final List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotelList);
    Assertions.assertThat(hotelList.get(0).getRates()).hasSameSizeAs(hotelsAfterLosRestrictions.get(0).getRates());
    assertFalse(hotelList.get(0).getHasMlosRestriction());
  }

  @Test
  void applyLosRestrictionsWithRestrictions() {
    final LosRestriction restriction = buildLosRestriction(LosRestrictionName.MIN_NIGHT_RESTRICTIONS);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(0),
        searchCriteria)).thenReturn(restriction);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(1),
        searchCriteria)).thenReturn(restriction);
    final List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotelList);
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getRates()).isEmpty();
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getAvailable()).isFalse();
    assertNotNull(hotelList.get(0).getHasMlosRestriction());
    assertTrue(hotelList.get(0).getHasMlosRestriction());
  }

  @Test
  void applyLosRestrictionsWithRestrictionsWithFlagMlosFalse() {
    final LosRestriction restriction = buildLosRestriction(LosRestrictionName.MIN_NIGHT_RESTRICTIONS);
    searchCriteria.setFlagMlos(false);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(0),
        searchCriteria)).thenReturn(restriction);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(1),
        searchCriteria)).thenReturn(restriction);
    final List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotelList);
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getRates()).isEmpty();
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getAvailable()).isFalse();
    assertNull(hotelList.get(0).getHasMlosRestriction());
  }

  @Test
  void applyLosRestrictionsWithMaxNightRestrictions() {
    final LosRestriction restriction = buildLosRestriction(LosRestrictionName.MAX_NIGHT_RESTRICTIONS);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(0),
        searchCriteria)).thenReturn(restriction);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(1),
        searchCriteria)).thenReturn(restriction);
    final List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotelList);
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getRates()).isEmpty();
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getAvailable()).isFalse();
    assertNotNull(hotelList.get(0).getHasMlosRestriction());
    assertFalse(hotelList.get(0).getHasMlosRestriction());
  }

  @Test
  void applyLosRestrictionsWithOneRestriction() {
    final LosRestriction restriction = buildLosRestriction(LosRestrictionName.MIN_NIGHT_RESTRICTIONS);
    final LosRestriction restriction1 = buildLosRestriction(LosRestrictionName.NO_RESTRICTIONS);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(0),
        searchCriteria)).thenReturn(restriction);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(hotelList.get(0).getRates().get(1),
        searchCriteria)).thenReturn(restriction1);
    List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotelList);
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getRates()).hasSize(1);
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getRates().get(0).getClassification()).isEqualTo("S");
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getAvailable()).isTrue();
    assertTrue(hotelList.get(0).getHasMlosRestriction());
  }

  @Test
  void applyLosRestrictionsForEmptyHotelsList() {
    List<Hotel> hotels = Collections.emptyList();
    final List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotels);
    Assertions.assertThat(hotelsAfterLosRestrictions).isEqualTo(Collections.emptyList());
  }

  @Test
  void testHotelWithEmptyRatesForLOS() {
    List<Hotel> hotels = new ArrayList<>();
    Hotel hotel = Hotel.builder()
        .hotelCode("PLYPTI")
        .available(false)
        .limitedAvailability(true)
        .rates(Collections.emptyList())
        .build();
    hotels.add(hotel);
    final List<Hotel> hotelsAfterLosRestrictions = losRestrictionPort.applyLosRestrictions(searchCriteria, hotels);
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getLimitedAvailability()).isTrue();
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getAvailable()).isFalse();
    Assertions.assertThat(hotelsAfterLosRestrictions.get(0).getRates()).isEmpty();
    assertNull(hotelList.get(0).getHasMlosRestriction());
  }

  private LosRestriction buildLosRestriction(LosRestrictionName losRule) {
    return LosRestriction.builder()
        .losRule(losRule)
        .maxNights(1)
        .minNights(1)
        .noNightsAllowed(new int[]{1})
        .build();
  }

  private SearchCriteria buildSearchCriteria() {
    //PLYPTI, PLYLOC, PLYMAR, LISBAR, PAIWHI
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .flagMlos(true)
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(false)
        .rooms(1)
        .type(new String[]{RoomType.SB.name()})
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }

  private List<Hotel> buildHotels() {
    Room room = HotelMock.mockRoom("SB", 1, 0, false, new Price(new BigDecimal(100.00), "GBP"), false);
    List<Room> roomList = new ArrayList<>();
    roomList.add(room);
    RatePlan ratePlan = HotelMock.mockRatePlan("A", "RATE1", roomList, new Price(new BigDecimal(100.00), "GBP"));
    RatePlan ratePlan1 = HotelMock.mockRatePlan("S", "RATE2", roomList, new Price(new BigDecimal(100.00), "GBP"));
    List<RatePlan> ratePlanList = new ArrayList<>();
    ratePlanList.add(ratePlan);
    ratePlanList.add(ratePlan1);
    Hotel hotel = HotelMock.mockHotel("PLYPTI", ratePlanList);
    hotel.setAvailable(true);
    List<Hotel> hotelList = new ArrayList<>();
    hotelList.add(hotel);
    return hotelList;
  }

  @Test
  void isLosApplicable_returnsFalse() {
    final LosRestriction restriction1 = buildLosRestriction(LosRestrictionName.NO_RESTRICTIONS);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(any(),
        any())).thenReturn(restriction1);

    boolean hasLosRestrictions = losRestrictionPort.isLosApplicable(
        RatePlan.builder().code("FLEXRATE").build(),
        SearchCriteria.builder().hotelCodes(List.of("FRAMTI")).build());

    assertFalse(hasLosRestrictions);
  }

  @Test
  void isLosApplicable_returnsTrue() {
    final LosRestriction restriction1 = buildLosRestriction(LosRestrictionName.MIN_NIGHT_RESTRICTIONS);
    when(losRestrictionRulesEvaluator.evaluateRestrictionRules(any(),
        any())).thenReturn(restriction1);

    boolean hasLosRestrictions = losRestrictionPort.isLosApplicable(
        RatePlan.builder().code("FLEXRATE").build(),
        SearchCriteria.builder().hotelCodes(List.of("FRAMTI")).build());

    assertTrue(hasLosRestrictions);
  }
}
