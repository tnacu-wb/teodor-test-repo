package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.MessageDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InfoMessageDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InfoMessageDto {

  @Valid
  private List<@Valid MessageDto> messages = new ArrayList<>();

  private @Nullable String rate;

  private @Nullable String rateCategory;

  private @Nullable String rateDisplaySet;

  public InfoMessageDto messages(List<@Valid MessageDto> messages) {
    this.messages = messages;
    return this;
  }

  public InfoMessageDto addMessagesItem(MessageDto messagesItem) {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    this.messages.add(messagesItem);
    return this;
  }

  /**
   * Get messages
   * @return messages
   */
  @Valid 
  @Schema(name = "messages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("messages")
  public List<@Valid MessageDto> getMessages() {
    return messages;
  }

  public void setMessages(List<@Valid MessageDto> messages) {
    this.messages = messages;
  }

  public InfoMessageDto rate(String rate) {
    this.rate = rate;
    return this;
  }

  /**
   * Get rate
   * @return rate
   */
  
  @Schema(name = "rate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rate")
  public String getRate() {
    return rate;
  }

  public void setRate(String rate) {
    this.rate = rate;
  }

  public InfoMessageDto rateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
    return this;
  }

  /**
   * Get rateCategory
   * @return rateCategory
   */
  
  @Schema(name = "rateCategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateCategory")
  public String getRateCategory() {
    return rateCategory;
  }

  public void setRateCategory(String rateCategory) {
    this.rateCategory = rateCategory;
  }

  public InfoMessageDto rateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
    return this;
  }

  /**
   * Get rateDisplaySet
   * @return rateDisplaySet
   */
  
  @Schema(name = "rateDisplaySet", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateDisplaySet")
  public String getRateDisplaySet() {
    return rateDisplaySet;
  }

  public void setRateDisplaySet(String rateDisplaySet) {
    this.rateDisplaySet = rateDisplaySet;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InfoMessageDto infoMessageDto = (InfoMessageDto) o;
    return Objects.equals(this.messages, infoMessageDto.messages) &&
        Objects.equals(this.rate, infoMessageDto.rate) &&
        Objects.equals(this.rateCategory, infoMessageDto.rateCategory) &&
        Objects.equals(this.rateDisplaySet, infoMessageDto.rateDisplaySet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messages, rate, rateCategory, rateDisplaySet);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InfoMessageDto {\n");
    sb.append("    messages: ").append(toIndentedString(messages)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    rateCategory: ").append(toIndentedString(rateCategory)).append("\n");
    sb.append("    rateDisplaySet: ").append(toIndentedString(rateDisplaySet)).append("\n");
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

