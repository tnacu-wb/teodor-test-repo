package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BookingDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BusinessAccountDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CompanyQuestionAndAnswerDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PaymentRequestDto {

  private @Nullable BookingDto booking;

  @Valid
  private List<String> bookingNotes = new ArrayList<>();

  private @Nullable BusinessAccountDto businessAccount;

  private @Nullable CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails;

  private @Nullable Boolean isCiol;

  private @Nullable PaymentDto payment;

  private @Nullable String requestId;

  @Valid
  private List<String> specialRequests = new ArrayList<>();

  private @Nullable String tmpBasketRef;

  public PaymentRequestDto booking(BookingDto booking) {
    this.booking = booking;
    return this;
  }

  /**
   * Get booking
   * @return booking
   */
  @Valid 
  @Schema(name = "booking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("booking")
  public BookingDto getBooking() {
    return booking;
  }

  public void setBooking(BookingDto booking) {
    this.booking = booking;
  }

  public PaymentRequestDto bookingNotes(List<String> bookingNotes) {
    this.bookingNotes = bookingNotes;
    return this;
  }

  public PaymentRequestDto addBookingNotesItem(String bookingNotesItem) {
    if (this.bookingNotes == null) {
      this.bookingNotes = new ArrayList<>();
    }
    this.bookingNotes.add(bookingNotesItem);
    return this;
  }

  /**
   * Get bookingNotes
   * @return bookingNotes
   */
  
  @Schema(name = "bookingNotes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingNotes")
  public List<String> getBookingNotes() {
    return bookingNotes;
  }

  public void setBookingNotes(List<String> bookingNotes) {
    this.bookingNotes = bookingNotes;
  }

  public PaymentRequestDto businessAccount(BusinessAccountDto businessAccount) {
    this.businessAccount = businessAccount;
    return this;
  }

  /**
   * Get businessAccount
   * @return businessAccount
   */
  @Valid 
  @Schema(name = "businessAccount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessAccount")
  public BusinessAccountDto getBusinessAccount() {
    return businessAccount;
  }

  public void setBusinessAccount(BusinessAccountDto businessAccount) {
    this.businessAccount = businessAccount;
  }

  public PaymentRequestDto companyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails) {
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
    return this;
  }

  /**
   * Get companyQuestionAndAnswerDetails
   * @return companyQuestionAndAnswerDetails
   */
  @Valid 
  @Schema(name = "companyQuestionAndAnswerDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyQuestionAndAnswerDetails")
  public CompanyQuestionAndAnswerDetailsDto getCompanyQuestionAndAnswerDetails() {
    return companyQuestionAndAnswerDetails;
  }

  public void setCompanyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetails) {
    this.companyQuestionAndAnswerDetails = companyQuestionAndAnswerDetails;
  }

  public PaymentRequestDto isCiol(Boolean isCiol) {
    this.isCiol = isCiol;
    return this;
  }

  /**
   * Get isCiol
   * @return isCiol
   */
  
  @Schema(name = "isCiol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isCiol")
  public Boolean getIsCiol() {
    return isCiol;
  }

  public void setIsCiol(Boolean isCiol) {
    this.isCiol = isCiol;
  }

  public PaymentRequestDto payment(PaymentDto payment) {
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

  public PaymentRequestDto requestId(String requestId) {
    this.requestId = requestId;
    return this;
  }

  /**
   * Get requestId
   * @return requestId
   */
  
  @Schema(name = "requestId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("requestId")
  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public PaymentRequestDto specialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
    return this;
  }

  public PaymentRequestDto addSpecialRequestsItem(String specialRequestsItem) {
    if (this.specialRequests == null) {
      this.specialRequests = new ArrayList<>();
    }
    this.specialRequests.add(specialRequestsItem);
    return this;
  }

  /**
   * Get specialRequests
   * @return specialRequests
   */
  
  @Schema(name = "specialRequests", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("specialRequests")
  public List<String> getSpecialRequests() {
    return specialRequests;
  }

  public void setSpecialRequests(List<String> specialRequests) {
    this.specialRequests = specialRequests;
  }

  public PaymentRequestDto tmpBasketRef(String tmpBasketRef) {
    this.tmpBasketRef = tmpBasketRef;
    return this;
  }

  /**
   * Get tmpBasketRef
   * @return tmpBasketRef
   */
  
  @Schema(name = "tmpBasketRef", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tmpBasketRef")
  public String getTmpBasketRef() {
    return tmpBasketRef;
  }

  public void setTmpBasketRef(String tmpBasketRef) {
    this.tmpBasketRef = tmpBasketRef;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRequestDto paymentRequestDto = (PaymentRequestDto) o;
    return Objects.equals(this.booking, paymentRequestDto.booking) &&
        Objects.equals(this.bookingNotes, paymentRequestDto.bookingNotes) &&
        Objects.equals(this.businessAccount, paymentRequestDto.businessAccount) &&
        Objects.equals(this.companyQuestionAndAnswerDetails, paymentRequestDto.companyQuestionAndAnswerDetails) &&
        Objects.equals(this.isCiol, paymentRequestDto.isCiol) &&
        Objects.equals(this.payment, paymentRequestDto.payment) &&
        Objects.equals(this.requestId, paymentRequestDto.requestId) &&
        Objects.equals(this.specialRequests, paymentRequestDto.specialRequests) &&
        Objects.equals(this.tmpBasketRef, paymentRequestDto.tmpBasketRef);
  }

  @Override
  public int hashCode() {
    return Objects.hash(booking, bookingNotes, businessAccount, companyQuestionAndAnswerDetails, isCiol, payment, requestId, specialRequests, tmpBasketRef);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequestDto {\n");
    sb.append("    booking: ").append(toIndentedString(booking)).append("\n");
    sb.append("    bookingNotes: ").append(toIndentedString(bookingNotes)).append("\n");
    sb.append("    businessAccount: ").append(toIndentedString(businessAccount)).append("\n");
    sb.append("    companyQuestionAndAnswerDetails: ").append(toIndentedString(companyQuestionAndAnswerDetails)).append("\n");
    sb.append("    isCiol: ").append(toIndentedString(isCiol)).append("\n");
    sb.append("    payment: ").append(toIndentedString(payment)).append("\n");
    sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
    sb.append("    specialRequests: ").append(toIndentedString(specialRequests)).append("\n");
    sb.append("    tmpBasketRef: ").append(toIndentedString(tmpBasketRef)).append("\n");
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

