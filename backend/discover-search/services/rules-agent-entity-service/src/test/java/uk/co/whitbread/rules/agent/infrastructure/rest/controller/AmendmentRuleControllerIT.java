package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.AmendmentRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.AmendmentRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.AmendmentRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.AmendmentRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class AmendmentRuleControllerIT {

  @InjectMocks
  private AmendmentRuleController amendmentRuleController;
  @Mock
  private AmendmentRuleInPort amendmentRuleInPort;
  @Mock
  private AmendmentRuleDtoMapper amendmentRuleDtoMapper;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyyMMdd'T'HHmmss");

  @Test
  void shouldRetrieveAmendmentRule() {
    //Arrange
    var requestDto = mockAmendmentRuleRequestDto();
    when(amendmentRuleDtoMapper.toModel(any())).thenReturn(mockAmendmentRuleRequest());
    when(amendmentRuleInPort.getAmendmentRule(any())).thenReturn(mockAmendmentRuleResponse());
    when(amendmentRuleDtoMapper.toDto(any())).thenReturn(mockAmendmentRuleResponseDto());

    //Act
    var amendmentRuleDto = amendmentRuleDtoMapper.toDto(mockAmendmentRuleResponse());
    var response = amendmentRuleController.getAmendmentRule(requestDto);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals("Flex", response.getRequestDetails().getRateType());
    Assertions.assertEquals(response.getRequestDetails().getRateType(), amendmentRuleDto.getRequestDetails().getRateType());
    Assertions.assertEquals(false, response.getIsAmendable());
    Assertions.assertEquals(response.getIsAmendable(), amendmentRuleDto.getIsAmendable());
    Assertions.assertEquals("GB", response.getRequestDetails().getHotelCountryCode());
    Assertions.assertEquals(response.getRequestDetails().getHotelCountryCode(), amendmentRuleDto.getRequestDetails().getHotelCountryCode());
  }

  @Test
  void shouldHandleAmendmentRuleNotFound() {
    //Arrange
    var requestDto = mockAmendmentRuleRequestDto();
    when(amendmentRuleDtoMapper.toModel(any())).thenReturn(null);
    when(amendmentRuleInPort.getAmendmentRule(any())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_NOT_AMENDABLE_EXCEPTION,
              "Amendment rule not found."));

    //Assert
    assertThrows(RuleEngineException.class, () -> amendmentRuleController.getAmendmentRule(requestDto));
  }

  private AmendmentRuleRequest mockAmendmentRuleRequest(){
    return AmendmentRuleRequest.builder()
        .arrivalDate(LocalDate.parse("20220701", DATE_FORMATTER))
        .hotelLocalDateTime(LocalDateTime.parse("20220701T090000", DATE_TIME_FORMATTER))
        .hotelCountryCode("GB")
        .rateType("Flex")
        .build();
  }

  private AmendmentRuleRequestDto mockAmendmentRuleRequestDto(){
    return AmendmentRuleRequestDto.builder()
        .arrivalDate("20220701")
        .hotelLocalDateTime("20220701")
        .hotelCountryCode("GB")
        .rateType("Flex")
        .build();
  }

  private AmendmentRuleResponse mockAmendmentRuleResponse(){
    return AmendmentRuleResponse.builder().isAmendable(false)
        .requestDetails(AmendmentRequestDetails.builder()
            .arrivalDate(LocalDate.parse("20220701", DATE_FORMATTER))
            .hotelLocalDateTime(LocalDateTime.parse("20220701T090000", DATE_TIME_FORMATTER))
            .hotelCountryCode("GB")
            .rateType("Flex")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private AmendmentRuleResponseDto mockAmendmentRuleResponseDto(){
    return AmendmentRuleResponseDto.builder()
        .isAmendable(false)
        .requestDetails(AmendmentRequestDetailsDto.builder()
            .arrivalDate("20220701")
            .hotelLocalDateTime("20220701T090000")
            .hotelCountryCode("GB")
            .rateType("Flex")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }
}
