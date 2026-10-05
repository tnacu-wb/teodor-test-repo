package uk.co.whitbread.payapp.generated.models.company;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.payapp.generated.models.company.BookingAlertsDto;
import uk.co.whitbread.payapp.generated.models.company.BookingAllowancesDto;
import uk.co.whitbread.payapp.generated.models.company.CellCodeDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyDetailsDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyManagementQuestionsDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyStatusDto;
import uk.co.whitbread.payapp.generated.models.company.HotelCodeDto;
import uk.co.whitbread.payapp.generated.models.company.PaymentDetailsDto;
import uk.co.whitbread.payapp.generated.models.company.RatePlanDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CompanyDto
 */

@JsonTypeName("Company")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:38.523560+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CompanyDto {

  private @Nullable BookingAlertsDto bookingAlerts;

  private @Nullable BookingAllowancesDto bookingAllowances;

  @Valid
  private List<@Valid CellCodeDto> companyCellCodes = new ArrayList<>();

  private @Nullable CompanyDetailsDto companyDetails;

  private @Nullable CompanyManagementQuestionsDto companyManagementDetails;

  private @Nullable CompanyStatusDto companyStatus;

  private @Nullable PaymentDetailsDto paymentDetails;

  @Valid
  private List<@Valid HotelCodeDto> restrictedHotelCodes = new ArrayList<>();

  @Valid
  private List<@Valid RatePlanDto> restrictedRatePlans = new ArrayList<>();

  public CompanyDto bookingAlerts(BookingAlertsDto bookingAlerts) {
    this.bookingAlerts = bookingAlerts;
    return this;
  }

  /**
   * Get bookingAlerts
   * @return bookingAlerts
   */
  @Valid 
  @Schema(name = "bookingAlerts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAlerts")
  public BookingAlertsDto getBookingAlerts() {
    return bookingAlerts;
  }

  public void setBookingAlerts(BookingAlertsDto bookingAlerts) {
    this.bookingAlerts = bookingAlerts;
  }

  public CompanyDto bookingAllowances(BookingAllowancesDto bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    return this;
  }

  /**
   * Get bookingAllowances
   * @return bookingAllowances
   */
  @Valid 
  @Schema(name = "bookingAllowances", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingAllowances")
  public BookingAllowancesDto getBookingAllowances() {
    return bookingAllowances;
  }

  public void setBookingAllowances(BookingAllowancesDto bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
  }

  public CompanyDto companyCellCodes(List<@Valid CellCodeDto> companyCellCodes) {
    this.companyCellCodes = companyCellCodes;
    return this;
  }

  public CompanyDto addCompanyCellCodesItem(CellCodeDto companyCellCodesItem) {
    if (this.companyCellCodes == null) {
      this.companyCellCodes = new ArrayList<>();
    }
    this.companyCellCodes.add(companyCellCodesItem);
    return this;
  }

  /**
   * Get companyCellCodes
   * @return companyCellCodes
   */
  @Valid 
  @Schema(name = "companyCellCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyCellCodes")
  public List<@Valid CellCodeDto> getCompanyCellCodes() {
    return companyCellCodes;
  }

  public void setCompanyCellCodes(List<@Valid CellCodeDto> companyCellCodes) {
    this.companyCellCodes = companyCellCodes;
  }

  public CompanyDto companyDetails(CompanyDetailsDto companyDetails) {
    this.companyDetails = companyDetails;
    return this;
  }

  /**
   * Get companyDetails
   * @return companyDetails
   */
  @Valid 
  @Schema(name = "companyDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyDetails")
  public CompanyDetailsDto getCompanyDetails() {
    return companyDetails;
  }

  public void setCompanyDetails(CompanyDetailsDto companyDetails) {
    this.companyDetails = companyDetails;
  }

  public CompanyDto companyManagementDetails(CompanyManagementQuestionsDto companyManagementDetails) {
    this.companyManagementDetails = companyManagementDetails;
    return this;
  }

  /**
   * Get companyManagementDetails
   * @return companyManagementDetails
   */
  @Valid 
  @Schema(name = "companyManagementDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyManagementDetails")
  public CompanyManagementQuestionsDto getCompanyManagementDetails() {
    return companyManagementDetails;
  }

  public void setCompanyManagementDetails(CompanyManagementQuestionsDto companyManagementDetails) {
    this.companyManagementDetails = companyManagementDetails;
  }

  public CompanyDto companyStatus(CompanyStatusDto companyStatus) {
    this.companyStatus = companyStatus;
    return this;
  }

  /**
   * Get companyStatus
   * @return companyStatus
   */
  @Valid 
  @Schema(name = "companyStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyStatus")
  public CompanyStatusDto getCompanyStatus() {
    return companyStatus;
  }

  public void setCompanyStatus(CompanyStatusDto companyStatus) {
    this.companyStatus = companyStatus;
  }

  public CompanyDto paymentDetails(PaymentDetailsDto paymentDetails) {
    this.paymentDetails = paymentDetails;
    return this;
  }

  /**
   * Get paymentDetails
   * @return paymentDetails
   */
  @Valid 
  @Schema(name = "paymentDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentDetails")
  public PaymentDetailsDto getPaymentDetails() {
    return paymentDetails;
  }

  public void setPaymentDetails(PaymentDetailsDto paymentDetails) {
    this.paymentDetails = paymentDetails;
  }

  public CompanyDto restrictedHotelCodes(List<@Valid HotelCodeDto> restrictedHotelCodes) {
    this.restrictedHotelCodes = restrictedHotelCodes;
    return this;
  }

  public CompanyDto addRestrictedHotelCodesItem(HotelCodeDto restrictedHotelCodesItem) {
    if (this.restrictedHotelCodes == null) {
      this.restrictedHotelCodes = new ArrayList<>();
    }
    this.restrictedHotelCodes.add(restrictedHotelCodesItem);
    return this;
  }

  /**
   * Get restrictedHotelCodes
   * @return restrictedHotelCodes
   */
  @Valid 
  @Schema(name = "restrictedHotelCodes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictedHotelCodes")
  public List<@Valid HotelCodeDto> getRestrictedHotelCodes() {
    return restrictedHotelCodes;
  }

  public void setRestrictedHotelCodes(List<@Valid HotelCodeDto> restrictedHotelCodes) {
    this.restrictedHotelCodes = restrictedHotelCodes;
  }

  public CompanyDto restrictedRatePlans(List<@Valid RatePlanDto> restrictedRatePlans) {
    this.restrictedRatePlans = restrictedRatePlans;
    return this;
  }

  public CompanyDto addRestrictedRatePlansItem(RatePlanDto restrictedRatePlansItem) {
    if (this.restrictedRatePlans == null) {
      this.restrictedRatePlans = new ArrayList<>();
    }
    this.restrictedRatePlans.add(restrictedRatePlansItem);
    return this;
  }

  /**
   * Get restrictedRatePlans
   * @return restrictedRatePlans
   */
  @Valid 
  @Schema(name = "restrictedRatePlans", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restrictedRatePlans")
  public List<@Valid RatePlanDto> getRestrictedRatePlans() {
    return restrictedRatePlans;
  }

  public void setRestrictedRatePlans(List<@Valid RatePlanDto> restrictedRatePlans) {
    this.restrictedRatePlans = restrictedRatePlans;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompanyDto company = (CompanyDto) o;
    return Objects.equals(this.bookingAlerts, company.bookingAlerts) &&
        Objects.equals(this.bookingAllowances, company.bookingAllowances) &&
        Objects.equals(this.companyCellCodes, company.companyCellCodes) &&
        Objects.equals(this.companyDetails, company.companyDetails) &&
        Objects.equals(this.companyManagementDetails, company.companyManagementDetails) &&
        Objects.equals(this.companyStatus, company.companyStatus) &&
        Objects.equals(this.paymentDetails, company.paymentDetails) &&
        Objects.equals(this.restrictedHotelCodes, company.restrictedHotelCodes) &&
        Objects.equals(this.restrictedRatePlans, company.restrictedRatePlans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingAlerts, bookingAllowances, companyCellCodes, companyDetails, companyManagementDetails, companyStatus, paymentDetails, restrictedHotelCodes, restrictedRatePlans);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CompanyDto {\n");
    sb.append("    bookingAlerts: ").append(toIndentedString(bookingAlerts)).append("\n");
    sb.append("    bookingAllowances: ").append(toIndentedString(bookingAllowances)).append("\n");
    sb.append("    companyCellCodes: ").append(toIndentedString(companyCellCodes)).append("\n");
    sb.append("    companyDetails: ").append(toIndentedString(companyDetails)).append("\n");
    sb.append("    companyManagementDetails: ").append(toIndentedString(companyManagementDetails)).append("\n");
    sb.append("    companyStatus: ").append(toIndentedString(companyStatus)).append("\n");
    sb.append("    paymentDetails: ").append(toIndentedString(paymentDetails)).append("\n");
    sb.append("    restrictedHotelCodes: ").append(toIndentedString(restrictedHotelCodes)).append("\n");
    sb.append("    restrictedRatePlans: ").append(toIndentedString(restrictedRatePlans)).append("\n");
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

