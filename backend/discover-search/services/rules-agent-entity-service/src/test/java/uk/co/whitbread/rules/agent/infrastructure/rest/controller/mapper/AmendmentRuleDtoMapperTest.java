package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.AmendmentRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.AmendmentRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.AmendmentRuleResponseDto;

class AmendmentRuleDtoMapperTest {

  private final AmendmentRuleDtoMapper amendmentRuleMapper = new AmendmentRuleDtoMapperImpl();
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyyMMdd'T'HHmmss");

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var amendmentRuleRequestDto = AmendmentRuleRequestDto.builder()
        .rateType("Flex")
        .arrivalDate("20220510")
        .hotelLocalDateTime("20220512T102033")
        .hotelCountryCode("GB")
        .build();
    var amendmentRuleRequest = AmendmentRuleRequest.builder()
        .rateType("Flex")
        .arrivalDate(LocalDate.parse("20220510", DATE_FORMATTER))
        .hotelLocalDateTime(LocalDateTime.parse("20220512T102033", DATE_TIME_FORMATTER))
        .hotelCountryCode("GB")
        .build();

    //Act
    var result = amendmentRuleMapper.toModel(amendmentRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(amendmentRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = amendmentRuleMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var amendmentRuleResponse = AmendmentRuleResponse.builder()
        .isAmendable(true)
        .requestDetails(AmendmentRequestDetails.builder()
            .rateType("Flex")
            .arrivalDate(LocalDate.parse("20220510", DATE_FORMATTER))
            .hotelLocalDateTime(LocalDateTime.parse("20220512T102033", DATE_TIME_FORMATTER))
            .hotelCountryCode("GB")
            .build())
        .generatedAt(time)
        .build();
    var amendmentRuleResponseDto = AmendmentRuleResponseDto.builder()
        .isAmendable(true)
        .requestDetails(AmendmentRequestDetailsDto.builder()
            .rateType("Flex")
            .arrivalDate("20220510")
            .hotelLocalDateTime("20220512T102033")
            .hotelCountryCode("GB")
            .build())
        .generatedAt(time)
        .build();

    //Act
    var result = amendmentRuleMapper.toDto(amendmentRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(amendmentRuleResponseDto);
  }

  @Test
  void toDto_givenNullDetailsObject_shouldTransformToDtoObjectWithNullDetails()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();
    var amendmentRuleResponse = AmendmentRuleResponse.builder()
        .isAmendable(true)
        .requestDetails(AmendmentRequestDetails.builder()
            .rateType("Flex")
            .arrivalDate(LocalDate.parse("20220510", DATE_FORMATTER))
            .hotelLocalDateTime(LocalDateTime.parse("20220512T102033", DATE_TIME_FORMATTER))
            .hotelCountryCode("GB")
            .build())
        .generatedAt(time)
        .build();
    FieldUtils.writeField(amendmentRuleResponse,
        "requestDetails",
        null,
        true);

    //Act
    var result = amendmentRuleMapper.toDto(amendmentRuleResponse);

    //Assert
    assertThat(result.getRequestDetails()).isNull();
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = amendmentRuleMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }
}
