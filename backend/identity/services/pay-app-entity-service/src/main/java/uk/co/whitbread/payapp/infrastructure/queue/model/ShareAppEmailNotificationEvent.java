package uk.co.whitbread.payapp.infrastructure.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString(exclude = "emailAddress")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public final class ShareAppEmailNotificationEvent {

  private String id;
  private String emailAddress;
  private String type; // PAY_APP_SHARE
  private String language;
  private ExtraDetails extraDetails;
}