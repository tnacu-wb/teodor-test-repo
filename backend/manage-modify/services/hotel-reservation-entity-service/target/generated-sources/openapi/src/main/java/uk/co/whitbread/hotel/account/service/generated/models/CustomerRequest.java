package uk.co.whitbread.hotel.account.service.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.account.service.generated.models.BaseContact;
import uk.co.whitbread.hotel.account.service.generated.models.BookingPreference;
import uk.co.whitbread.hotel.account.service.generated.models.ContactDetail;
import uk.co.whitbread.hotel.account.service.generated.models.MarketingPreference;
import uk.co.whitbread.hotel.account.service.generated.models.PaymentPreference;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CustomerRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:35.247020+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CustomerRequest {

  @Valid
  private List<@Valid BaseContact> additionalGuests = new ArrayList<>();

  private @Nullable BookingPreference bookingPreference;

  private @Nullable String companyId;

  @Deprecated
  private @Nullable String companyName;

  private @Nullable ContactDetail contactDetail;

  private @Nullable String guestHistoryNumber;

  private @Nullable Long guestId;

  private @Nullable MarketingPreference marketingPreference;

  private @Nullable String newPassword;

  private @Nullable String password;

  private @Nullable PaymentPreference paymentPreference;

  public CustomerRequest additionalGuests(List<@Valid BaseContact> additionalGuests) {
    this.additionalGuests = additionalGuests;
    return this;
  }

  public CustomerRequest addAdditionalGuestsItem(BaseContact additionalGuestsItem) {
    if (this.additionalGuests == null) {
      this.additionalGuests = new ArrayList<>();
    }
    this.additionalGuests.add(additionalGuestsItem);
    return this;
  }

  /**
   * Get additionalGuests
   * @return additionalGuests
   */
  @Valid 
  @Schema(name = "additionalGuests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalGuests")
  public List<@Valid BaseContact> getAdditionalGuests() {
    return additionalGuests;
  }

  public void setAdditionalGuests(List<@Valid BaseContact> additionalGuests) {
    this.additionalGuests = additionalGuests;
  }

  public CustomerRequest bookingPreference(BookingPreference bookingPreference) {
    this.bookingPreference = bookingPreference;
    return this;
  }

  /**
   * Get bookingPreference
   * @return bookingPreference
   */
  @Valid 
  @Schema(name = "bookingPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingPreference")
  public BookingPreference getBookingPreference() {
    return bookingPreference;
  }

  public void setBookingPreference(BookingPreference bookingPreference) {
    this.bookingPreference = bookingPreference;
  }

  public CustomerRequest companyId(String companyId) {
    this.companyId = companyId;
    return this;
  }

  /**
   * Get companyId
   * @return companyId
   */
  
  @Schema(name = "companyId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyId")
  public String getCompanyId() {
    return companyId;
  }

  public void setCompanyId(String companyId) {
    this.companyId = companyId;
  }

  public CustomerRequest companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   * @deprecated
   */
  
  @Schema(name = "companyName", deprecated = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  @Deprecated
  public String getCompanyName() {
    return companyName;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public CustomerRequest contactDetail(ContactDetail contactDetail) {
    this.contactDetail = contactDetail;
    return this;
  }

  /**
   * Get contactDetail
   * @return contactDetail
   */
  @Valid 
  @Schema(name = "contactDetail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contactDetail")
  public ContactDetail getContactDetail() {
    return contactDetail;
  }

  public void setContactDetail(ContactDetail contactDetail) {
    this.contactDetail = contactDetail;
  }

  public CustomerRequest guestHistoryNumber(String guestHistoryNumber) {
    this.guestHistoryNumber = guestHistoryNumber;
    return this;
  }

  /**
   * Get guestHistoryNumber
   * @return guestHistoryNumber
   */
  
  @Schema(name = "guestHistoryNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestHistoryNumber")
  public String getGuestHistoryNumber() {
    return guestHistoryNumber;
  }

  public void setGuestHistoryNumber(String guestHistoryNumber) {
    this.guestHistoryNumber = guestHistoryNumber;
  }

  public CustomerRequest guestId(Long guestId) {
    this.guestId = guestId;
    return this;
  }

  /**
   * Get guestId
   * @return guestId
   */
  
  @Schema(name = "guestId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestId")
  public Long getGuestId() {
    return guestId;
  }

  public void setGuestId(Long guestId) {
    this.guestId = guestId;
  }

  public CustomerRequest marketingPreference(MarketingPreference marketingPreference) {
    this.marketingPreference = marketingPreference;
    return this;
  }

  /**
   * Get marketingPreference
   * @return marketingPreference
   */
  @Valid 
  @Schema(name = "marketingPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("marketingPreference")
  public MarketingPreference getMarketingPreference() {
    return marketingPreference;
  }

  public void setMarketingPreference(MarketingPreference marketingPreference) {
    this.marketingPreference = marketingPreference;
  }

  public CustomerRequest newPassword(String newPassword) {
    this.newPassword = newPassword;
    return this;
  }

  /**
   * Get newPassword
   * @return newPassword
   */
  
  @Schema(name = "newPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newPassword")
  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public CustomerRequest password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
   */
  
  @Schema(name = "password", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public CustomerRequest paymentPreference(PaymentPreference paymentPreference) {
    this.paymentPreference = paymentPreference;
    return this;
  }

  /**
   * Get paymentPreference
   * @return paymentPreference
   */
  @Valid 
  @Schema(name = "paymentPreference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentPreference")
  public PaymentPreference getPaymentPreference() {
    return paymentPreference;
  }

  public void setPaymentPreference(PaymentPreference paymentPreference) {
    this.paymentPreference = paymentPreference;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomerRequest customerRequest = (CustomerRequest) o;
    return Objects.equals(this.additionalGuests, customerRequest.additionalGuests) &&
        Objects.equals(this.bookingPreference, customerRequest.bookingPreference) &&
        Objects.equals(this.companyId, customerRequest.companyId) &&
        Objects.equals(this.companyName, customerRequest.companyName) &&
        Objects.equals(this.contactDetail, customerRequest.contactDetail) &&
        Objects.equals(this.guestHistoryNumber, customerRequest.guestHistoryNumber) &&
        Objects.equals(this.guestId, customerRequest.guestId) &&
        Objects.equals(this.marketingPreference, customerRequest.marketingPreference) &&
        Objects.equals(this.newPassword, customerRequest.newPassword) &&
        Objects.equals(this.password, customerRequest.password) &&
        Objects.equals(this.paymentPreference, customerRequest.paymentPreference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalGuests, bookingPreference, companyId, companyName, contactDetail, guestHistoryNumber, guestId, marketingPreference, newPassword, password, paymentPreference);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerRequest {\n");
    sb.append("    additionalGuests: ").append(toIndentedString(additionalGuests)).append("\n");
    sb.append("    bookingPreference: ").append(toIndentedString(bookingPreference)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    contactDetail: ").append(toIndentedString(contactDetail)).append("\n");
    sb.append("    guestHistoryNumber: ").append(toIndentedString(guestHistoryNumber)).append("\n");
    sb.append("    guestId: ").append(toIndentedString(guestId)).append("\n");
    sb.append("    marketingPreference: ").append(toIndentedString(marketingPreference)).append("\n");
    sb.append("    newPassword: ").append(toIndentedString(newPassword)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    paymentPreference: ").append(toIndentedString(paymentPreference)).append("\n");
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

