package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonIconsResponseAemDto {

  @JsonProperty("icon.payment.visa")
  private String paymentVisa;

  @JsonProperty("icon.chevron.up")
  private String chevronUp;

  @JsonProperty("icon.notification.error")
  private String notificationError;

  @JsonProperty("icon.chevron.right")
  private String chevronRight;

  @JsonProperty("icon.arrow.down")
  private String arrowDown;

  @JsonProperty("icon.arrow.left")
  private String arrowLeft;

  @JsonProperty("icon.arrow.up")
  private String arrowUp;

  @JsonProperty("icon.arrow.right")
  private String arrowRight;

  @JsonProperty("icon.payment.piba")
  private String paymentPiba;

  @JsonProperty("icon.payment.amex")
  private String paymentAmex;

  @JsonProperty("icon.payment.piba-euro")
  private String paymentPibaEuro;

  @JsonProperty("icon.notification.info")
  private String notificationInfo;

  @JsonProperty("icon.notification.question")
  private String notificationQuestion;

  @JsonProperty("icon.chevron.up.purple")
  private String chevronUpPurple;

  @JsonProperty("icon.chevron.right.purple")
  private String chevronRightPurple;

  @JsonProperty("icon.chevron.down.purple")
  private String chevronDownPurple;

  @JsonProperty("icon.chevron.left.purple")
  private String chevronLeftPurple;

  @JsonProperty("icon.chevron.left")
  private String chevronLeft;

  @JsonProperty("icon.payment.mastercard")
  private String paymentMastercard;

  @JsonProperty("icon.notification.success")
  private String notificationSuccess;

  @JsonProperty("icon.chevron.down")
  private String chevronDown;

  @JsonProperty("icon.notification.alert")
  private String notificationAlert;

}
