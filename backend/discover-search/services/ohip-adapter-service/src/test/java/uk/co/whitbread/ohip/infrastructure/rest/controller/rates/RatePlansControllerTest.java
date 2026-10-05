package uk.co.whitbread.ohip.infrastructure.rest.controller.rates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.rates.out.PromotionCodeResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlanInfoResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.RatePlansResponse;
import uk.co.whitbread.ohip.domain.model.rates.out.NegotiatedRatesResponse;
import uk.co.whitbread.ohip.domain.ports.primary.RatePlansInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.NegotiatedRatesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.PromotionCodeResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.RatePlanInfoResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.mapper.RatePlansResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.ClassificationsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.DescriptionDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.NegotiatedRatesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.PrimaryDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.PromotionResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlanDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlanInfoResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.rates.model.out.RatePlansResponseDto;

@ExtendWith(MockitoExtension.class)
class RatePlansControllerTest {

  @Mock
  private RatePlansInPort ratePlansInPort;
  @Mock
  private RatePlansResponseMapper ratePlansResponseMapper;
  @Mock
  private NegotiatedRatesResponseMapper negotiatedRatesResponseMapper;
  @Mock
  private PromotionCodeResponseMapper promotionCodeResponseMapper;
  @Mock
  private RatePlanInfoResponseMapper ratePlanInfoResponseMapper;

  @InjectMocks
  private RatePlansController ratePlansController;

  @Test
  void getRatePlans__ShouldReturnOk() {

    when(ratePlansResponseMapper.toDto(any())).thenReturn(mockRatePlansResponseDto());
    when(ratePlansInPort.getRatePlans(anyList(), any())).thenReturn(
        RatePlansResponse.builder()
            .build());

    var response = ratePlansController.getRatePlans(List.of("FLEXRATE"), "HOTEL_ID");

    //Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getRatePlans());
    assertEquals(1, response.getRatePlans().size());
    assertNotNull(response.getRatePlans().get(0));
    assertNotNull(response.getRatePlans().get(0).getPrimaryDetails());
    assertNotNull(response.getRatePlans().get(0).getPrimaryDetails().getDescription());
    assertNotNull(response.getRatePlans().get(0).getClassifications());

    assertEquals("Some description",
        response.getRatePlans().get(0).getPrimaryDetails().getDescription().getDefaultText());
    assertEquals("PBN", response.getRatePlans().get(0).getClassifications().getDisplaySet());
    assertEquals("U", response.getRatePlans().get(0).getClassifications().getRateCategory());
    assertEquals("OTH", response.getRatePlans().get(0).getClassifications().getMarketCode());

    verifyNoMoreInteractions(ratePlansResponseMapper);
    verifyNoMoreInteractions(ratePlansInPort);

  }

  @Test
  void getNegotiatedRatesForProfileId__ShouldReturnOk() {

    when(negotiatedRatesResponseMapper.toDto(any())).thenReturn(
        NegotiatedRatesResponseDto.builder().build());
    when(ratePlansInPort.getNegotiatedRatesForProfileId(anyString())).thenReturn(
        NegotiatedRatesResponse.builder()
            .build());

    var response = ratePlansController.getNegotiatedRatesForProfileId(anyString());

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(ratePlansResponseMapper);
    verifyNoMoreInteractions(ratePlansInPort);

  }

  @Test
  void getRatePlanInfo__ShouldReturnOk() {
    // Arrange
    String ratePlanCode = "FLEXRATE";
    String hotelId = "HOTEL_ID";

    when(ratePlansInPort.getRatePlanInfo(ratePlanCode, hotelId))
        .thenReturn(RatePlanInfoResponse.builder().build());
    when(ratePlanInfoResponseMapper.toDto(any()))
        .thenReturn(RatePlanInfoResponseDto.builder().ratePlanInfo(Collections.emptyList()).build());

    // Act
    var response = ratePlansController.getRatePlanInfo(ratePlanCode, hotelId);

    // Assert
    assertThat(response, notNullValue());
    assertNotNull(response.getRatePlanInfo());
    verifyNoMoreInteractions(ratePlanInfoResponseMapper);
    verifyNoMoreInteractions(ratePlansInPort);
  }

  @Test
  void getPromotionCode__ShouldReturnOk() {
    // Arrange
    List<String> promoCodes = List.of("FX20R");
    String hotelId = "HEAPTI";

    when(ratePlansInPort.getPromotionCode(anyList(), anyString()))
        .thenReturn(Collections.singletonList(PromotionCodeResponse.builder().build()));

    when(promotionCodeResponseMapper.toDto(anyList()))
        .thenReturn(mockPromotionResponseDto());

    // Act
    var response = ratePlansController.getPromotionCode(promoCodes, hotelId);

    // Assert
    assertThat(response, notNullValue());
    assertEquals("FX10R", response.get(0).getPromotionCode());
    assertEquals("Flex Rate 10% Discount Room Only", response.get(0).getPromotionName());

    verifyNoMoreInteractions(promotionCodeResponseMapper);
    verifyNoMoreInteractions(ratePlansInPort);
  }

  private List<PromotionResponseDto> mockPromotionResponseDto() {
    return Collections.singletonList(PromotionResponseDto
        .builder()
        .promotionCode("FX10R")
        .promotionName("Flex Rate 10% Discount Room Only")
        .bookingEndDate(LocalDate.parse("2025-09-30"))
        .bookingStartDate(LocalDate.parse("2025-08-19"))
        .stayEndDate(LocalDate.parse("2025-11-30"))
        .stayStartDate(LocalDate.parse("2025-10-01"))
        .build());
  }

  private RatePlansResponseDto mockRatePlansResponseDto() {
    return RatePlansResponseDto
        .builder()
        .ratePlans(Collections.singletonList(mockRatePlanDto()))
        .build();
  }


  private RatePlanDto mockRatePlanDto() {
    return RatePlanDto.builder()
        .primaryDetails(mockPrimaryDetailsDto())
        .classifications(
            ClassificationsDto.builder().displaySet("PBN").rateCategory("U").marketCode("OTH")
                .build())
        .build();
  }


  private PrimaryDetailsDto mockPrimaryDetailsDto() {
    return PrimaryDetailsDto
        .builder()
        .description(DescriptionDto.builder().defaultText("Some description").build())
        .build();

  }
}