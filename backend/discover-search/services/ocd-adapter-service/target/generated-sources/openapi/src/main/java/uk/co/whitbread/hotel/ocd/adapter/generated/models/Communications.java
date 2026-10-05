package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Email;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Phone;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Url;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * The contact information for the property.
 */

@Schema(name = "Communications", description = "The contact information for the property.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Communications {

  @Valid
  private List<@Valid Phone> phones = new ArrayList<>();

  @Valid
  private List<@Valid Email> emails = new ArrayList<>();

  @Valid
  private List<@Valid Url> urls = new ArrayList<>();

  public Communications phones(List<@Valid Phone> phones) {
    this.phones = phones;
    return this;
  }

  public Communications addPhonesItem(Phone phonesItem) {
    if (this.phones == null) {
      this.phones = new ArrayList<>();
    }
    this.phones.add(phonesItem);
    return this;
  }

  /**
   * List of phone numbers for the property.
   * @return phones
   */
  @Valid @Size(min = 1) 
  @Schema(name = "phones", description = "List of phone numbers for the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phones")
  public List<@Valid Phone> getPhones() {
    return phones;
  }

  public void setPhones(List<@Valid Phone> phones) {
    this.phones = phones;
  }

  public Communications emails(List<@Valid Email> emails) {
    this.emails = emails;
    return this;
  }

  public Communications addEmailsItem(Email emailsItem) {
    if (this.emails == null) {
      this.emails = new ArrayList<>();
    }
    this.emails.add(emailsItem);
    return this;
  }

  /**
   * List of emails addresses for the property.
   * @return emails
   */
  @Valid 
  @Schema(name = "emails", description = "List of emails addresses for the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("emails")
  public List<@Valid Email> getEmails() {
    return emails;
  }

  public void setEmails(List<@Valid Email> emails) {
    this.emails = emails;
  }

  public Communications urls(List<@Valid Url> urls) {
    this.urls = urls;
    return this;
  }

  public Communications addUrlsItem(Url urlsItem) {
    if (this.urls == null) {
      this.urls = new ArrayList<>();
    }
    this.urls.add(urlsItem);
    return this;
  }

  /**
   * List of urls for the property.
   * @return urls
   */
  @Valid 
  @Schema(name = "urls", description = "List of urls for the property.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("urls")
  public List<@Valid Url> getUrls() {
    return urls;
  }

  public void setUrls(List<@Valid Url> urls) {
    this.urls = urls;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Communications communications = (Communications) o;
    return Objects.equals(this.phones, communications.phones) &&
        Objects.equals(this.emails, communications.emails) &&
        Objects.equals(this.urls, communications.urls);
  }

  @Override
  public int hashCode() {
    return Objects.hash(phones, emails, urls);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Communications {\n");
    sb.append("    phones: ").append(toIndentedString(phones)).append("\n");
    sb.append("    emails: ").append(toIndentedString(emails)).append("\n");
    sb.append("    urls: ").append(toIndentedString(urls)).append("\n");
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

