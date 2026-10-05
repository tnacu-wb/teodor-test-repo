package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.ChannelRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.ChannelRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.ChannelRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.ChannelRuleRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.ChannelRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class ChannelRuleControllerIT {

  private static final String SOURCE_ID = "11";
  @InjectMocks
  private ChannelRuleController channelRuleController;
  @Mock
  private ChannelRuleInPort channelRuleInPort;
  @Mock
  private ChannelRuleDtoMapper channelRuleDtoMapper;

  @Test
  void shouldRetrieveChannelRule() {
    //Arrange
    var channelRule = createChannelRuleResponse();
    when(channelRuleDtoMapper.toModel(any())).thenReturn(createChannelRuleRequest());
    when(channelRuleInPort.getChannelRule(any(ChannelRuleRequest.class))).thenReturn(channelRule);
    when(channelRuleDtoMapper.toDto(channelRule)).thenReturn(createChannelRuleResponseDto());

    //Act
    var response = channelRuleController.getChannelRule(createChannelRuleRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals("11", response.getSourceId());
    Assertions.assertEquals("WEB", response.getRequestDetails().getSubchannel());
  }

  @Test
  void shouldRetrieveChannelRuleBasedOnSourceId() {
    //Arrange
    var channelRule = createChannelRuleResponse();
    when(channelRuleInPort.getChannelRuleBasedOnSourceId(SOURCE_ID)).thenReturn(channelRule);
    when(channelRuleDtoMapper.toDto(channelRule)).thenReturn(createChannelRuleResponseDto());

    //Act
    var response = channelRuleController.getChannelRuleBasedOnSourceId("11");

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals("WEB", response.getRequestDetails().getSubchannel());
    Assertions.assertEquals("PI", response.getRequestDetails().getChannel());
  }

  @Test
  void shouldHandleChannelRuleNotFound() {
    //Arrange
    when(channelRuleDtoMapper.toModel(any())).thenReturn(null);
    when(channelRuleInPort.getChannelRule(null)).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_CHANNEL_RULE_EXCEPTION,
              "Channel rule not found."));

    //Assert
    assertThrows(RuleEngineException.class, () -> channelRuleController.getChannelRule(createChannelRuleRequestDto()));
  }

  private ChannelRuleResponse createChannelRuleResponse() {
    return ChannelRuleResponse.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBN"))
        .requestDetails(ChannelRuleRequestDetails.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .pms("OP")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private ChannelRuleResponseDto createChannelRuleResponseDto() {
    return ChannelRuleResponseDto.builder()
        .sourceId("11")
        .ratePlanSets(List.of("PBN"))
        .requestDetails(ChannelRuleRequestDetailsDto.builder()
            .channel("PI")
            .subchannel("WEB")
            .language("EN")
            .pms("OP")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private ChannelRuleRequest createChannelRuleRequest() {
    return ChannelRuleRequest.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build();
  }
  private ChannelRuleRequestDto createChannelRuleRequestDto() {
    return ChannelRuleRequestDto.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build();
  }
}
