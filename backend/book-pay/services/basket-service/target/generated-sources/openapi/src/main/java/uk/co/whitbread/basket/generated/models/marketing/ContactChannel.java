package uk.co.whitbread.basket.generated.models.marketing;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ContactChannel
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:09.701357+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class ContactChannel {

  private @Nullable String contactChannelId;

  /**
   * Gets or Sets contactChannelType
   */
  public enum ContactChannelTypeEnum {
    EMAIL("Email"),
    
    TELEPHONE("Telephone");

    private String value;

    ContactChannelTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ContactChannelTypeEnum fromValue(String value) {
      for (ContactChannelTypeEnum b : ContactChannelTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private ContactChannelTypeEnum contactChannelType;

  private @Nullable String contactChannelValue;

  public ContactChannel() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ContactChannel(ContactChannelTypeEnum contactChannelType) {
    this.contactChannelType = contactChannelType;
  }

  public ContactChannel contactChannelId(String contactChannelId) {
    this.contactChannelId = contactChannelId;
    return this;
  }

  /**
   * Get contactChannelId
   * @return contactChannelId
   */
  
  @Schema(name = "contactChannelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactChannelId")
  public String getContactChannelId() {
    return contactChannelId;
  }

  public void setContactChannelId(String contactChannelId) {
    this.contactChannelId = contactChannelId;
  }

  public ContactChannel contactChannelType(ContactChannelTypeEnum contactChannelType) {
    this.contactChannelType = contactChannelType;
    return this;
  }

  /**
   * Get contactChannelType
   * @return contactChannelType
   */
  @NotNull 
  @Schema(name = "contactChannelType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("contactChannelType")
  public ContactChannelTypeEnum getContactChannelType() {
    return contactChannelType;
  }

  public void setContactChannelType(ContactChannelTypeEnum contactChannelType) {
    this.contactChannelType = contactChannelType;
  }

  public ContactChannel contactChannelValue(String contactChannelValue) {
    this.contactChannelValue = contactChannelValue;
    return this;
  }

  /**
   * Get contactChannelValue
   * @return contactChannelValue
   */
  
  @Schema(name = "contactChannelValue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactChannelValue")
  public String getContactChannelValue() {
    return contactChannelValue;
  }

  public void setContactChannelValue(String contactChannelValue) {
    this.contactChannelValue = contactChannelValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ContactChannel contactChannel = (ContactChannel) o;
    return Objects.equals(this.contactChannelId, contactChannel.contactChannelId) &&
        Objects.equals(this.contactChannelType, contactChannel.contactChannelType) &&
        Objects.equals(this.contactChannelValue, contactChannel.contactChannelValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contactChannelId, contactChannelType, contactChannelValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ContactChannel {\n");
    sb.append("    contactChannelId: ").append(toIndentedString(contactChannelId)).append("\n");
    sb.append("    contactChannelType: ").append(toIndentedString(contactChannelType)).append("\n");
    sb.append("    contactChannelValue: ").append(toIndentedString(contactChannelValue)).append("\n");
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

