package uk.co.whitbread.rules.agent.domain.ports.primary;

import uk.co.whitbread.rules.agent.domain.model.out.PaypalRuleResponse;

public interface PaypalRuleInPort {


  PaypalRuleResponse getPaypalHasAccess(String channelId, String country, String hotelId);


}
