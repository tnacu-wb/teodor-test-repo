package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.ArrowDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ChevronDto;
import uk.co.whitbread.content.entity.service.generated.models.content.NotificationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PaymentDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CommonIconsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CommonIconsDto {

  private @Nullable ArrowDto arrow;

  private @Nullable ChevronDto chevron;

  private @Nullable NotificationDto notification;

  private @Nullable PaymentDto payment;

  public CommonIconsDto arrow(ArrowDto arrow) {
    this.arrow = arrow;
    return this;
  }

  /**
   * Get arrow
   * @return arrow
   */
  @Valid 
  @Schema(name = "arrow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("arrow")
  public ArrowDto getArrow() {
    return arrow;
  }

  public void setArrow(ArrowDto arrow) {
    this.arrow = arrow;
  }

  public CommonIconsDto chevron(ChevronDto chevron) {
    this.chevron = chevron;
    return this;
  }

  /**
   * Get chevron
   * @return chevron
   */
  @Valid 
  @Schema(name = "chevron", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("chevron")
  public ChevronDto getChevron() {
    return chevron;
  }

  public void setChevron(ChevronDto chevron) {
    this.chevron = chevron;
  }

  public CommonIconsDto notification(NotificationDto notification) {
    this.notification = notification;
    return this;
  }

  /**
   * Get notification
   * @return notification
   */
  @Valid 
  @Schema(name = "notification", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("notification")
  public NotificationDto getNotification() {
    return notification;
  }

  public void setNotification(NotificationDto notification) {
    this.notification = notification;
  }

  public CommonIconsDto payment(PaymentDto payment) {
    this.payment = payment;
    return this;
  }

  /**
   * Get payment
   * @return payment
   */
  @Valid 
  @Schema(name = "payment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("payment")
  public PaymentDto getPayment() {
    return payment;
  }

  public void setPayment(PaymentDto payment) {
    this.payment = payment;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommonIconsDto commonIconsDto = (CommonIconsDto) o;
    return Objects.equals(this.arrow, commonIconsDto.arrow) &&
        Objects.equals(this.chevron, commonIconsDto.chevron) &&
        Objects.equals(this.notification, commonIconsDto.notification) &&
        Objects.equals(this.payment, commonIconsDto.payment);
  }

  @Override
  public int hashCode() {
    return Objects.hash(arrow, chevron, notification, payment);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommonIconsDto {\n");
    sb.append("    arrow: ").append(toIndentedString(arrow)).append("\n");
    sb.append("    chevron: ").append(toIndentedString(chevron)).append("\n");
    sb.append("    notification: ").append(toIndentedString(notification)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

