package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.ChannelRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.ChannelRuleRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.ChannelRuleResponseDto;

class ChannelRuleDtoMapperTest {

  private final ChannelRuleDtoMapper channelRuleMapper = new ChannelRuleDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var channelRuleRequestDto = ChannelRuleRequestDto.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build();
    var channelRuleRequest = ChannelRuleRequest.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build();

    //Act
    var result = channelRuleMapper.toModel(channelRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(channelRuleRequest);
  }

  @Test
  void toModel_givenDtoObjectWithNullLanguage_shouldTransformToModelObjectWithNALanguage() {
    //Arrange
    var channelRuleRequestDto = ChannelRuleRequestDto.builder()
        .channel("PI")
        .subchannel("WEB")
        .pms("OP")
        .build();
    var channelRuleRequest = ChannelRuleRequest.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("N/A")
        .pms("OP")
        .build();

    //Act
    var result = channelRuleMapper.toModel(channelRuleRequestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(channelRuleRequest);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = channelRuleMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleResponse = ChannelRuleResponse.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBN"))
        .requestDetails(ChannelRuleRequestDetails.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .pms("OP")
            .build())
        .generatedAt(time)
        .build();
    var channelRuleResponseDto = ChannelRuleResponseDto.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBN"))
        .requestDetails(ChannelRuleRequestDetailsDto.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .pms("OP")
            .build())
        .generatedAt(time)
        .build();

    //Act
    var result = channelRuleMapper.toDto(channelRuleResponse);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(channelRuleResponseDto);
  }

  @Test
  void toDto_givenNullDetailsObject_shouldTransformToDtoObjectWithNullDetails()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();
    var channelRuleResponse = ChannelRuleResponse.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBN"))
        .requestDetails(ChannelRuleRequestDetails.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .pms("OP")
            .build())
        .generatedAt(time)
        .build();
    FieldUtils.writeField(channelRuleResponse,
        "requestDetails",
        null,
        true);

    //Act
    var result = channelRuleMapper.toDto(channelRuleResponse);

    //Assert
    assertThat(result.getRequestDetails()).isNull();
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNull() {
    //Arrange

    //Act
    var result = channelRuleMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }
}
