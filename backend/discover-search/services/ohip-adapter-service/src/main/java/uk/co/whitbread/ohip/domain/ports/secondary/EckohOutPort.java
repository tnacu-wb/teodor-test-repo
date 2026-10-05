package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;

public interface EckohOutPort {

  void eckohWebhook(EckohWebhook eckohWebhookRequest);
}
