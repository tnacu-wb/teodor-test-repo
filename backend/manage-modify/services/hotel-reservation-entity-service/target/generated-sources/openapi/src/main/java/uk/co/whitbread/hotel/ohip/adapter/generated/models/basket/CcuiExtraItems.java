package uk.co.whitbread.hotel.ohip.adapter.generated.models.basket;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AccountCompanyItems;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BusinessItems;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.NonguaranteedItems;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CcuiExtraItems
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:25.444555+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CcuiExtraItems {

  private @Nullable AccountCompanyItems accountCompanyItems;

  private @Nullable String addressCompanyName;

  private @Nullable BusinessItems businessItems;

  private @Nullable Boolean cardPresent;

  private @Nullable NonguaranteedItems nonguaranteedItems;

  public CcuiExtraItems accountCompanyItems(AccountCompanyItems accountCompanyItems) {
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
  public AccountCompanyItems getAccountCompanyItems() {
    return accountCompanyItems;
  }

  public void setAccountCompanyItems(AccountCompanyItems accountCompanyItems) {
    this.accountCompanyItems = accountCompanyItems;
  }

  public CcuiExtraItems addressCompanyName(String addressCompanyName) {
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

  public CcuiExtraItems businessItems(BusinessItems businessItems) {
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
  public BusinessItems getBusinessItems() {
    return businessItems;
  }

  public void setBusinessItems(BusinessItems businessItems) {
    this.businessItems = businessItems;
  }

  public CcuiExtraItems cardPresent(Boolean cardPresent) {
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

  public CcuiExtraItems nonguaranteedItems(NonguaranteedItems nonguaranteedItems) {
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
  public NonguaranteedItems getNonguaranteedItems() {
    return nonguaranteedItems;
  }

  public void setNonguaranteedItems(NonguaranteedItems nonguaranteedItems) {
    this.nonguaranteedItems = nonguaranteedItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CcuiExtraItems ccuiExtraItems = (CcuiExtraItems) o;
    return Objects.equals(this.accountCompanyItems, ccuiExtraItems.accountCompanyItems) &&
        Objects.equals(this.addressCompanyName, ccuiExtraItems.addressCompanyName) &&
        Objects.equals(this.businessItems, ccuiExtraItems.businessItems) &&
        Objects.equals(this.cardPresent, ccuiExtraItems.cardPresent) &&
        Objects.equals(this.nonguaranteedItems, ccuiExtraItems.nonguaranteedItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountCompanyItems, addressCompanyName, businessItems, cardPresent, nonguaranteedItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CcuiExtraItems {\n");
    sb.append("    accountCompanyItems: ").append(toIndentedString(accountCompanyItems)).append("\n");
    sb.append("    addressCompanyName: ").append(toIndentedString(addressCompanyName)).append("\n");
    sb.append("    businessItems: ").append(toIndentedString(businessItems)).append("\n");
    sb.append("    cardPresent: ").append(toIndentedString(cardPresent)).append("\n");
    sb.append("    nonguaranteedItems: ").append(toIndentedString(nonguaranteedItems)).append("\n");
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

