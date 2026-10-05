package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.eckoh.in.EckohWebhook;
import uk.co.whitbread.ohip.domain.ports.primary.EckohInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.EckohOutPort;

@RequiredArgsConstructor
@Slf4j
public class EckohInPortImpl implements EckohInPort {

  private final EckohOutPort eckohOutPort;
  private static final int SUCCES_CODE = 100;

  @Override
  public void eckohWebhook(EckohWebhook eckohWebhookRequest) {
    if (eckohWebhookRequest.getResultCode() != SUCCES_CODE) {
      log.warn("Eckoh unable to capture card details");
      return;
    }
    eckohOutPort.eckohWebhook(eckohWebhookRequest);
  }
}
