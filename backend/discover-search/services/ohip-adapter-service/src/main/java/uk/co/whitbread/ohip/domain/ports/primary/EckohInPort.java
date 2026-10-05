package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;

public interface EckohInPort {

  void eckohWebhook(EckohWebhook eckohWebhookRequest);

}
