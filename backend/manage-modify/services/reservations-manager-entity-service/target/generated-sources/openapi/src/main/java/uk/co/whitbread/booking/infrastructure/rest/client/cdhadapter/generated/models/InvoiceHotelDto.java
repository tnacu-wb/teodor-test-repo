package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.WbCompanyDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * InvoiceHotelDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:45.778333+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class InvoiceHotelDto {

  private @Nullable String addressLine1;

  private @Nullable String addressLine2;

  private @Nullable String addressLine3;

  private @Nullable String addressLine4;

  private @Nullable String businessDate;

  private @Nullable String city;

  private @Nullable String countryCode;

  private @Nullable String hotelId;

  private @Nullable String name;

  private @Nullable String phoneNumber;

  private @Nullable String postalCode;

  private @Nullable WbCompanyDto wbCompany;

  public InvoiceHotelDto addressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
    return this;
  }

  /**
   * Get addressLine1
   * @return addressLine1
   */
  
  @Schema(name = "addressLine1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine1")
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public InvoiceHotelDto addressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
    return this;
  }

  /**
   * Get addressLine2
   * @return addressLine2
   */
  
  @Schema(name = "addressLine2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine2")
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public InvoiceHotelDto addressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
    return this;
  }

  /**
   * Get addressLine3
   * @return addressLine3
   */
  
  @Schema(name = "addressLine3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine3")
  public String getAddressLine3() {
    return addressLine3;
  }

  public void setAddressLine3(String addressLine3) {
    this.addressLine3 = addressLine3;
  }

  public InvoiceHotelDto addressLine4(String addressLine4) {
    this.addressLine4 = addressLine4;
    return this;
  }

  /**
   * Get addressLine4
   * @return addressLine4
   */
  
  @Schema(name = "addressLine4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressLine4")
  public String getAddressLine4() {
    return addressLine4;
  }

  public void setAddressLine4(String addressLine4) {
    this.addressLine4 = addressLine4;
  }

  public InvoiceHotelDto businessDate(String businessDate) {
    this.businessDate = businessDate;
    return this;
  }

  /**
   * Get businessDate
   * @return businessDate
   */
  
  @Schema(name = "businessDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessDate")
  public String getBusinessDate() {
    return businessDate;
  }

  public void setBusinessDate(String businessDate) {
    this.businessDate = businessDate;
  }

  public InvoiceHotelDto city(String city) {
    this.city = city;
    return this;
  }

  /**
   * Get city
   * @return city
   */
  
  @Schema(name = "city", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("city")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public InvoiceHotelDto countryCode(String countryCode) {
    this.countryCode = countryCode;
    return this;
  }

  /**
   * Get countryCode
   * @return countryCode
   */
  
  @Schema(name = "countryCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("countryCode")
  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public InvoiceHotelDto hotelId(String hotelId) {
    this.hotelId = hotelId;
    return this;
  }

  /**
   * Get hotelId
   * @return hotelId
   */
  
  @Schema(name = "hotelId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelId")
  public String getHotelId() {
    return hotelId;
  }

  public void setHotelId(String hotelId) {
    this.hotelId = hotelId;
  }

  public InvoiceHotelDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
   */
  
  @Schema(name = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public InvoiceHotelDto phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  
  @Schema(name = "phoneNumber", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public InvoiceHotelDto postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }

  /**
   * Get postalCode
   * @return postalCode
   */
  
  @Schema(name = "postalCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postalCode")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public InvoiceHotelDto wbCompany(WbCompanyDto wbCompany) {
    this.wbCompany = wbCompany;
    return this;
  }

  /**
   * Get wbCompany
   * @return wbCompany
   */
  @Valid 
  @Schema(name = "wbCompany", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("wbCompany")
  public WbCompanyDto getWbCompany() {
    return wbCompany;
  }

  public void setWbCompany(WbCompanyDto wbCompany) {
    this.wbCompany = wbCompany;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceHotelDto invoiceHotelDto = (InvoiceHotelDto) o;
    return Objects.equals(this.addressLine1, invoiceHotelDto.addressLine1) &&
        Objects.equals(this.addressLine2, invoiceHotelDto.addressLine2) &&
        Objects.equals(this.addressLine3, invoiceHotelDto.addressLine3) &&
        Objects.equals(this.addressLine4, invoiceHotelDto.addressLine4) &&
        Objects.equals(this.businessDate, invoiceHotelDto.businessDate) &&
        Objects.equals(this.city, invoiceHotelDto.city) &&
        Objects.equals(this.countryCode, invoiceHotelDto.countryCode) &&
        Objects.equals(this.hotelId, invoiceHotelDto.hotelId) &&
        Objects.equals(this.name, invoiceHotelDto.name) &&
        Objects.equals(this.phoneNumber, invoiceHotelDto.phoneNumber) &&
        Objects.equals(this.postalCode, invoiceHotelDto.postalCode) &&
        Objects.equals(this.wbCompany, invoiceHotelDto.wbCompany);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressLine1, addressLine2, addressLine3, addressLine4, businessDate, city, countryCode, hotelId, name, phoneNumber, postalCode, wbCompany);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceHotelDto {\n");
    sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
    sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
    sb.append("    addressLine3: ").append(toIndentedString(addressLine3)).append("\n");
    sb.append("    addressLine4: ").append(toIndentedString(addressLine4)).append("\n");
    sb.append("    businessDate: ").append(toIndentedString(businessDate)).append("\n");
    sb.append("    city: ").append(toIndentedString(city)).append("\n");
    sb.append("    countryCode: ").append(toIndentedString(countryCode)).append("\n");
    sb.append("    hotelId: ").append(toIndentedString(hotelId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    wbCompany: ").append(toIndentedString(wbCompany)).append("\n");
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

