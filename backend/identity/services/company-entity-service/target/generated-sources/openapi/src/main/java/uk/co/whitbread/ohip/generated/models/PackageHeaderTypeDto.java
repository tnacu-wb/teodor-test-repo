package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CheckInPrimaryDetailsDto;
import uk.co.whitbread.ohip.generated.models.PostingAttributesDto;
import uk.co.whitbread.ohip.generated.models.TransactionDetailsDto;
import uk.co.whitbread.ohip.generated.models.UsageDetailsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * PackageHeaderTypeDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class PackageHeaderTypeDto {

  private @Nullable PostingAttributesDto postingAttributes;

  private @Nullable CheckInPrimaryDetailsDto primaryDetails;

  private @Nullable TransactionDetailsDto transactionDetails;

  private @Nullable UsageDetailsDto usageDetails;

  public PackageHeaderTypeDto postingAttributes(PostingAttributesDto postingAttributes) {
    this.postingAttributes = postingAttributes;
    return this;
  }

  /**
   * Get postingAttributes
   * @return postingAttributes
   */
  @Valid 
  @Schema(name = "postingAttributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("postingAttributes")
  public PostingAttributesDto getPostingAttributes() {
    return postingAttributes;
  }

  public void setPostingAttributes(PostingAttributesDto postingAttributes) {
    this.postingAttributes = postingAttributes;
  }

  public PackageHeaderTypeDto primaryDetails(CheckInPrimaryDetailsDto primaryDetails) {
    this.primaryDetails = primaryDetails;
    return this;
  }

  /**
   * Get primaryDetails
   * @return primaryDetails
   */
  @Valid 
  @Schema(name = "primaryDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("primaryDetails")
  public CheckInPrimaryDetailsDto getPrimaryDetails() {
    return primaryDetails;
  }

  public void setPrimaryDetails(CheckInPrimaryDetailsDto primaryDetails) {
    this.primaryDetails = primaryDetails;
  }

  public PackageHeaderTypeDto transactionDetails(TransactionDetailsDto transactionDetails) {
    this.transactionDetails = transactionDetails;
    return this;
  }

  /**
   * Get transactionDetails
   * @return transactionDetails
   */
  @Valid 
  @Schema(name = "transactionDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("transactionDetails")
  public TransactionDetailsDto getTransactionDetails() {
    return transactionDetails;
  }

  public void setTransactionDetails(TransactionDetailsDto transactionDetails) {
    this.transactionDetails = transactionDetails;
  }

  public PackageHeaderTypeDto usageDetails(UsageDetailsDto usageDetails) {
    this.usageDetails = usageDetails;
    return this;
  }

  /**
   * Get usageDetails
   * @return usageDetails
   */
  @Valid 
  @Schema(name = "usageDetails", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("usageDetails")
  public UsageDetailsDto getUsageDetails() {
    return usageDetails;
  }

  public void setUsageDetails(UsageDetailsDto usageDetails) {
    this.usageDetails = usageDetails;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PackageHeaderTypeDto packageHeaderTypeDto = (PackageHeaderTypeDto) o;
    return Objects.equals(this.postingAttributes, packageHeaderTypeDto.postingAttributes) &&
        Objects.equals(this.primaryDetails, packageHeaderTypeDto.primaryDetails) &&
        Objects.equals(this.transactionDetails, packageHeaderTypeDto.transactionDetails) &&
        Objects.equals(this.usageDetails, packageHeaderTypeDto.usageDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(postingAttributes, primaryDetails, transactionDetails, usageDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PackageHeaderTypeDto {\n");
    sb.append("    postingAttributes: ").append(toIndentedString(postingAttributes)).append("\n");
    sb.append("    primaryDetails: ").append(toIndentedString(primaryDetails)).append("\n");
    sb.append("    transactionDetails: ").append(toIndentedString(transactionDetails)).append("\n");
    sb.append("    usageDetails: ").append(toIndentedString(usageDetails)).append("\n");
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

