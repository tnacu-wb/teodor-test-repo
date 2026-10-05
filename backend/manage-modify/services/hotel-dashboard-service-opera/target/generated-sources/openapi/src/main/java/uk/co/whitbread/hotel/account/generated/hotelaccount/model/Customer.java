package uk.co.whitbread.hotel.account.generated.hotelaccount.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BaseContact;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BookingPreference;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.Business;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.ContactDetail;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.PaymentPreference;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Customer
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:33.863804+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Customer {

  @Valid
  private List<@Valid BaseContact> additionalGuests = new ArrayList<>();

  private @Nullable BookingPreference bookingPreference;

  private @Nullable Business business;

  private @Nullable Boolean businessUse;

  private @Nullable String companyId;

  @Deprecated
  private @Nullable String companyName;

  private @Nullable ContactDetail contactDetail;

  private @Nullable String customerAccountId;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private @Nullable LocalDate guestHistoryCreation;

  @Deprecated
  private @Nullable String guestHistoryNumber;

  private @Nullable PaymentPreference paymentPreference;

  @Deprecated
  private @Nullable String sessionId;

  private @Nullable String tetheredGuid;

  private @Nullable Long totalStays;

  public Customer additionalGuests(List<@Valid BaseContact> additionalGuests) {
    this.additionalGuests = additionalGuests;
    return this;
  }

  public Customer addAdditionalGuestsItem(BaseContact additionalGuestsItem) {
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

  public Customer bookingPreference(BookingPreference bookingPreference) {
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

  public Customer business(Business business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  @Valid 
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public Business getBusiness() {
    return business;
  }

  public void setBusiness(Business business) {
    this.business = business;
  }

  public Customer businessUse(Boolean businessUse) {
    this.businessUse = businessUse;
    return this;
  }

  /**
   * Get businessUse
   * @return businessUse
   */
  
  @Schema(name = "businessUse", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessUse")
  public Boolean getBusinessUse() {
    return businessUse;
  }

  public void setBusinessUse(Boolean businessUse) {
    this.businessUse = businessUse;
  }

  public Customer companyId(String companyId) {
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

  public Customer companyName(String companyName) {
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

  public Customer contactDetail(ContactDetail contactDetail) {
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

  public Customer customerAccountId(String customerAccountId) {
    this.customerAccountId = customerAccountId;
    return this;
  }

  /**
   * Get customerAccountId
   * @return customerAccountId
   */
  
  @Schema(name = "customerAccountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerAccountId")
  public String getCustomerAccountId() {
    return customerAccountId;
  }

  public void setCustomerAccountId(String customerAccountId) {
    this.customerAccountId = customerAccountId;
  }

  public Customer guestHistoryCreation(LocalDate guestHistoryCreation) {
    this.guestHistoryCreation = guestHistoryCreation;
    return this;
  }

  /**
   * Get guestHistoryCreation
   * @return guestHistoryCreation
   */
  @Valid 
  @Schema(name = "guestHistoryCreation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestHistoryCreation")
  public LocalDate getGuestHistoryCreation() {
    return guestHistoryCreation;
  }

  public void setGuestHistoryCreation(LocalDate guestHistoryCreation) {
    this.guestHistoryCreation = guestHistoryCreation;
  }

  public Customer guestHistoryNumber(String guestHistoryNumber) {
    this.guestHistoryNumber = guestHistoryNumber;
    return this;
  }

  /**
   * Get guestHistoryNumber
   * @return guestHistoryNumber
   * @deprecated
   */
  
  @Schema(name = "guestHistoryNumber", deprecated = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestHistoryNumber")
  @Deprecated
  public String getGuestHistoryNumber() {
    return guestHistoryNumber;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public void setGuestHistoryNumber(String guestHistoryNumber) {
    this.guestHistoryNumber = guestHistoryNumber;
  }

  public Customer paymentPreference(PaymentPreference paymentPreference) {
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

  public Customer sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
   * @deprecated
   */
  
  @Schema(name = "sessionId", deprecated = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sessionId")
  @Deprecated
  public String getSessionId() {
    return sessionId;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public Customer tetheredGuid(String tetheredGuid) {
    this.tetheredGuid = tetheredGuid;
    return this;
  }

  /**
   * Get tetheredGuid
   * @return tetheredGuid
   */
  
  @Schema(name = "tetheredGuid", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tetheredGuid")
  public String getTetheredGuid() {
    return tetheredGuid;
  }

  public void setTetheredGuid(String tetheredGuid) {
    this.tetheredGuid = tetheredGuid;
  }

  public Customer totalStays(Long totalStays) {
    this.totalStays = totalStays;
    return this;
  }

  /**
   * Get totalStays
   * @return totalStays
   */
  
  @Schema(name = "totalStays", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalStays")
  public Long getTotalStays() {
    return totalStays;
  }

  public void setTotalStays(Long totalStays) {
    this.totalStays = totalStays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(this.additionalGuests, customer.additionalGuests) &&
        Objects.equals(this.bookingPreference, customer.bookingPreference) &&
        Objects.equals(this.business, customer.business) &&
        Objects.equals(this.businessUse, customer.businessUse) &&
        Objects.equals(this.companyId, customer.companyId) &&
        Objects.equals(this.companyName, customer.companyName) &&
        Objects.equals(this.contactDetail, customer.contactDetail) &&
        Objects.equals(this.customerAccountId, customer.customerAccountId) &&
        Objects.equals(this.guestHistoryCreation, customer.guestHistoryCreation) &&
        Objects.equals(this.guestHistoryNumber, customer.guestHistoryNumber) &&
        Objects.equals(this.paymentPreference, customer.paymentPreference) &&
        Objects.equals(this.sessionId, customer.sessionId) &&
        Objects.equals(this.tetheredGuid, customer.tetheredGuid) &&
        Objects.equals(this.totalStays, customer.totalStays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalGuests, bookingPreference, business, businessUse, companyId, companyName, contactDetail, customerAccountId, guestHistoryCreation, guestHistoryNumber, paymentPreference, sessionId, tetheredGuid, totalStays);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Customer {\n");
    sb.append("    additionalGuests: ").append(toIndentedString(additionalGuests)).append("\n");
    sb.append("    bookingPreference: ").append(toIndentedString(bookingPreference)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    businessUse: ").append(toIndentedString(businessUse)).append("\n");
    sb.append("    companyId: ").append(toIndentedString(companyId)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    contactDetail: ").append(toIndentedString(contactDetail)).append("\n");
    sb.append("    customerAccountId: ").append(toIndentedString(customerAccountId)).append("\n");
    sb.append("    guestHistoryCreation: ").append(toIndentedString(guestHistoryCreation)).append("\n");
    sb.append("    guestHistoryNumber: ").append(toIndentedString(guestHistoryNumber)).append("\n");
    sb.append("    paymentPreference: ").append(toIndentedString(paymentPreference)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    tetheredGuid: ").append(toIndentedString(tetheredGuid)).append("\n");
    sb.append("    totalStays: ").append(toIndentedString(totalStays)).append("\n");
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

