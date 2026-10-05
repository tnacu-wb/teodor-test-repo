package uk.co.whitbread.payapp.domain.ports.secondary;

public interface EmailNotificationOutPort {

  void sendShareAppEmailNotificationEvent(String sharedWith, String sharedBy,
      String applicationGuid, String language);
}
