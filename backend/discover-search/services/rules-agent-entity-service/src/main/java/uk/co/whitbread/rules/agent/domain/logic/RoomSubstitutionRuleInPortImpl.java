package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitution;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.RoomSubstitutionRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomSubstitutionRuleRepositoryOutPort;

@RequiredArgsConstructor
public class RoomSubstitutionRuleInPortImpl implements RoomSubstitutionRuleInPort {

  private static final int SILENT_SUBSTITUTION_THRESHOLD = 100;

  private final RoomSubstitutionRuleRepositoryOutPort roomSubstitutionRuleRepositoryOutPort;

  @Override
  public RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest domainRoomSubstitutionRuleRequest) {
    return RoomSubstitutionRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .substitutionList(obtainListOfSubstitutions(domainRoomSubstitutionRuleRequest))
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(domainRoomSubstitutionRuleRequest.getAdults())
            .children(domainRoomSubstitutionRuleRequest.getChildren())
            .roomType(domainRoomSubstitutionRuleRequest.getRoomType())
            .pms(domainRoomSubstitutionRuleRequest.getPms())
            .channel(domainRoomSubstitutionRuleRequest.getChannel())
            .build())
        .build();
  }

  private List<RoomSubstitution> obtainListOfSubstitutions(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest) {
    return roomSubstitutionRuleRepositoryOutPort.findRoomSubstitutionRules(
            roomSubstitutionRuleRequest)
        .stream()
        .sorted(Comparator.comparing(RoomSubstitutionRule::getOfferOrder))
        .map(roomSubstitutionRule -> RoomSubstitution.builder()
            .accessibleSpecialRequest(roomSubstitutionRule.getAccessibleSpecialRequest())
            .codePackage(roomSubstitutionRule.getCodePackage())
            .silent(isRoomSilentlySubstituted(roomSubstitutionRule))
            .specialRequest(roomSubstitutionRule.getSpecialRequest())
            .type(roomSubstitutionRule.getPmsRoomType())
            .build())
        .toList();
  }

  private boolean isRoomSilentlySubstituted(RoomSubstitutionRule roomSubstitutionRule) {
    return roomSubstitutionRule.getOfferOrder() <= SILENT_SUBSTITUTION_THRESHOLD;
  }

}
