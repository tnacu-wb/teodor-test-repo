package uk.co.whitbread.basket.domain.ports.primary;

import uk.co.whitbread.basket.domain.model.email.in.EmailRequest;

public interface EmailNotificationInPort {

  void triggerEmailNotification(final EmailRequest emailRequest);

}
