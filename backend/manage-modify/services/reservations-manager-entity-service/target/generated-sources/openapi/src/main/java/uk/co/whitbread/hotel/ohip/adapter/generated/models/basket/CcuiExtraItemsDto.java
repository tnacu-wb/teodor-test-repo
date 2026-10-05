package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AccountCompanyItemsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BusinessItemsCcuiDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.NonguaranteedItemsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CcuiExtraItemsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:52:44.119190+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CcuiExtraItemsDto {

  private @Nullable AccountCompanyItemsDto accountCompanyItems;

  private @Nullable String addressCompanyName;

  private @Nullable BusinessItemsCcuiDto businessItems;

  private @Nullable Boolean cardPresent;

  private @Nullable NonguaranteedItemsDto nonguaranteedItems;

  private @Nullable Boolean sendMail;

  public CcuiExtraItemsDto accountCompanyItems(AccountCompanyItemsDto accountCompanyItems) {
    this.accountCompanyItems = accountCompanyItems;
    return this;
  }

  /**
   * Get accountCompanyItems
   * @return accountCompanyItems
   */
  @Valid 
  @Schema(name = "accountCompanyItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountCompanyItems")
  public AccountCompanyItemsDto getAccountCompanyItems() {
    return accountCompanyItems;
  }

  public void setAccountCompanyItems(AccountCompanyItemsDto accountCompanyItems) {
    this.accountCompanyItems = accountCompanyItems;
  }

  public CcuiExtraItemsDto addressCompanyName(String addressCompanyName) {
    this.addressCompanyName = addressCompanyName;
    return this;
  }

  /**
   * Get addressCompanyName
   * @return addressCompanyName
   */
  
  @Schema(name = "addressCompanyName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("addressCompanyName")
  public String getAddressCompanyName() {
    return addressCompanyName;
  }

  public void setAddressCompanyName(String addressCompanyName) {
    this.addressCompanyName = addressCompanyName;
  }

  public CcuiExtraItemsDto businessItems(BusinessItemsCcuiDto businessItems) {
    this.businessItems = businessItems;
    return this;
  }

  /**
   * Get businessItems
   * @return businessItems
   */
  @Valid 
  @Schema(name = "businessItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("businessItems")
  public BusinessItemsCcuiDto getBusinessItems() {
    return businessItems;
  }

  public void setBusinessItems(BusinessItemsCcuiDto businessItems) {
    this.businessItems = businessItems;
  }

  public CcuiExtraItemsDto cardPresent(Boolean cardPresent) {
    this.cardPresent = cardPresent;
    return this;
  }

  /**
   * Get cardPresent
   * @return cardPresent
   */
  
  @Schema(name = "cardPresent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardPresent")
  public Boolean getCardPresent() {
    return cardPresent;
  }

  public void setCardPresent(Boolean cardPresent) {
    this.cardPresent = cardPresent;
  }

  public CcuiExtraItemsDto nonguaranteedItems(NonguaranteedItemsDto nonguaranteedItems) {
    this.nonguaranteedItems = nonguaranteedItems;
    return this;
  }

  /**
   * Get nonguaranteedItems
   * @return nonguaranteedItems
   */
  @Valid 
  @Schema(name = "nonguaranteedItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nonguaranteedItems")
  public NonguaranteedItemsDto getNonguaranteedItems() {
    return nonguaranteedItems;
  }

  public void setNonguaranteedItems(NonguaranteedItemsDto nonguaranteedItems) {
    this.nonguaranteedItems = nonguaranteedItems;
  }

  public CcuiExtraItemsDto sendMail(Boolean sendMail) {
    this.sendMail = sendMail;
    return this;
  }

  /**
   * Get sendMail
   * @return sendMail
   */
  
  @Schema(name = "sendMail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sendMail")
  public Boolean getSendMail() {
    return sendMail;
  }

  public void setSendMail(Boolean sendMail) {
    this.sendMail = sendMail;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CcuiExtraItemsDto ccuiExtraItemsDto = (CcuiExtraItemsDto) o;
    return Objects.equals(this.accountCompanyItems, ccuiExtraItemsDto.accountCompanyItems) &&
        Objects.equals(this.addressCompanyName, ccuiExtraItemsDto.addressCompanyName) &&
        Objects.equals(this.businessItems, ccuiExtraItemsDto.businessItems) &&
        Objects.equals(this.cardPresent, ccuiExtraItemsDto.cardPresent) &&
        Objects.equals(this.nonguaranteedItems, ccuiExtraItemsDto.nonguaranteedItems) &&
        Objects.equals(this.sendMail, ccuiExtraItemsDto.sendMail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountCompanyItems, addressCompanyName, businessItems, cardPresent, nonguaranteedItems, sendMail);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CcuiExtraItemsDto {\n");
    sb.append("    accountCompanyItems: ").append(toIndentedString(accountCompanyItems)).append("\n");
    sb.append("    addressCompanyName: ").append(toIndentedString(addressCompanyName)).append("\n");
    sb.append("    businessItems: ").append(toIndentedString(businessItems)).append("\n");
    sb.append("    cardPresent: ").append(toIndentedString(cardPresent)).append("\n");
    sb.append("    nonguaranteedItems: ").append(toIndentedString(nonguaranteedItems)).append("\n");
    sb.append("    sendMail: ").append(toIndentedString(sendMail)).append("\n");
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

