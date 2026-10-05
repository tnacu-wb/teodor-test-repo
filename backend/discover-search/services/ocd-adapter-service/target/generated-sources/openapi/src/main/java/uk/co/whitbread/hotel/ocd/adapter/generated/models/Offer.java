package uk.co.whitbread.hotel.ocd.adapter.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.BlockInformation;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferAvailabilityStatus;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferRateInformation;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferTotalTypeWithTaxes;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.RatePackage;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Offer
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:32.554166+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class Offer {

  private @Nullable String bookingCode;

  private @Nullable String offerName;

  private @Nullable OfferAvailabilityStatus availabilityStatus;

  private @Nullable String roomType;

  private @Nullable String ratePlanCode;

  private Boolean rateChangeDuringStay = false;

  private @Nullable OfferRateInformation rateInformation;

  @Valid
  private List<@Valid RatePackage> packages = new ArrayList<>();

  private @Nullable OfferTotalTypeWithTaxes total;

  private @Nullable BlockInformation blockInformation;

  public Offer bookingCode(String bookingCode) {
    this.bookingCode = bookingCode;
    return this;
  }

  /**
   * The code of the offer, this is a concatenation of channel room type code and channel rate plan code.
   * @return bookingCode
   */
  @Size(min = 1, max = 100) 
  @Schema(name = "bookingCode", example = "XA1KXDAILY", description = "The code of the offer, this is a concatenation of channel room type code and channel rate plan code.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingCode")
  public String getBookingCode() {
    return bookingCode;
  }

  public void setBookingCode(String bookingCode) {
    this.bookingCode = bookingCode;
  }

  public Offer offerName(String offerName) {
    this.offerName = offerName;
    return this;
  }

  /**
   * Description of the offer, this includes information about the room type and rate plan.
   * @return offerName
   */
  @Size(max = 63) 
  @Schema(name = "offerName", example = "Deluxe King Room Corporate Rate in CP", description = "Description of the offer, this includes information about the room type and rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("offerName")
  public String getOfferName() {
    return offerName;
  }

  public void setOfferName(String offerName) {
    this.offerName = offerName;
  }

  public Offer availabilityStatus(OfferAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
    return this;
  }

  /**
   * Get availabilityStatus
   * @return availabilityStatus
   */
  @Valid 
  @Schema(name = "availabilityStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availabilityStatus")
  public OfferAvailabilityStatus getAvailabilityStatus() {
    return availabilityStatus;
  }

  public void setAvailabilityStatus(OfferAvailabilityStatus availabilityStatus) {
    this.availabilityStatus = availabilityStatus;
  }

  public Offer roomType(String roomType) {
    this.roomType = roomType;
    return this;
  }

  /**
   * The code for the room type of the offer.
   * @return roomType
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "roomType", example = "XA1K", description = "The code for the room type of the offer.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomType")
  public String getRoomType() {
    return roomType;
  }

  public void setRoomType(String roomType) {
    this.roomType = roomType;
  }

  public Offer ratePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
    return this;
  }

  /**
   * The code for the rate plan of the offer.
   * @return ratePlanCode
   */
  @Size(min = 1, max = 50) 
  @Schema(name = "ratePlanCode", example = "XDAILY", description = "The code for the rate plan of the offer.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("ratePlanCode")
  public String getRatePlanCode() {
    return ratePlanCode;
  }

  public void setRatePlanCode(String ratePlanCode) {
    this.ratePlanCode = ratePlanCode;
  }

  public Offer rateChangeDuringStay(Boolean rateChangeDuringStay) {
    this.rateChangeDuringStay = rateChangeDuringStay;
    return this;
  }

  /**
   * When true there is a rate change over the course of the stay.
   * @return rateChangeDuringStay
   */
  
  @Schema(name = "rateChangeDuringStay", example = "true", description = "When true there is a rate change over the course of the stay.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateChangeDuringStay")
  public Boolean getRateChangeDuringStay() {
    return rateChangeDuringStay;
  }

  public void setRateChangeDuringStay(Boolean rateChangeDuringStay) {
    this.rateChangeDuringStay = rateChangeDuringStay;
  }

  public Offer rateInformation(OfferRateInformation rateInformation) {
    this.rateInformation = rateInformation;
    return this;
  }

  /**
   * Get rateInformation
   * @return rateInformation
   */
  @Valid 
  @Schema(name = "rateInformation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rateInformation")
  public OfferRateInformation getRateInformation() {
    return rateInformation;
  }

  public void setRateInformation(OfferRateInformation rateInformation) {
    this.rateInformation = rateInformation;
  }

  public Offer packages(List<@Valid RatePackage> packages) {
    this.packages = packages;
    return this;
  }

  public Offer addPackagesItem(RatePackage packagesItem) {
    if (this.packages == null) {
      this.packages = new ArrayList<>();
    }
    this.packages.add(packagesItem);
    return this;
  }

  /**
   * List of package elements and/or package groups associated to the rate plan.
   * @return packages
   */
  @Valid 
  @Schema(name = "packages", description = "List of package elements and/or package groups associated to the rate plan.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("packages")
  public List<@Valid RatePackage> getPackages() {
    return packages;
  }

  public void setPackages(List<@Valid RatePackage> packages) {
    this.packages = packages;
  }

  public Offer total(OfferTotalTypeWithTaxes total) {
    this.total = total;
    return this;
  }

  /**
   * Get total
   * @return total
   */
  @Valid 
  @Schema(name = "total", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("total")
  public OfferTotalTypeWithTaxes getTotal() {
    return total;
  }

  public void setTotal(OfferTotalTypeWithTaxes total) {
    this.total = total;
  }

  public Offer blockInformation(BlockInformation blockInformation) {
    this.blockInformation = blockInformation;
    return this;
  }

  /**
   * Get blockInformation
   * @return blockInformation
   */
  @Valid 
  @Schema(name = "blockInformation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("blockInformation")
  public BlockInformation getBlockInformation() {
    return blockInformation;
  }

  public void setBlockInformation(BlockInformation blockInformation) {
    this.blockInformation = blockInformation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Offer offer = (Offer) o;
    return Objects.equals(this.bookingCode, offer.bookingCode) &&
        Objects.equals(this.offerName, offer.offerName) &&
        Objects.equals(this.availabilityStatus, offer.availabilityStatus) &&
        Objects.equals(this.roomType, offer.roomType) &&
        Objects.equals(this.ratePlanCode, offer.ratePlanCode) &&
        Objects.equals(this.rateChangeDuringStay, offer.rateChangeDuringStay) &&
        Objects.equals(this.rateInformation, offer.rateInformation) &&
        Objects.equals(this.packages, offer.packages) &&
        Objects.equals(this.total, offer.total) &&
        Objects.equals(this.blockInformation, offer.blockInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingCode, offerName, availabilityStatus, roomType, ratePlanCode, rateChangeDuringStay, rateInformation, packages, total, blockInformation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Offer {\n");
    sb.append("    bookingCode: ").append(toIndentedString(bookingCode)).append("\n");
    sb.append("    offerName: ").append(toIndentedString(offerName)).append("\n");
    sb.append("    availabilityStatus: ").append(toIndentedString(availabilityStatus)).append("\n");
    sb.append("    roomType: ").append(toIndentedString(roomType)).append("\n");
    sb.append("    ratePlanCode: ").append(toIndentedString(ratePlanCode)).append("\n");
    sb.append("    rateChangeDuringStay: ").append(toIndentedString(rateChangeDuringStay)).append("\n");
    sb.append("    rateInformation: ").append(toIndentedString(rateInformation)).append("\n");
    sb.append("    packages: ").append(toIndentedString(packages)).append("\n");
    sb.append("    total: ").append(toIndentedString(total)).append("\n");
    sb.append("    blockInformation: ").append(toIndentedString(blockInformation)).append("\n");
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

