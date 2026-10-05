package uk.co.whitbread.rules.agent.domain.logic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.ChannelRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRule;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.ports.secondary.ChannelRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
public class ChannelRuleInPortImplTest {

  private static final String DISTRIBUTION_CHANNEL = "DISTR";
  private static final String PI_CHANNEL = "PI";
  private static final String BOOKING_SUBCHANNEL = "BOOKING";
  private static final String WEB_SUBCHANNEL = "WEB";
  private static final String PMS = "OP";
  private static final String SOURCE_ID = "123456";

  @Mock
  private ChannelRuleRepositoryOutPort channelRuleRepositoryOutPort;
  @InjectMocks
  private ChannelRuleInPortImpl channelRuleInport;

  @Test
  void getChannelRule_shouldThrowExceptionIfRuleNotFound() {

    var channelRuleRequest = mockChannelRuleRequestForPI();

    when(channelRuleRepositoryOutPort.findRule(channelRuleRequest))
        .thenReturn(Optional.empty());

    assertThrows(RuleEngineException.class,
        () -> channelRuleInport.getChannelRule(channelRuleRequest));
    verifyNoMoreInteractions(channelRuleRepositoryOutPort);
  }

  @Test
  void getChannelRule_shouldThrowException_whenChannelIsNotDistributionAndLanguageIsEmpty() {

    var channelRuleRequest = ChannelRuleRequest.builder()
        .channel(PI_CHANNEL)
        .subchannel(WEB_SUBCHANNEL)
        .pms(PMS)
        .language("")
        .build();

    assertThrows(RuleEngineException.class,
        () -> channelRuleInport.getChannelRule(channelRuleRequest));
    verifyNoMoreInteractions(channelRuleRepositoryOutPort);
  }

  @Test
  void getChannelRule_whenChannelIsNotDistribution() {

    var channelRuleRequest = mockChannelRuleRequestForPI();

    when(channelRuleRepositoryOutPort.findRule(channelRuleRequest))
        .thenReturn(Optional.of(mockChannelRuleForPI()));

    var channelRuleResponse = channelRuleInport.getChannelRule(channelRuleRequest);
    assertEquals("EN", channelRuleResponse.getRequestDetails().getLanguage());
    commonAssertions(channelRuleResponse, channelRuleRequest);
    verifyNoMoreInteractions(channelRuleRepositoryOutPort);
  }

  @Test
  void getChannelRule_whenChannelIsDistribution() {

    var channelRuleRequest = mockChannelRuleRequestForDistr();

    when(channelRuleRepositoryOutPort.findRule(channelRuleRequest))
        .thenReturn(Optional.of(mockChannelRuleForDistr()));

    var channelRuleResponse = channelRuleInport.getChannelRule(channelRuleRequest);
    assertEquals("N/A", channelRuleResponse.getRequestDetails().getLanguage());
    commonAssertions(channelRuleResponse, channelRuleRequest);
    verifyNoMoreInteractions(channelRuleRepositoryOutPort);
  }

  @Test
  void givenValidSourceId_whenGetChannelRuleBasedOnSourceId_returnChannelRuleResponse() {
    when(channelRuleRepositoryOutPort.findRule(SOURCE_ID)).thenReturn(Optional.of(mockChannelRuleForDistr()));

    var channelRuleResponse = channelRuleInport.getChannelRuleBasedOnSourceId(SOURCE_ID);

    assertEquals("N/A", channelRuleResponse.getRequestDetails().getLanguage());
    assertEquals(DISTRIBUTION_CHANNEL, channelRuleResponse.getRequestDetails().getChannel());
    assertEquals(BOOKING_SUBCHANNEL, channelRuleResponse.getRequestDetails().getSubchannel());
    assertEquals(PMS, channelRuleResponse.getRequestDetails().getPms());
    assertEquals(SOURCE_ID, channelRuleResponse.getSourceId());
    assertEquals(3, channelRuleResponse.getRatePlanSets().size());
    assertNotNull(channelRuleResponse.getGeneratedAt());
    verifyNoMoreInteractions(channelRuleRepositoryOutPort);
  }

  @Test
  void givenInvalidSourceId_whenGetChannelRuleBasedOnSourceId_throwRuleEngineException() {
    when(channelRuleRepositoryOutPort.findRule(SOURCE_ID)).thenReturn(Optional.empty());

    assertThrows(RuleEngineException.class,
        () ->  channelRuleInport.getChannelRuleBasedOnSourceId(SOURCE_ID));
  }

  private void commonAssertions(ChannelRuleResponse channelRuleResponse,
      ChannelRuleRequest channelRuleRequest) {
    ChannelRuleRequestDetails channelRuleRequestDetails = channelRuleResponse.getRequestDetails();
    assertThat(channelRuleRequestDetails.getChannel()).isEqualTo(channelRuleRequest.getChannel());
    assertThat(channelRuleRequestDetails.getSubchannel())
        .isEqualTo(channelRuleRequest.getSubchannel());
    assertThat(channelRuleRequestDetails.getLanguage()).isEqualTo(channelRuleRequest.getLanguage());
    assertThat(channelRuleRequestDetails.getPms()).isEqualTo(channelRuleRequest.getPms());
  }

  ChannelRuleRequest mockChannelRuleRequestForPI() {
    return ChannelRuleRequest.builder()
        .channel(PI_CHANNEL)
        .subchannel(WEB_SUBCHANNEL)
        .language("EN")
        .pms(PMS)
        .build();
  }

  ChannelRuleRequest mockChannelRuleRequestForDistr() {
    return ChannelRuleRequest.builder()
        .channel(DISTRIBUTION_CHANNEL)
        .subchannel(BOOKING_SUBCHANNEL)
        .pms(PMS)
        .build();
  }

  ChannelRule mockChannelRuleForPI() {

    return ChannelRule.builder()
        .channel(PI_CHANNEL)
        .subchannel(WEB_SUBCHANNEL)
        .language("EN")
        .pms(PMS)
        .sourceId(SOURCE_ID)
        .ruleId(1)
        .lastModifiedAt(LocalDateTime.of(2023, Month.JANUARY,
            12,12,12,12))
        .createdAt(LocalDateTime.of(2022, Month.DECEMBER,
            12, 12,12,12))
        .status(RuleStatus.ACTIVE)
        .ratePlanSets(List.of("A", "B", "C"))
        .build();
  }

  ChannelRule mockChannelRuleForDistr() {

    return ChannelRule.builder()
        .channel(DISTRIBUTION_CHANNEL)
        .subchannel(BOOKING_SUBCHANNEL)
        .language("N/A")
        .pms(PMS)
        .sourceId(SOURCE_ID)
        .ruleId(1)
        .lastModifiedAt(LocalDateTime.of(2023, Month.JANUARY,
            12,12,12,12))
        .createdAt(LocalDateTime.of(2022, Month.DECEMBER,
            12, 12,12,12))
        .status(RuleStatus.ACTIVE)
        .ratePlanSets(List.of("A", "B", "C"))
        .build();
  }


}
