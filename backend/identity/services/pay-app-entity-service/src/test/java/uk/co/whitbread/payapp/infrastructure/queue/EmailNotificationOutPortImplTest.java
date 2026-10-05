package uk.co.whitbread.payapp.infrastructure.queue;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payapp.infrastructure.queue.model.ExtraDetails;
import uk.co.whitbread.payapp.infrastructure.queue.producer.EmailNotificationProducer;

@ExtendWith(MockitoExtension.class)
class EmailNotificationOutPortImplTest {

  private EmailNotificationOutPortImpl emailNotificationOutPort;

  @Mock
  private EmailNotificationProducer emailNotificationProducer;

  @BeforeEach
  void setUp() {
    emailNotificationOutPort = new EmailNotificationOutPortImpl(emailNotificationProducer);
  }

  @Test
  void testSendShareAppEmailNotificationEvent() {
    // Arrange
    String sharedWith = "test@example.com";
    String sharedBy = "user123";
    String applicationGuid = "app-guid-123";
    String language = "en";

    // Act
    emailNotificationOutPort.sendShareAppEmailNotificationEvent(sharedWith, sharedBy, applicationGuid, language);

    // Assert
    verify(emailNotificationProducer).sendShareAppEmailNotificationEvent(argThat(event -> {
      assert event.getId() != null;
      assert event.getType().equals("PAY_APP_SHARE");
      assert event.getLanguage().equals(language);
      assert event.getEmailAddress().equals(sharedWith);
      ExtraDetails extraDetails = event.getExtraDetails();
      assert extraDetails.getSharedBy().equals(sharedBy);
      assert extraDetails.getApplicationGuid().equals(applicationGuid);
      return true;
    }));
  }
}