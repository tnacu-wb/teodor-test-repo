package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.MaxArrivalDateRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxArrivalDateRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRuleResponseDto;

class MaxArrivalDateRuleDtoMapperTest {

  private final MaxArrivalDateRuleDtoMapper maxArrivalDateRuleDtoMapper = new MaxArrivalDateRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var maxArrivalDateRuleRequestDto = MaxArrivalDateRuleRequestDto.builder()
        .channelId("CCUI")
        .build();
    var maxArrivalDateRuleRequest = MaxArrivalDateRuleRequest.builder()
        .channelId("CCUI")
        .build();

    //Act
    var result = maxArrivalDateRuleDtoMapper.toModel(maxArrivalDateRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxArrivalDateRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = maxArrivalDateRuleDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleResponse = MaxArrivalDateRuleResponse.builder()
        .maxArrivalDate(14)
        .requestDetails(MaxArrivalDateRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();
    var maxArrivalDateRuleResponseDto = MaxArrivalDateRuleResponseDto.builder()
        .maxArrivalDate(14)
        .requestDetails(MaxArrivalDateRequestDetailsDto.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();

    //Act
    var result = maxArrivalDateRuleDtoMapper.toDto(rbacRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxArrivalDateRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = maxArrivalDateRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenNullDetailsObject_shouldTransformToDtoObjectWithNullDetails()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();
    var maxArrivalDateRuleResponse = MaxArrivalDateRuleResponse.builder()
        .maxArrivalDate(14)
        .requestDetails(MaxArrivalDateRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();
    FieldUtils.writeField(maxArrivalDateRuleResponse,
        "requestDetails",
        null,
        true);

    //Act
    var result = maxArrivalDateRuleDtoMapper.toDto(maxArrivalDateRuleResponse);

    //Assert
    assertThat(result.getRequestDetails()).isNull();
  }
}
