package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRuleResponseDto;

class MaxRoomsRuleDtoMapperTest {

  private final MaxRoomsRuleDtoMapper maxRoomsRuleDtoMapper = new MaxRoomsRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var maxRoomsRuleRequestDto = MaxRoomsRuleRequestDto.builder()
        .channelId("CCUI")
        .build();
    var maxRoomsRuleRequest = MaxRoomsRuleRequest.builder()
        .channelId("CCUI")
        .build();

    //Act
    var result = maxRoomsRuleDtoMapper.toModel(maxRoomsRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomsRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = maxRoomsRuleDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleResponse = MaxRoomsRuleResponse.builder()
        .maxRooms(14)
        .requestDetails(MaxRoomsRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();
    var maxRoomsRuleResponseDto = MaxRoomsRuleResponseDto.builder()
        .maxRooms(14)
        .requestDetails(MaxRoomsRequestDetailsDto.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();

    //Act
    var result = maxRoomsRuleDtoMapper.toDto(rbacRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(maxRoomsRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = maxRoomsRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenNullDetailsObject_shouldTransformToDtoObjectWithNullDetails()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();
    var maxRoomsRuleResponse = MaxRoomsRuleResponse.builder()
        .maxRooms(14)
        .requestDetails(MaxRoomsRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(time)
        .build();
    FieldUtils.writeField(maxRoomsRuleResponse,
        "requestDetails",
        null,
        true);

    //Act
    var result = maxRoomsRuleDtoMapper.toDto(maxRoomsRuleResponse);

    //Assert
    assertThat(result.getRequestDetails()).isNull();
  }

}
