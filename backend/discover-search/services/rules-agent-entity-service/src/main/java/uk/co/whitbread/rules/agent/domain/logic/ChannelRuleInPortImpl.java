package uk.co.whitbread.rules.agent.domain.logic;

import static uk.co.whitbread.rules.agent.domain.constants.RuleConstants.DISTRIBUTION;
import static uk.co.whitbread.rules.agent.domain.constants.RuleConstants.NO_LANGUAGE;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.ChannelRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.ChannelRuleRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class ChannelRuleInPortImpl implements ChannelRuleInPort {

  private final ChannelRuleRepositoryOutPort channelRuleRepositoryOutPort;

  @Override
  public ChannelRuleResponse getChannelRule(ChannelRuleRequest channelRuleRequest) {

    if (!DISTRIBUTION.equals(channelRuleRequest.getChannel())) {
      String channelLanguage = channelRuleRequest.getLanguage();
      if (Strings.isEmpty(channelLanguage)) {
        var message = "Error while trying to get channel language.";
        var exception = new RuleEngineException(ErrorCode.DIGITAL_NO_LANGUAGE_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    } else {
      channelRuleRequest = channelRuleRequest.toBuilder().language(NO_LANGUAGE).build();
    }
    var channelRule = channelRuleRepositoryOutPort.findRule(channelRuleRequest);

    if (channelRule.isEmpty()) {
      var message = "Error while trying to get channel rule.";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_CHANNEL_RULE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    return ChannelRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .sourceId(channelRule.get().getSourceId())
        .ratePlanSets(channelRule.get().getRatePlanSets())
        .requestDetails(ChannelRuleRequestDetails.builder()
            .channel(channelRuleRequest.getChannel())
            .subchannel(channelRuleRequest.getSubchannel())
            .language(channelRuleRequest.getLanguage())
            .pms(channelRuleRequest.getPms())
            .build()
        )
        .build();
  }

  @Override
  public ChannelRuleResponse getChannelRuleBasedOnSourceId(String sourceId) {
    var channelRule = channelRuleRepositoryOutPort.findRule(sourceId);

    if (channelRule.isEmpty()) {
      var message = "Error while trying to get channel rule based on source id.";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_CHANNEL_RULE_SOURCE_ID_EXCEPTION,
            message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    return ChannelRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .sourceId(channelRule.get().getSourceId())
        .ratePlanSets(channelRule.get().getRatePlanSets())
        .requestDetails(ChannelRuleRequestDetails.builder()
            .channel(channelRule.get().getChannel())
            .subchannel(channelRule.get().getSubchannel())
            .language(channelRule.get().getLanguage())
            .pms(channelRule.get().getPms())
            .build()
        )
        .build();
  }

}
