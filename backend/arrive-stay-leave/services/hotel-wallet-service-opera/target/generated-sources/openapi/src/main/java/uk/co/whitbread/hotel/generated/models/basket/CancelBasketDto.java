package uk.co.whitbread.hotel.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.generated.models.basket.DepositsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CancelBasketDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:22.312200+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CancelBasketDto {

  @Valid
  private List<@Valid DepositsDto> deposits = new ArrayList<>();

  private @Nullable Boolean isFailed;

  private @Nullable Boolean sendEmail;

  public CancelBasketDto deposits(List<@Valid DepositsDto> deposits) {
    this.deposits = deposits;
    return this;
  }

  public CancelBasketDto addDepositsItem(DepositsDto depositsItem) {
    if (this.deposits == null) {
      this.deposits = new ArrayList<>();
    }
    this.deposits.add(depositsItem);
    return this;
  }

  /**
   * Get deposits
   * @return deposits
   */
  @Valid 
  @Schema(name = "deposits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("deposits")
  public List<@Valid DepositsDto> getDeposits() {
    return deposits;
  }

  public void setDeposits(List<@Valid DepositsDto> deposits) {
    this.deposits = deposits;
  }

  public CancelBasketDto isFailed(Boolean isFailed) {
    this.isFailed = isFailed;
    return this;
  }

  /**
   * Get isFailed
   * @return isFailed
   */
  
  @Schema(name = "isFailed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isFailed")
  public Boolean getIsFailed() {
    return isFailed;
  }

  public void setIsFailed(Boolean isFailed) {
    this.isFailed = isFailed;
  }

  public CancelBasketDto sendEmail(Boolean sendEmail) {
    this.sendEmail = sendEmail;
    return this;
  }

  /**
   * Get sendEmail
   * @return sendEmail
   */
  
  @Schema(name = "sendEmail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendEmail")
  public Boolean getSendEmail() {
    return sendEmail;
  }

  public void setSendEmail(Boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CancelBasketDto cancelBasketDto = (CancelBasketDto) o;
    return Objects.equals(this.deposits, cancelBasketDto.deposits) &&
        Objects.equals(this.isFailed, cancelBasketDto.isFailed) &&
        Objects.equals(this.sendEmail, cancelBasketDto.sendEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deposits, isFailed, sendEmail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CancelBasketDto {\n");
    sb.append("    deposits: ").append(toIndentedString(deposits)).append("\n");
    sb.append("    isFailed: ").append(toIndentedString(isFailed)).append("\n");
    sb.append("    sendEmail: ").append(toIndentedString(sendEmail)).append("\n");
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

