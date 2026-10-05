package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.RbacRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RbacRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RbacRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RbacRuleResponseDto;

class RbacRuleDtoMapperTest {

  private final RbacRuleDtoMapper rbacRuleDtoMapper = new RbacRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var rbacRuleRequestDto = RbacRuleRequestDto.builder()
        .resourceId("CCUI_RES1")
        .roleId("AGENT")
        .build();
    var rbacRuleRequest = RbacRuleRequest.builder()
        .resourceId("CCUI_RES1")
        .roleId("AGENT")
        .build();

    //Act
    var result = rbacRuleDtoMapper.toModel(rbacRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(rbacRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = rbacRuleDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var rbacRuleResponse = RbacRuleResponse.builder()
        .hasAccess(true)
        .generatedAt(time)
        .build();
    var rbacRuleResponseDto = RbacRuleResponseDto.builder()
        .hasAccess(true)
        .generatedAt(time)
        .build();

    //Act
    var result = rbacRuleDtoMapper.toDto(rbacRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(rbacRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = rbacRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

}
