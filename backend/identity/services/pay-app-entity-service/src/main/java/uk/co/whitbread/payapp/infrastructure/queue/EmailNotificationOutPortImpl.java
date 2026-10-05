package uk.co.whitbread.payapp.infrastructure.queue;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import uk.co.whitbread.payapp.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.payapp.infrastructure.queue.model.ExtraDetails;
import uk.co.whitbread.payapp.infrastructure.queue.model.ShareAppEmailNotificationEvent;
import uk.co.whitbread.payapp.infrastructure.queue.producer.EmailNotificationProducer;

@Slf4j
@AllArgsConstructor
public class EmailNotificationOutPortImpl implements EmailNotificationOutPort {

  private static final String PAY_APP_SHARE_EVENT_TYPE = "PAY_APP_SHARE";
  private final EmailNotificationProducer emailNotificationProducer;

  @Async
  @Override
  public void sendShareAppEmailNotificationEvent(String sharedWith, String sharedBy,
      String applicationGuid, String language) {

    var emailNotificationEvent = ShareAppEmailNotificationEvent.builder()
        .id(UUID.randomUUID().toString())
        .type(PAY_APP_SHARE_EVENT_TYPE)
        .language(language)
        .emailAddress(sharedWith)
        .extraDetails(ExtraDetails.builder()
            .sharedBy(sharedBy)
            .applicationGuid(applicationGuid)
            .build())
        .build();

    emailNotificationProducer.sendShareAppEmailNotificationEvent(emailNotificationEvent);
  }
}