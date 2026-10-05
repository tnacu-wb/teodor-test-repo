package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.ContentClientLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RateClassificationLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;


@ExtendWith(MockitoExtension.class)

class RateClassificationLookUpSvcTest {

  private static final BartBrandHotelCode hotelBrand = BartBrandHotelCode.PI;
  private static final String LANGUAGE_CODE = "en";
  private static final String COUNTRY_NULL = null;
  private final Map<String, RateClassification> emptyMap = Collections.emptyMap();
  @Mock
  private ContentClientLookupPort contentClientLookupSvc;
  private RateClassificationLookupPort rateClassificationLookupsvc;
  private Map<String, RateClassification> classificationMap = new HashMap<>();

  private Map<String, RateClassification> rateClassificationMap = new HashMap<>();

  private List<RatePlan> ratePlansList;
  private List<RatePlan> ratePlansListSecondary;
  private RatePlan ratePlanA;
  private RatePlan ratePlanS;
  private RatePlan ratePlanR;
  private RatePlan ratePlanQ;
  private RatePlan ratePlanF;

  private RateClassification rateClassificationA;
  private RateClassification rateClassificationS;
  private RateClassification rateClassificationR;
  private RateClassification rateClassificationF;
  private RateClassification rateClassificationQ;

  @BeforeEach
  void setup() {

    rateClassificationLookupsvc = new RateClassificationLookupService(contentClientLookupSvc);
    ratePlanA = getRatePlan("A", null,
        null, null);
    ratePlanS = getRatePlan("S", "2", null,
        null);
    ratePlanF = getRatePlan("F", null, null, null);
    ratePlanR = getRatePlan("R", "1", null, null);
    ratePlanQ = getRatePlan("Q", "4", null, null);

    ratePlansList = Arrays.asList(ratePlanA, ratePlanS, ratePlanF);

    rateClassificationA = getRateClassification("A", "5", "Flex",
        "Pay on arrival. Fully refundable up to 1pm on arrival day.");
    rateClassificationS = getRateClassification("S", "2", "Standard",
        "Pay on arrival. Change arrival date. Non-refundable");
    rateClassificationR = getRateClassification("R", "1", "non-Flex",
        "Pay on arrival. No change");

    rateClassificationF = getRateClassification("F", "3", "Advanced",
        "Pay now. Change arrival date. Fully refundable up to 28 days before arrival.");

    rateClassificationQ = getRateClassification("Q", "4", "Semi-Flex",
        "Pay now. Change arrival date. Fully refundable up to 3 days before arrival.");

    rateClassificationMap.put(rateClassificationA.getClassification(), rateClassificationA);
    rateClassificationMap.put(rateClassificationS.getClassification(), rateClassificationS);
    rateClassificationMap.put(rateClassificationR.getClassification(), rateClassificationR);
    rateClassificationMap.put(rateClassificationF.getClassification(), rateClassificationF);
    rateClassificationMap.put(rateClassificationQ.getClassification(), rateClassificationQ);

  }

  @Test
  void getRateClassificationsTestForNullRateClassifications() {

    when(contentClientLookupSvc.getRateClassifications(
        hotelBrand, LANGUAGE_CODE, null, COUNTRY_NULL))
        .thenReturn(null);

    Map<String, RateClassification> map = rateClassificationLookupsvc
        .getRateClassifications(hotelBrand, LANGUAGE_CODE, null);
    assertThat(map).isNull();
  }

  @Test
  void getRateClassificationsTestForEmptyRateClassifications() {

    when(contentClientLookupSvc.getRateClassifications(
        hotelBrand, LANGUAGE_CODE, null, COUNTRY_NULL))
        .thenReturn(classificationMap);

    Map<String, RateClassification> map = rateClassificationLookupsvc
        .getRateClassifications(hotelBrand, LANGUAGE_CODE, null);
    assertThat(map).isEmpty();
  }

  @Test
  void getRateClassificationsTestForEmptyRateOperaBartTest() {

    when(contentClientLookupSvc.getRateClassifications(
        hotelBrand, LANGUAGE_CODE, null, "gb"))
        .thenReturn(classificationMap);

    Map<String, RateClassification> map = rateClassificationLookupsvc
        .getRateClassificationsOperaBartHotels(hotelBrand, LANGUAGE_CODE, "gb");
    assertThat(map).isEmpty();
  }

  @Test
  void processRatePlansWithRateClassificationHandleNullRatePlans() {
    List<RatePlan> ratePlans = rateClassificationLookupsvc
        .processRatePlansWithRateClassification(Collections.EMPTY_LIST, null);
    assertEquals(Collections.emptyList(), ratePlans);
  }

  @Test
  void processRatePlansWithRateClassificationSuccessTest() {

    RateClassificationLookupPort rateClassificationLookupPortSpy =
        Mockito.spy(new RateClassificationLookupService(contentClientLookupSvc));

    List<RatePlan> expectedRatePlans = rateClassificationLookupPortSpy
        .processRatePlansWithRateClassification(ratePlansList, rateClassificationMap);

//        assertThat(expectedRatePlans.get(0).getOrder()).isEqualTo("5");
    assertThat(expectedRatePlans).hasSize(3);

    assertThat(expectedRatePlans).isNotEmpty()
        .extracting(RatePlan::getClassification).contains("A", "F", "S");

//        assertThat(expectedRatePlans).isNotEmpty()
//                .extracting(RatePlanDto::getOrder).contains("5", "2", "3");

    verify(rateClassificationLookupPortSpy, times(1))
        .populateRatePlanDtoWithRateClassification(ratePlanF, rateClassificationF);

    verify(rateClassificationLookupPortSpy, times(1))
        .populateRatePlanDtoWithRateClassification(ratePlanA, rateClassificationA);

    verify(rateClassificationLookupPortSpy, times(1))
        .populateRatePlanDtoWithRateClassification(ratePlanS, rateClassificationS);

    verify(rateClassificationLookupPortSpy, times(0))
        .populateRatePlanDtoWithRateClassification(ratePlanQ, rateClassificationQ);

  }

  @Test
  void populateRatePlanDtoWithRateClassificationSuccessTest() {
    rateClassificationLookupsvc.populateRatePlanDtoWithRateClassification(ratePlanR, rateClassificationR);
    assertEquals("non-Flex", ratePlanR.getName());
    assertEquals("Pay on arrival. No change", ratePlanR.getDescription());
//        assertEquals("1", ratePlanR.getOrder().toString());
  }

  private RateClassification getRateClassification(String classification, String order, String name,
      String description) {
    return RateClassification.builder()
        .classification(classification)
        .description(description)
        .name(name)
        .order(order)
        .build();
  }

  private RatePlan getRatePlan(String classification, String order, String name, String description) {
    return RatePlan.builder()
        .classification(classification)
        .description(description)
        .name(name)
//                .order(order)
        .build();
  }

}
