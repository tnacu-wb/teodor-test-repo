package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.MaxNightsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxNightsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRuleResponseDto;

class MaxNightsRuleDtoMapperTest {

  private final MaxNightsRuleDtoMapper maxNightsRuleDtoMapper = new MaxNightsRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var maxNightsRuleRequestDto = MaxNightsRuleRequestDto.builder()
        .channelId("CCUI")
        .build();
    var maxNightsRuleRequest = MaxNightsRuleRequest.builder()
        .channelId("CCUI")
        .build();

    //Act
    var result = maxNightsRuleDtoMapper.toModel(maxNightsRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxNightsRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = maxNightsRuleDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleResponse = MaxNightsRuleResponse.builder()
        .maxNights(14)
        .requestDetails(MaxNightsRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();
    var maxNightsRuleResponseDto = MaxNightsRuleResponseDto.builder()
        .maxNights(14)
        .requestDetails(MaxNightsRequestDetailsDto.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();

    //Act
    var result = maxNightsRuleDtoMapper.toDto(rbacRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxNightsRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = maxNightsRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenNullDetailsObject_shouldTransformToDtoObjectWithNullDetails()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();
    var maxNightsRuleResponse = MaxNightsRuleResponse.builder()
        .maxNights(14)
        .requestDetails(MaxNightsRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();
    FieldUtils.writeField(maxNightsRuleResponse,
        "requestDetails",
        null,
        true);

    //Act
    var result = maxNightsRuleDtoMapper.toDto(maxNightsRuleResponse);

    //Assert
    assertThat(result.getRequestDetails()).isNull();
  }

}
