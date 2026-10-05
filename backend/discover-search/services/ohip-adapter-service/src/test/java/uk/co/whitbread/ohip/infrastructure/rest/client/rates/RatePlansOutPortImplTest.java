package uk.co.whitbread.ohip.infrastructure.rest.client.rates;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.NegotiatedRates;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.RatePlanInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PromotionCodeDetailsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PropertyPromotionCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PropertyPromotionCodes;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PropertyPromotionCodesPropertyPromotionCodes;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlanClassificationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlanShortInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlansSummary;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.RatePlansSummaryRatePlanShortInfoList;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.TimeSpanType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.TranslationTextType2000;
import uk.co.whitbread.ohip.domain.model.rates.out.Classifications;
import uk.co.whitbread.ohip.domain.model.rates.out.Description;
import uk.co.whitbread.ohip.domain.model.rates.out.DynamicBaseRate;
import uk.co.whitbread.ohip.domain.model.rates.out.PrimaryDetails;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlan;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanBasedOnRate;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlans;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.NegotiatedRatesMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.PromotionCodeMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.RatePlanInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.mapper.RatePlansMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rates.ohip.OhipRatePlansClient;

@ExtendWith(MockitoExtension.class)
class RatePlansOutPortImplTest {

  @InjectMocks
  private RatePlansOutPortImpl ratePlansOutPort;

  @Mock
  private OhipRatePlansClient ohipRatePlansClient;

  @Mock
  private RatePlansMapper ratePlansMapper;

  @Mock
  private NegotiatedRatesMapper negotiatedRatesMapper;

  @Mock
  private RatePlanInfoMapper ratePlanInfoMapper;

  @Mock
  private PromotionCodeMapper promotionCodeMapper;

  @Test
  void getNegotiatedRatesForProfileId__ShouldReturnOK() {
    //Arrange
    var profileId = "123456";
    when(ohipRatePlansClient.getNegotiatedRatesForProfileId(profileId)).
        thenReturn(Mono.just(new NegotiatedRates()));
    when(negotiatedRatesMapper.toDomainModel(any())).thenReturn((NegotiatedRatesResponse.builder()
        .build()));

    //Act
    var response = ratePlansOutPort.getNegotiatedRatesForProfileId(profileId);

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(ohipRatePlansClient);
  }

  @Test
  void getRatePlans__ShouldReturnOK() {
    //Arrange
    when(ohipRatePlansClient.getRatePlans(anyList(), any())).thenReturn(mockRatePlanSummary());
    when(ratePlansMapper.toDomainModel(any())).thenReturn(mockRatePlanResponse());

    //Act
    var response = ratePlansOutPort.getRatePlans(List.of("FLEXRATE"), "HOTEL_ID");

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getRatePlans());
    assertEquals(1, response.getRatePlans().size());
    assertNotNull(response.getRatePlans().get(0));
    assertNotNull(response.getRatePlans().get(0).getPrimaryDetails());
    assertNotNull(response.getRatePlans().get(0).getPrimaryDetails().getDescription());
    assertNotNull(response.getRatePlans().get(0).getClassifications());


    assertEquals("Some description", response.getRatePlans().get(0).getPrimaryDetails().getDescription().getDefaultText());
    assertEquals("PBN", response.getRatePlans().get(0).getClassifications().getDisplaySet());
    assertEquals("U", response.getRatePlans().get(0).getClassifications().getRateCategory());
    assertEquals("OTH", response.getRatePlans().get(0).getClassifications().getMarketCode());


    verifyNoMoreInteractions(ohipRatePlansClient);
  }

  @Test
  void getRatePlanInfo__ShouldReturnOK() {
    //Arrange
    when(ohipRatePlansClient.getRatePlanInfo(any(), any())).
        thenReturn(mockRatePlanInfo());
    when(ratePlanInfoMapper.toDomainModel(any())).thenReturn(mockRatePlanInfoResponse());

    //Act
    var response = ratePlansOutPort.getRatePlanInfo("STDDIS10", "HEAPTI");

    //Assert
    assertThat(response, notNullValue());
    assertEquals("STANDARD",
        response.getRatePlanInfo().get(0).getRatePlanBasedOnRates().get(0).getDynamicBaseRate()
            .getDynamicBasedOnRatePlan());
    verifyNoMoreInteractions(ohipRatePlansClient);
  }

  @Test
  void getPromotionCode__ShouldReturnOK() {
    //Arrange
    List<String> promotionCodes = List.of("FX10R");
    String hotelId = "HEAPTI";
    when(ohipRatePlansClient.getPromotionCode(anyList(), anyString()))
        .thenReturn(mockPropertyPromotionCodes());
    when(promotionCodeMapper.toDomainModel(any()))
        .thenReturn(mockPromotionCodeResponse());

    //Act
    var response = ratePlansOutPort.getPromotionCode(promotionCodes, hotelId);

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(ohipRatePlansClient);
  }


  private PromotionCodeResponse mockPromotionCodeResponse() {
    return PromotionCodeResponse
        .builder()
        .promotionCode("FX10R")
        .promotionName("Flex Rate 10% Discount Room Only")
        .bookingEndDate(LocalDate.parse("2025-09-30"))
        .bookingStartDate(LocalDate.parse("2025-08-19"))
        .stayStartDate(LocalDate.parse("2025-10-01"))
        .stayEndDate(LocalDate.parse("2025-11-30"))
        .build();

  }

  private PropertyPromotionCodes mockPropertyPromotionCodes() {

    PropertyPromotionCodeType property =
        mockPropertyPromotionCodeType();

    PropertyPromotionCodesPropertyPromotionCodes wrapper =
        new PropertyPromotionCodesPropertyPromotionCodes();

    wrapper.setPropertyPromotionCodes(List.of(property));

    PropertyPromotionCodes root =
        new PropertyPromotionCodes();

    root.setPropertyPromotionCodes(wrapper);

    return root;
  }

  private PropertyPromotionCodeType mockPropertyPromotionCodeType() {

    PropertyPromotionCodeType property = new PropertyPromotionCodeType();

    property.setPromotionCode("FX10R");
    property.setHotelId("TKINPT");   // Important for property promo

    PromotionCodeDetailsType details = new PromotionCodeDetailsType();

    TranslationTextType2000 promotionName = new TranslationTextType2000();
    promotionName.setDefaultText("Flex Rate 10% Discount Room Only");
    details.setPromotionName(promotionName);

    TimeSpanType bookingDate = new TimeSpanType();
    bookingDate.setStartDate(LocalDate.parse("2025-08-19"));
    bookingDate.setEndDate(LocalDate.parse("2025-09-30"));
    details.setBookingDate(bookingDate);

    TimeSpanType stayDate = new TimeSpanType();
    stayDate.setStartDate(LocalDate.parse("2025-10-01"));
    stayDate.setEndDate(LocalDate.parse("2025-11-30"));
    details.setStayDate(stayDate);

    property.setPromotionCodeDetails(details);

    return property;
  }

  private RatePlanInfoResponse mockRatePlanInfoResponse() {
    return RatePlanInfoResponse.builder()
        .ratePlanInfo(List.of(mockRatePlans()))
        .build();
  }

  private RatePlans mockRatePlans() {
    return RatePlans.builder()
        .ratePlanBasedOnRates(List.of(mockRatePlanBasedOnRate()))
        .build();
  }

  private RatePlanBasedOnRate mockRatePlanBasedOnRate() {
    return RatePlanBasedOnRate.builder()
        .dynamicBaseRate(mockDynamicBaseRate())
        .build();
  }

  private DynamicBaseRate mockDynamicBaseRate() {
    return DynamicBaseRate.builder()
        .dynamicBasedOnRatePlan("STANDARD")
        .build();
  }


  private RatePlanInfo mockRatePlanInfo() {
    RatePlanInfo ratePlanInfo = new RatePlanInfo();

    RatePlanBasedOnRate basedOnRate = new RatePlanBasedOnRate();
    DynamicBaseRate dynamicBaseRate = new DynamicBaseRate();
    dynamicBaseRate.setDynamicBasedOnRatePlan("STANDARD");
    basedOnRate.setDynamicBaseRate(dynamicBaseRate);

    RatePlans ratePlans = new RatePlans();
    ratePlans.setRatePlanBasedOnRates(List.of(basedOnRate));

    return ratePlanInfo;
  }


  private Mono<RatePlansSummary> mockRatePlanSummary() {
    var ratePlansSummary = new RatePlansSummary();
    var ratePlanShortInfoList = new RatePlansSummaryRatePlanShortInfoList();
    var ratePlanShortInfo = new RatePlanShortInfoType();
    var ratePlanClassification = new RatePlanClassificationsType();
    ratePlanClassification.setDisplaySet("BMD");
    ratePlanShortInfo.setHotelId("LONEUS");
    ratePlanShortInfo.setRatePlanCode("FLEXRATE");
    ratePlanShortInfo.setClassifications(ratePlanClassification);
    ratePlanShortInfoList.setRatePlanShortInfo(List.of(ratePlanShortInfo));

    ratePlansSummary.setRatePlanShortInfoList(ratePlanShortInfoList);

    return Mono.just(ratePlansSummary);
  }

  private RatePlansResponse mockRatePlanResponse() {

    return RatePlansResponse
        .builder()
        .ratePlans(Collections.singletonList(mockRatePlan()))
        .build();

  }

  private RatePlan mockRatePlan() {
    return RatePlan.builder()
        .primaryDetails(mockPrimaryDetails())
        .classifications(
            Classifications.builder().displaySet("PBN").rateCategory("U").marketCode("OTH").build())
        .build();
  }

  private PrimaryDetails mockPrimaryDetails() {
    return PrimaryDetails
        .builder()
        .description(Description.builder().defaultText("Some description").build())
        .build();

  }
}
