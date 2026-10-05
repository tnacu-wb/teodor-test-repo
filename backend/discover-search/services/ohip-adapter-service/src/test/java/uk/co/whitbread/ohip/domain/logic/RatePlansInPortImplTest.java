package uk.co.whitbread.ohip.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_NO_PROMO_CODE_EXCEPTION;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.exceptions.UnavailableRatesException;
import uk.co.whitbread.ohip.domain.model.rates.out.Classifications;
import uk.co.whitbread.ohip.domain.model.rates.out.Description;
import uk.co.whitbread.ohip.domain.model.rates.out.PrimaryDetails;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlan;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlans;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.RatePlansOutPort;

@ExtendWith(MockitoExtension.class)
class RatePlansInPortImplTest {

  @Mock
  private RatePlansOutPort ratePlansOutPort;

  @InjectMocks
  private RatePlansInPortImpl ratePlansInPort;


  @Test
  void getRatePlans__ShouldReturnOK() {
    //Arrange
    when(ratePlansOutPort.getRatePlans(anyList(), any())).thenReturn(mockRatePlanResponse());

    //Act
    var response = ratePlansInPort.getRatePlans(List.of("FLEXRATE"), "HOTEL_ID");

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


    verifyNoMoreInteractions(ratePlansOutPort);

  }

  @Test
  void getNegotiatedRatesForProfileId__ShouldReturnOK() {
    //Arrange
    var profileId = "123456";
    when(ratePlansOutPort.getNegotiatedRatesForProfileId(profileId)).thenReturn(
        NegotiatedRatesResponse.builder().build());

    //Act
    var response = ratePlansInPort.getNegotiatedRatesForProfileId(profileId);

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(ratePlansOutPort);

  }

  @Test
  void getRatePlanInfo__ShouldReturnOK() {
    // Arrange
    String ratePlanCode = "FLEXRATE";
    String hotelId = "HOTEL_ID";
    when(ratePlansOutPort.getRatePlanInfo(ratePlanCode, hotelId)).thenReturn(
        RatePlanInfoResponse.builder()
            .ratePlanInfo(List.of(RatePlans.builder().ratePlanCode(ratePlanCode).hotelId(hotelId).build()))
            .build());

    // Act
    var response = ratePlansInPort.getRatePlanInfo(ratePlanCode, hotelId);

    // Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getRatePlanInfo());
    assertEquals(1, response.getRatePlanInfo().size());
    assertEquals(ratePlanCode, response.getRatePlanInfo().get(0).getRatePlanCode());
    assertEquals(hotelId, response.getRatePlanInfo().get(0).getHotelId());
    verifyNoMoreInteractions(ratePlansOutPort);
  }

  @Test
  void getPromotionCode__ShouldReturnOK() {
    // Arrange
    List<String> promotionCodes = List.of("FX10R");
    String hotelId = "HEAPTI";
    when(ratePlansOutPort.getPromotionCode(promotionCodes, hotelId))
        .thenReturn(Collections.singletonList(PromotionCodeResponse.builder().build()));

    // Act
    var response = ratePlansInPort.getPromotionCode(promotionCodes, hotelId);

    // Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(ratePlansOutPort);
  }

  @Test
  void getPromotionCode__ShouldThrowException() {
    String hotelId = "HEAPTI";
    List<String> promotionCodes = new ArrayList<>();
    // Act
    Assertions.assertEquals(
        DIGITAL_NO_PROMO_CODE_EXCEPTION.getCode(),
        assertThrows(
            UnavailableRatesException.class,
            () -> ratePlansInPort.getPromotionCode(promotionCodes, hotelId)
        ).getErrorCode()
    );
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