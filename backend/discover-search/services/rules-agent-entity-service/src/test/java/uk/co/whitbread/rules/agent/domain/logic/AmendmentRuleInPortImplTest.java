package uk.co.whitbread.rules.agent.domain.logic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.AmendmentRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRule;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRuleResponse;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.ports.secondary.AmendmentRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class AmendmentRuleInPortImplTest {

  @Mock
  private AmendmentRuleRepositoryOutPort amendmentRuleRepository;
  @InjectMocks
  private AmendmentRuleInPortImpl amendmentRuleInPort;
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyyMMdd'T'HHmmss");

  @Test
  void getAmendmentRule__shouldThrowExceptionIfRuleNotFound() {
    //Arrange
    var dummyAmendmentRuleRequest = createAmendmentRuleRequest();
    when(amendmentRuleRepository.findRule(dummyAmendmentRuleRequest.getRateType(),
        dummyAmendmentRuleRequest.getHotelCountryCode())).thenReturn(
        Optional.empty());

    //Act
    assertThrows(RuleEngineException.class,
        () -> amendmentRuleInPort.getAmendmentRule(dummyAmendmentRuleRequest));

    //Assert
    verifyNoMoreInteractions(amendmentRuleRepository);
  }

  @Test
  void getAmendmentRule__shouldReturnIsAmendableFalseWhenTryToAmendAfterAmendmentWindow() {
    //Arrange
    var amendmentRule = createAmendmentRule().toBuilder().arrivalDateLimit(39600).build();
    var amendmentRuleRequest = createAmendmentRuleRequest()
        .toBuilder().hotelLocalDateTime(LocalDateTime.parse("20220504T130001",
            DATE_TIME_FORMATTER)).build();
    when(amendmentRuleRepository.findRule(amendmentRuleRequest.getRateType(),
        amendmentRuleRequest.getHotelCountryCode())).thenReturn(
        Optional.of(amendmentRule));

    //Act
    var response = amendmentRuleInPort.getAmendmentRule(amendmentRuleRequest);

    //Assert
    assertThat(response.getIsAmendable()).isFalse();
    commonAssertions(response, amendmentRuleRequest);
    verifyNoMoreInteractions(amendmentRuleRepository);
  }

  @Test
  void getAmendmentRule__shouldReturnIsAmendableTrueWhenTryToAmendBeforeAmendmentWindow() {
    //Arrange
    var amendmentRule = createAmendmentRule();
    var amendmentRuleRequest = createAmendmentRuleRequest()
        .toBuilder().hotelLocalDateTime(LocalDateTime.parse("20220504T130000",
            DATE_TIME_FORMATTER)).build();
    when(amendmentRuleRepository.findRule(amendmentRuleRequest.getRateType(),
        amendmentRuleRequest.getHotelCountryCode())).thenReturn(
        Optional.of(amendmentRule));

    //Act
    var response = amendmentRuleInPort.getAmendmentRule(amendmentRuleRequest);

    //Assert
    assertThat(response.getIsAmendable()).isTrue();
    commonAssertions(response, amendmentRuleRequest);
    verifyNoMoreInteractions(amendmentRuleRepository);
  }

  @Test
  void getAmendmentRule__shouldReturnIsAmendableTrueWhenTryToAmendBeforeArrivalDate() {
    //Arrange
    var amendmentRule = createAmendmentRule();
    var amendmentRuleRequest = createAmendmentRuleRequest()
        .toBuilder()
        .arrivalDate(LocalDate.parse("20220504", DATE_FORMATTER))
        .hotelLocalDateTime(LocalDateTime.parse("20220503T130000",
            DATE_TIME_FORMATTER))
        .hotelCountryCode("GB")
        .build();
    when(amendmentRuleRepository.findRule(amendmentRuleRequest.getRateType(),
        amendmentRuleRequest.getHotelCountryCode())).thenReturn(
        Optional.of(amendmentRule));

    //Act
    var response = amendmentRuleInPort.getAmendmentRule(amendmentRuleRequest);

    //Assert
    assertThat(response.getIsAmendable()).isTrue();
    commonAssertions(response, amendmentRuleRequest);
    verifyNoMoreInteractions(amendmentRuleRepository);
  }

  private AmendmentRuleRequest createAmendmentRuleRequest() {
    return AmendmentRuleRequest.builder()
        .rateType("Flex")
        .arrivalDate(LocalDate.parse("20220504", DATE_FORMATTER))
        .hotelLocalDateTime(LocalDateTime.parse("20220504T130000",
            DATE_TIME_FORMATTER))
        .hotelCountryCode("GB")
        .build();
  }

  private AmendmentRule createAmendmentRule() {
    return AmendmentRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.of(2022, 4, 1, 10, 23, 55))
        .lastModifiedAt(LocalDateTime.of(2022, 4, 1, 10, 23, 55))
        .rateType("Flex")
        .countryCode("GB")
        .arrivalDateLimit(39600)
        .build();
  }

  private void commonAssertions(AmendmentRuleResponse amendmentRuleResponse,
      AmendmentRuleRequest amendmentRuleRequest) {
    assertThat(amendmentRuleResponse.getGeneratedAt()).isNotNull();
    AmendmentRequestDetails requestDetails = amendmentRuleResponse.getRequestDetails();
    assertThat(requestDetails.getRateType()).isEqualTo(amendmentRuleRequest.getRateType());
    assertThat(requestDetails.getArrivalDate()).isEqualTo(amendmentRuleRequest.getArrivalDate());
    assertThat(requestDetails.getHotelLocalDateTime()).isEqualTo(
        amendmentRuleRequest.getHotelLocalDateTime());
  }
}
