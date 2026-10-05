package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InfoMessagesDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InfoMessagesDto {

  private @Nullable Integer messageOrder;

  private @Nullable String messageSubtitle;

  private @Nullable String messageTitle;

  private @Nullable String messageType;

  public InfoMessagesDto messageOrder(Integer messageOrder) {
    this.messageOrder = messageOrder;
    return this;
  }

  /**
   * Get messageOrder
   * @return messageOrder
   */
  
  @Schema(name = "messageOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messageOrder")
  public Integer getMessageOrder() {
    return messageOrder;
  }

  public void setMessageOrder(Integer messageOrder) {
    this.messageOrder = messageOrder;
  }

  public InfoMessagesDto messageSubtitle(String messageSubtitle) {
    this.messageSubtitle = messageSubtitle;
    return this;
  }

  /**
   * Get messageSubtitle
   * @return messageSubtitle
   */
  
  @Schema(name = "messageSubtitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messageSubtitle")
  public String getMessageSubtitle() {
    return messageSubtitle;
  }

  public void setMessageSubtitle(String messageSubtitle) {
    this.messageSubtitle = messageSubtitle;
  }

  public InfoMessagesDto messageTitle(String messageTitle) {
    this.messageTitle = messageTitle;
    return this;
  }

  /**
   * Get messageTitle
   * @return messageTitle
   */
  
  @Schema(name = "messageTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messageTitle")
  public String getMessageTitle() {
    return messageTitle;
  }

  public void setMessageTitle(String messageTitle) {
    this.messageTitle = messageTitle;
  }

  public InfoMessagesDto messageType(String messageType) {
    this.messageType = messageType;
    return this;
  }

  /**
   * Get messageType
   * @return messageType
   */
  
  @Schema(name = "messageType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messageType")
  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InfoMessagesDto infoMessagesDto = (InfoMessagesDto) o;
    return Objects.equals(this.messageOrder, infoMessagesDto.messageOrder) &&
        Objects.equals(this.messageSubtitle, infoMessagesDto.messageSubtitle) &&
        Objects.equals(this.messageTitle, infoMessagesDto.messageTitle) &&
        Objects.equals(this.messageType, infoMessagesDto.messageType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageOrder, messageSubtitle, messageTitle, messageType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InfoMessagesDto {\n");
    sb.append("    messageOrder: ").append(toIndentedString(messageOrder)).append("\n");
    sb.append("    messageSubtitle: ").append(toIndentedString(messageSubtitle)).append("\n");
    sb.append("    messageTitle: ").append(toIndentedString(messageTitle)).append("\n");
    sb.append("    messageType: ").append(toIndentedString(messageType)).append("\n");
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

