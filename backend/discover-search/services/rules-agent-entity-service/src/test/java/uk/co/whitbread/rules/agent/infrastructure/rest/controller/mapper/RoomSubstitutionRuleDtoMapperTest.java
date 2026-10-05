package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitution;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionRuleResponseDto;

class RoomSubstitutionRuleDtoMapperTest {

  private static final String OP = "OP";
  private static final String DOUBLE = "DOUBLE";
  private static final String DBLWIN = "DBLWIN";
  private static final String CHANNEL = "PI";
  private final RoomSubstitutionRuleDtoMapper roomSubstitutionRuleDtoMapper = new RoomSubstitutionRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var roomSubstitutionRuleRequestDto = RoomSubstitutionRuleRequestDto.builder()
        .adults(2)
        .children(0)
        .pms(OP)
        .roomType(DOUBLE)
        .channel(CHANNEL)
        .build();
    var roomSubstitutionRuleRequest = RoomSubstitutionRuleRequest.builder()
        .adults(2)
        .children(0)
        .pms(OP)
        .roomType(DOUBLE)
        .channel(CHANNEL)
        .build();

    //Act
    var result = roomSubstitutionRuleDtoMapper.toModel(roomSubstitutionRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(roomSubstitutionRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = roomSubstitutionRuleDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var roomSubstitutionRuleResponse = RoomSubstitutionRuleResponse.builder()
        .generatedAt(time)
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(2)
            .children(0)
            .pms(OP)
            .roomType(DOUBLE)
            .channel(CHANNEL)
            .build())
        .substitutionList(of(RoomSubstitution.builder()
            .silent(true)
            .type(DBLWIN)
            .build()))
        .build();
    var roomSubstitutionRuleResponseDto = RoomSubstitutionRuleResponseDto.builder()
        .generatedAt(time)
        .requestDetails(RoomSubstitutionRequestDetailsDto.builder()
            .adults(2)
            .children(0)
            .pms(OP)
            .roomType(DOUBLE)
            .channel(CHANNEL)
            .build())
        .substitutionList(of(RoomSubstitutionDto.builder()
            .silent(true)
            .type(DBLWIN)
            .build()))
        .build();

    //Act
    var result = roomSubstitutionRuleDtoMapper.toDto(roomSubstitutionRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(roomSubstitutionRuleResponseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = roomSubstitutionRuleDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenNullDetailsObject_shouldTransformToDtoObjectWithNullDetails()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();
    var roomSubstitutionRuleResponse = RoomSubstitutionRuleResponse.builder()
        .generatedAt(time)
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(2)
            .children(0)
            .pms(OP)
            .roomType(DOUBLE)
            .channel(CHANNEL)
            .build())
        .substitutionList(of(RoomSubstitution.builder()
            .silent(true)
            .type(DBLWIN)
            .build()))
        .build();
    FieldUtils.writeField(roomSubstitutionRuleResponse,
        "requestDetails",
        null,
        true);

    //Act
    var result = roomSubstitutionRuleDtoMapper.toDto(roomSubstitutionRuleResponse);

    //Assert
    assertThat(result.getRequestDetails()).isNull();
  }

}
