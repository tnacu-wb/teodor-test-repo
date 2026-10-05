package uk.co.whitbread.basket.domain.model.email.out;

public enum EmailNotificationEventType {

  CONFIRM("CONFIRM"),
  CANCEL("CANCEL"),
  AMEND("AMEND"),
  FAIL("FAIL"),
  SPLIT("SPLIT"),
  RESEND_CONF("RESEND_CONF"),
  INVOICE("INVOICE"),
  SECURE_BOOKING("SECURE_BOOKING");



  private final String eventType;

  EmailNotificationEventType(String eventType) {
    this.eventType = eventType;
  }

  @Override
  public String toString() {
    return eventType;
  }

}
