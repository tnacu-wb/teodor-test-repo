package uk.co.whitbread.hotel.entity.service.generated.models.hotel;

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
 * GroupBookingRequestDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:33.749132+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class GroupBookingRequestDto {

  private @Nullable Integer accessibleDouble;

  private @Nullable Integer accessibleSingle;

  private @Nullable Integer accessibleTwin;

  private @Nullable String additionalInformation;

  private String arrivalDate;

  private String bookerType;

  private @Nullable String companyName;

  private String departureDate;

  private @Nullable Integer doubleOccupancy;

  private String emailAddress;

  private @Nullable Integer familyOf21A1C;

  private @Nullable Integer familyOf31A2C;

  private @Nullable Integer familyOf32A1C;

  private @Nullable Integer familyOf42A2C;

  private String firstName;

  private @Nullable String hotelBrand;

  private @Nullable String hotelName;

  private @Nullable Boolean isAccessibleRoom;

  private @Nullable Boolean isPackageTypeBf;

  private @Nullable Boolean isPackageTypeMealDeal;

  private @Nullable Boolean isSchoolOrYouth;

  private @Nullable Boolean isTravellingWithChild;

  private @Nullable String language;

  private String lastName;

  private String phoneNumber;

  private String purposeOfStay;

  private String reasonForVisit;

  private @Nullable String reasonForVisitOther;

  private @Nullable Integer singleOccupancy;

  private String title;

  private @Nullable Integer twinRooms;

  public GroupBookingRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GroupBookingRequestDto(String arrivalDate, String bookerType, String departureDate, String emailAddress, String firstName, String lastName, String phoneNumber, String purposeOfStay, String reasonForVisit, String title) {
    this.arrivalDate = arrivalDate;
    this.bookerType = bookerType;
    this.departureDate = departureDate;
    this.emailAddress = emailAddress;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.purposeOfStay = purposeOfStay;
    this.reasonForVisit = reasonForVisit;
    this.title = title;
  }

  public GroupBookingRequestDto accessibleDouble(Integer accessibleDouble) {
    this.accessibleDouble = accessibleDouble;
    return this;
  }

  /**
   * Get accessibleDouble
   * @return accessibleDouble
   */
  
  @Schema(name = "accessibleDouble", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessibleDouble")
  public Integer getAccessibleDouble() {
    return accessibleDouble;
  }

  public void setAccessibleDouble(Integer accessibleDouble) {
    this.accessibleDouble = accessibleDouble;
  }

  public GroupBookingRequestDto accessibleSingle(Integer accessibleSingle) {
    this.accessibleSingle = accessibleSingle;
    return this;
  }

  /**
   * Get accessibleSingle
   * @return accessibleSingle
   */
  
  @Schema(name = "accessibleSingle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessibleSingle")
  public Integer getAccessibleSingle() {
    return accessibleSingle;
  }

  public void setAccessibleSingle(Integer accessibleSingle) {
    this.accessibleSingle = accessibleSingle;
  }

  public GroupBookingRequestDto accessibleTwin(Integer accessibleTwin) {
    this.accessibleTwin = accessibleTwin;
    return this;
  }

  /**
   * Get accessibleTwin
   * @return accessibleTwin
   */
  
  @Schema(name = "accessibleTwin", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accessibleTwin")
  public Integer getAccessibleTwin() {
    return accessibleTwin;
  }

  public void setAccessibleTwin(Integer accessibleTwin) {
    this.accessibleTwin = accessibleTwin;
  }

  public GroupBookingRequestDto additionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
    return this;
  }

  /**
   * Get additionalInformation
   * @return additionalInformation
   */
  
  @Schema(name = "additionalInformation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("additionalInformation")
  public String getAdditionalInformation() {
    return additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  public GroupBookingRequestDto arrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
    return this;
  }

  /**
   * Get arrivalDate
   * @return arrivalDate
   */
  @NotNull 
  @Schema(name = "arrivalDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("arrivalDate")
  public String getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(String arrivalDate) {
    this.arrivalDate = arrivalDate;
  }

  public GroupBookingRequestDto bookerType(String bookerType) {
    this.bookerType = bookerType;
    return this;
  }

  /**
   * Get bookerType
   * @return bookerType
   */
  @NotNull 
  @Schema(name = "bookerType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("bookerType")
  public String getBookerType() {
    return bookerType;
  }

  public void setBookerType(String bookerType) {
    this.bookerType = bookerType;
  }

  public GroupBookingRequestDto companyName(String companyName) {
    this.companyName = companyName;
    return this;
  }

  /**
   * Get companyName
   * @return companyName
   */
  
  @Schema(name = "companyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("companyName")
  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public GroupBookingRequestDto departureDate(String departureDate) {
    this.departureDate = departureDate;
    return this;
  }

  /**
   * Get departureDate
   * @return departureDate
   */
  @NotNull 
  @Schema(name = "departureDate", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("departureDate")
  public String getDepartureDate() {
    return departureDate;
  }

  public void setDepartureDate(String departureDate) {
    this.departureDate = departureDate;
  }

  public GroupBookingRequestDto doubleOccupancy(Integer doubleOccupancy) {
    this.doubleOccupancy = doubleOccupancy;
    return this;
  }

  /**
   * Get doubleOccupancy
   * @return doubleOccupancy
   */
  
  @Schema(name = "doubleOccupancy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("doubleOccupancy")
  public Integer getDoubleOccupancy() {
    return doubleOccupancy;
  }

  public void setDoubleOccupancy(Integer doubleOccupancy) {
    this.doubleOccupancy = doubleOccupancy;
  }

  public GroupBookingRequestDto emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
   */
  @NotNull 
  @Schema(name = "emailAddress", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("emailAddress")
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public GroupBookingRequestDto familyOf21A1C(Integer familyOf21A1C) {
    this.familyOf21A1C = familyOf21A1C;
    return this;
  }

  /**
   * Get familyOf21A1C
   * @return familyOf21A1C
   */
  
  @Schema(name = "familyOf21A1C", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("familyOf21A1C")
  public Integer getFamilyOf21A1C() {
    return familyOf21A1C;
  }

  public void setFamilyOf21A1C(Integer familyOf21A1C) {
    this.familyOf21A1C = familyOf21A1C;
  }

  public GroupBookingRequestDto familyOf31A2C(Integer familyOf31A2C) {
    this.familyOf31A2C = familyOf31A2C;
    return this;
  }

  /**
   * Get familyOf31A2C
   * @return familyOf31A2C
   */
  
  @Schema(name = "familyOf31A2C", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("familyOf31A2C")
  public Integer getFamilyOf31A2C() {
    return familyOf31A2C;
  }

  public void setFamilyOf31A2C(Integer familyOf31A2C) {
    this.familyOf31A2C = familyOf31A2C;
  }

  public GroupBookingRequestDto familyOf32A1C(Integer familyOf32A1C) {
    this.familyOf32A1C = familyOf32A1C;
    return this;
  }

  /**
   * Get familyOf32A1C
   * @return familyOf32A1C
   */
  
  @Schema(name = "familyOf32A1C", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("familyOf32A1C")
  public Integer getFamilyOf32A1C() {
    return familyOf32A1C;
  }

  public void setFamilyOf32A1C(Integer familyOf32A1C) {
    this.familyOf32A1C = familyOf32A1C;
  }

  public GroupBookingRequestDto familyOf42A2C(Integer familyOf42A2C) {
    this.familyOf42A2C = familyOf42A2C;
    return this;
  }

  /**
   * Get familyOf42A2C
   * @return familyOf42A2C
   */
  
  @Schema(name = "familyOf42A2C", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("familyOf42A2C")
  public Integer getFamilyOf42A2C() {
    return familyOf42A2C;
  }

  public void setFamilyOf42A2C(Integer familyOf42A2C) {
    this.familyOf42A2C = familyOf42A2C;
  }

  public GroupBookingRequestDto firstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  /**
   * Get firstName
   * @return firstName
   */
  @NotNull 
  @Schema(name = "firstName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public GroupBookingRequestDto hotelBrand(String hotelBrand) {
    this.hotelBrand = hotelBrand;
    return this;
  }

  /**
   * Get hotelBrand
   * @return hotelBrand
   */
  
  @Schema(name = "hotelBrand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelBrand")
  public String getHotelBrand() {
    return hotelBrand;
  }

  public void setHotelBrand(String hotelBrand) {
    this.hotelBrand = hotelBrand;
  }

  public GroupBookingRequestDto hotelName(String hotelName) {
    this.hotelName = hotelName;
    return this;
  }

  /**
   * Get hotelName
   * @return hotelName
   */
  
  @Schema(name = "hotelName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("hotelName")
  public String getHotelName() {
    return hotelName;
  }

  public void setHotelName(String hotelName) {
    this.hotelName = hotelName;
  }

  public GroupBookingRequestDto isAccessibleRoom(Boolean isAccessibleRoom) {
    this.isAccessibleRoom = isAccessibleRoom;
    return this;
  }

  /**
   * Get isAccessibleRoom
   * @return isAccessibleRoom
   */
  
  @Schema(name = "isAccessibleRoom", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isAccessibleRoom")
  public Boolean getIsAccessibleRoom() {
    return isAccessibleRoom;
  }

  public void setIsAccessibleRoom(Boolean isAccessibleRoom) {
    this.isAccessibleRoom = isAccessibleRoom;
  }

  public GroupBookingRequestDto isPackageTypeBf(Boolean isPackageTypeBf) {
    this.isPackageTypeBf = isPackageTypeBf;
    return this;
  }

  /**
   * Get isPackageTypeBf
   * @return isPackageTypeBf
   */
  
  @Schema(name = "isPackageTypeBf", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isPackageTypeBf")
  public Boolean getIsPackageTypeBf() {
    return isPackageTypeBf;
  }

  public void setIsPackageTypeBf(Boolean isPackageTypeBf) {
    this.isPackageTypeBf = isPackageTypeBf;
  }

  public GroupBookingRequestDto isPackageTypeMealDeal(Boolean isPackageTypeMealDeal) {
    this.isPackageTypeMealDeal = isPackageTypeMealDeal;
    return this;
  }

  /**
   * Get isPackageTypeMealDeal
   * @return isPackageTypeMealDeal
   */
  
  @Schema(name = "isPackageTypeMealDeal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isPackageTypeMealDeal")
  public Boolean getIsPackageTypeMealDeal() {
    return isPackageTypeMealDeal;
  }

  public void setIsPackageTypeMealDeal(Boolean isPackageTypeMealDeal) {
    this.isPackageTypeMealDeal = isPackageTypeMealDeal;
  }

  public GroupBookingRequestDto isSchoolOrYouth(Boolean isSchoolOrYouth) {
    this.isSchoolOrYouth = isSchoolOrYouth;
    return this;
  }

  /**
   * Get isSchoolOrYouth
   * @return isSchoolOrYouth
   */
  
  @Schema(name = "isSchoolOrYouth", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isSchoolOrYouth")
  public Boolean getIsSchoolOrYouth() {
    return isSchoolOrYouth;
  }

  public void setIsSchoolOrYouth(Boolean isSchoolOrYouth) {
    this.isSchoolOrYouth = isSchoolOrYouth;
  }

  public GroupBookingRequestDto isTravellingWithChild(Boolean isTravellingWithChild) {
    this.isTravellingWithChild = isTravellingWithChild;
    return this;
  }

  /**
   * Get isTravellingWithChild
   * @return isTravellingWithChild
   */
  
  @Schema(name = "isTravellingWithChild", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isTravellingWithChild")
  public Boolean getIsTravellingWithChild() {
    return isTravellingWithChild;
  }

  public void setIsTravellingWithChild(Boolean isTravellingWithChild) {
    this.isTravellingWithChild = isTravellingWithChild;
  }

  public GroupBookingRequestDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public GroupBookingRequestDto lastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  /**
   * Get lastName
   * @return lastName
   */
  @NotNull 
  @Schema(name = "lastName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public GroupBookingRequestDto phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
   */
  @NotNull 
  @Schema(name = "phoneNumber", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public GroupBookingRequestDto purposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
    return this;
  }

  /**
   * Get purposeOfStay
   * @return purposeOfStay
   */
  @NotNull 
  @Schema(name = "purposeOfStay", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("purposeOfStay")
  public String getPurposeOfStay() {
    return purposeOfStay;
  }

  public void setPurposeOfStay(String purposeOfStay) {
    this.purposeOfStay = purposeOfStay;
  }

  public GroupBookingRequestDto reasonForVisit(String reasonForVisit) {
    this.reasonForVisit = reasonForVisit;
    return this;
  }

  /**
   * Get reasonForVisit
   * @return reasonForVisit
   */
  @NotNull 
  @Schema(name = "reasonForVisit", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("reasonForVisit")
  public String getReasonForVisit() {
    return reasonForVisit;
  }

  public void setReasonForVisit(String reasonForVisit) {
    this.reasonForVisit = reasonForVisit;
  }

  public GroupBookingRequestDto reasonForVisitOther(String reasonForVisitOther) {
    this.reasonForVisitOther = reasonForVisitOther;
    return this;
  }

  /**
   * Get reasonForVisitOther
   * @return reasonForVisitOther
   */
  
  @Schema(name = "reasonForVisitOther", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reasonForVisitOther")
  public String getReasonForVisitOther() {
    return reasonForVisitOther;
  }

  public void setReasonForVisitOther(String reasonForVisitOther) {
    this.reasonForVisitOther = reasonForVisitOther;
  }

  public GroupBookingRequestDto singleOccupancy(Integer singleOccupancy) {
    this.singleOccupancy = singleOccupancy;
    return this;
  }

  /**
   * Get singleOccupancy
   * @return singleOccupancy
   */
  
  @Schema(name = "singleOccupancy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("singleOccupancy")
  public Integer getSingleOccupancy() {
    return singleOccupancy;
  }

  public void setSingleOccupancy(Integer singleOccupancy) {
    this.singleOccupancy = singleOccupancy;
  }

  public GroupBookingRequestDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  @NotNull 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public GroupBookingRequestDto twinRooms(Integer twinRooms) {
    this.twinRooms = twinRooms;
    return this;
  }

  /**
   * Get twinRooms
   * @return twinRooms
   */
  
  @Schema(name = "twinRooms", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("twinRooms")
  public Integer getTwinRooms() {
    return twinRooms;
  }

  public void setTwinRooms(Integer twinRooms) {
    this.twinRooms = twinRooms;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GroupBookingRequestDto groupBookingRequestDto = (GroupBookingRequestDto) o;
    return Objects.equals(this.accessibleDouble, groupBookingRequestDto.accessibleDouble) &&
        Objects.equals(this.accessibleSingle, groupBookingRequestDto.accessibleSingle) &&
        Objects.equals(this.accessibleTwin, groupBookingRequestDto.accessibleTwin) &&
        Objects.equals(this.additionalInformation, groupBookingRequestDto.additionalInformation) &&
        Objects.equals(this.arrivalDate, groupBookingRequestDto.arrivalDate) &&
        Objects.equals(this.bookerType, groupBookingRequestDto.bookerType) &&
        Objects.equals(this.companyName, groupBookingRequestDto.companyName) &&
        Objects.equals(this.departureDate, groupBookingRequestDto.departureDate) &&
        Objects.equals(this.doubleOccupancy, groupBookingRequestDto.doubleOccupancy) &&
        Objects.equals(this.emailAddress, groupBookingRequestDto.emailAddress) &&
        Objects.equals(this.familyOf21A1C, groupBookingRequestDto.familyOf21A1C) &&
        Objects.equals(this.familyOf31A2C, groupBookingRequestDto.familyOf31A2C) &&
        Objects.equals(this.familyOf32A1C, groupBookingRequestDto.familyOf32A1C) &&
        Objects.equals(this.familyOf42A2C, groupBookingRequestDto.familyOf42A2C) &&
        Objects.equals(this.firstName, groupBookingRequestDto.firstName) &&
        Objects.equals(this.hotelBrand, groupBookingRequestDto.hotelBrand) &&
        Objects.equals(this.hotelName, groupBookingRequestDto.hotelName) &&
        Objects.equals(this.isAccessibleRoom, groupBookingRequestDto.isAccessibleRoom) &&
        Objects.equals(this.isPackageTypeBf, groupBookingRequestDto.isPackageTypeBf) &&
        Objects.equals(this.isPackageTypeMealDeal, groupBookingRequestDto.isPackageTypeMealDeal) &&
        Objects.equals(this.isSchoolOrYouth, groupBookingRequestDto.isSchoolOrYouth) &&
        Objects.equals(this.isTravellingWithChild, groupBookingRequestDto.isTravellingWithChild) &&
        Objects.equals(this.language, groupBookingRequestDto.language) &&
        Objects.equals(this.lastName, groupBookingRequestDto.lastName) &&
        Objects.equals(this.phoneNumber, groupBookingRequestDto.phoneNumber) &&
        Objects.equals(this.purposeOfStay, groupBookingRequestDto.purposeOfStay) &&
        Objects.equals(this.reasonForVisit, groupBookingRequestDto.reasonForVisit) &&
        Objects.equals(this.reasonForVisitOther, groupBookingRequestDto.reasonForVisitOther) &&
        Objects.equals(this.singleOccupancy, groupBookingRequestDto.singleOccupancy) &&
        Objects.equals(this.title, groupBookingRequestDto.title) &&
        Objects.equals(this.twinRooms, groupBookingRequestDto.twinRooms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accessibleDouble, accessibleSingle, accessibleTwin, additionalInformation, arrivalDate, bookerType, companyName, departureDate, doubleOccupancy, emailAddress, familyOf21A1C, familyOf31A2C, familyOf32A1C, familyOf42A2C, firstName, hotelBrand, hotelName, isAccessibleRoom, isPackageTypeBf, isPackageTypeMealDeal, isSchoolOrYouth, isTravellingWithChild, language, lastName, phoneNumber, purposeOfStay, reasonForVisit, reasonForVisitOther, singleOccupancy, title, twinRooms);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GroupBookingRequestDto {\n");
    sb.append("    accessibleDouble: ").append(toIndentedString(accessibleDouble)).append("\n");
    sb.append("    accessibleSingle: ").append(toIndentedString(accessibleSingle)).append("\n");
    sb.append("    accessibleTwin: ").append(toIndentedString(accessibleTwin)).append("\n");
    sb.append("    additionalInformation: ").append(toIndentedString(additionalInformation)).append("\n");
    sb.append("    arrivalDate: ").append(toIndentedString(arrivalDate)).append("\n");
    sb.append("    bookerType: ").append(toIndentedString(bookerType)).append("\n");
    sb.append("    companyName: ").append(toIndentedString(companyName)).append("\n");
    sb.append("    departureDate: ").append(toIndentedString(departureDate)).append("\n");
    sb.append("    doubleOccupancy: ").append(toIndentedString(doubleOccupancy)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    familyOf21A1C: ").append(toIndentedString(familyOf21A1C)).append("\n");
    sb.append("    familyOf31A2C: ").append(toIndentedString(familyOf31A2C)).append("\n");
    sb.append("    familyOf32A1C: ").append(toIndentedString(familyOf32A1C)).append("\n");
    sb.append("    familyOf42A2C: ").append(toIndentedString(familyOf42A2C)).append("\n");
    sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
    sb.append("    hotelBrand: ").append(toIndentedString(hotelBrand)).append("\n");
    sb.append("    hotelName: ").append(toIndentedString(hotelName)).append("\n");
    sb.append("    isAccessibleRoom: ").append(toIndentedString(isAccessibleRoom)).append("\n");
    sb.append("    isPackageTypeBf: ").append(toIndentedString(isPackageTypeBf)).append("\n");
    sb.append("    isPackageTypeMealDeal: ").append(toIndentedString(isPackageTypeMealDeal)).append("\n");
    sb.append("    isSchoolOrYouth: ").append(toIndentedString(isSchoolOrYouth)).append("\n");
    sb.append("    isTravellingWithChild: ").append(toIndentedString(isTravellingWithChild)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    purposeOfStay: ").append(toIndentedString(purposeOfStay)).append("\n");
    sb.append("    reasonForVisit: ").append(toIndentedString(reasonForVisit)).append("\n");
    sb.append("    reasonForVisitOther: ").append(toIndentedString(reasonForVisitOther)).append("\n");
    sb.append("    singleOccupancy: ").append(toIndentedString(singleOccupancy)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    twinRooms: ").append(toIndentedString(twinRooms)).append("\n");
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

