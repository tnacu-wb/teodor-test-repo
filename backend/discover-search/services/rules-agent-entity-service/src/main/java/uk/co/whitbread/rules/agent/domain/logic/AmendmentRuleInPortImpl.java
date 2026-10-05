package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.AmendmentRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.AmendmentRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.AmendmentRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.AmendmentRuleRepositoryOutPort;

@Slf4j
@RequiredArgsConstructor
public class AmendmentRuleInPortImpl implements AmendmentRuleInPort {

  private static final int SECONDS_IN_A_DAY = 86400;
  private final AmendmentRuleRepositoryOutPort amendmentRuleRepository;

  @Override
  public AmendmentRuleResponse getAmendmentRule(AmendmentRuleRequest domainAmendmentRuleRequest) {
    return AmendmentRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .isAmendable(calculateIsAmendable(domainAmendmentRuleRequest))
        .requestDetails(AmendmentRequestDetails.builder()
            .arrivalDate(domainAmendmentRuleRequest.getArrivalDate())
            .rateType(domainAmendmentRuleRequest.getRateType())
            .hotelLocalDateTime(domainAmendmentRuleRequest.getHotelLocalDateTime())
            .hotelCountryCode(domainAmendmentRuleRequest.getHotelCountryCode())
            .build()
        )
        .build();
  }

  private boolean calculateIsAmendable(AmendmentRuleRequest request) {
    var ruleOptional = amendmentRuleRepository.findRule(request.getRateType(),
        request.getHotelCountryCode());
    if (ruleOptional.isEmpty()) {
      var message = String.format("Error while calculating if reservation is amendable, the rule is empty"
            + " for amendmentRequest=%s", request);
      var exception = new RuleEngineException(ErrorCode.DIGITAL_NOT_AMENDABLE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    var rule = ruleOptional.get();

    if (rule.getArrivalDateLimit() == null) {
      return false;
    }

    var hotelLocalDate = request.getHotelLocalDateTime().toLocalDate();
    var hotelLocalTime = request.getHotelLocalDateTime().toLocalTime();

    return !(request.getArrivalDate().isEqual(hotelLocalDate)
        && hotelLocalTime.toSecondOfDay() > SECONDS_IN_A_DAY - rule.getArrivalDateLimit());
  }
}
