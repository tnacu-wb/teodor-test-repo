package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.Balance;
import uk.co.whitbread.basket.generated.models.ohip.CompBalance;
import uk.co.whitbread.basket.generated.models.ohip.FoodAndBevRevenue;
import uk.co.whitbread.basket.generated.models.ohip.NonRevenue;
import uk.co.whitbread.basket.generated.models.ohip.OtherRevenue;
import uk.co.whitbread.basket.generated.models.ohip.RoomRevenue;
import uk.co.whitbread.basket.generated.models.ohip.TotalFixedCharge;
import uk.co.whitbread.basket.generated.models.ohip.TotalPayment;
import uk.co.whitbread.basket.generated.models.ohip.TotalRevenue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RevenuesAndBalances
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RevenuesAndBalances {

  private @Nullable Balance balance;

  private @Nullable CompBalance compBalance;

  private @Nullable FoodAndBevRevenue foodAndBevRevenue;

  private @Nullable NonRevenue nonRevenue;

  private @Nullable OtherRevenue otherRevenue;

  private @Nullable RoomRevenue roomRevenue;

  private @Nullable TotalFixedCharge totalFixedCharge;

  private @Nullable TotalPayment totalPayment;

  private @Nullable TotalRevenue totalRevenue;

  public RevenuesAndBalances balance(Balance balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
   * @return balance
   */
  @Valid 
  @Schema(name = "balance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("balance")
  public Balance getBalance() {
    return balance;
  }

  public void setBalance(Balance balance) {
    this.balance = balance;
  }

  public RevenuesAndBalances compBalance(CompBalance compBalance) {
    this.compBalance = compBalance;
    return this;
  }

  /**
   * Get compBalance
   * @return compBalance
   */
  @Valid 
  @Schema(name = "compBalance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("compBalance")
  public CompBalance getCompBalance() {
    return compBalance;
  }

  public void setCompBalance(CompBalance compBalance) {
    this.compBalance = compBalance;
  }

  public RevenuesAndBalances foodAndBevRevenue(FoodAndBevRevenue foodAndBevRevenue) {
    this.foodAndBevRevenue = foodAndBevRevenue;
    return this;
  }

  /**
   * Get foodAndBevRevenue
   * @return foodAndBevRevenue
   */
  @Valid 
  @Schema(name = "foodAndBevRevenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("foodAndBevRevenue")
  public FoodAndBevRevenue getFoodAndBevRevenue() {
    return foodAndBevRevenue;
  }

  public void setFoodAndBevRevenue(FoodAndBevRevenue foodAndBevRevenue) {
    this.foodAndBevRevenue = foodAndBevRevenue;
  }

  public RevenuesAndBalances nonRevenue(NonRevenue nonRevenue) {
    this.nonRevenue = nonRevenue;
    return this;
  }

  /**
   * Get nonRevenue
   * @return nonRevenue
   */
  @Valid 
  @Schema(name = "nonRevenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("nonRevenue")
  public NonRevenue getNonRevenue() {
    return nonRevenue;
  }

  public void setNonRevenue(NonRevenue nonRevenue) {
    this.nonRevenue = nonRevenue;
  }

  public RevenuesAndBalances otherRevenue(OtherRevenue otherRevenue) {
    this.otherRevenue = otherRevenue;
    return this;
  }

  /**
   * Get otherRevenue
   * @return otherRevenue
   */
  @Valid 
  @Schema(name = "otherRevenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("otherRevenue")
  public OtherRevenue getOtherRevenue() {
    return otherRevenue;
  }

  public void setOtherRevenue(OtherRevenue otherRevenue) {
    this.otherRevenue = otherRevenue;
  }

  public RevenuesAndBalances roomRevenue(RoomRevenue roomRevenue) {
    this.roomRevenue = roomRevenue;
    return this;
  }

  /**
   * Get roomRevenue
   * @return roomRevenue
   */
  @Valid 
  @Schema(name = "roomRevenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roomRevenue")
  public RoomRevenue getRoomRevenue() {
    return roomRevenue;
  }

  public void setRoomRevenue(RoomRevenue roomRevenue) {
    this.roomRevenue = roomRevenue;
  }

  public RevenuesAndBalances totalFixedCharge(TotalFixedCharge totalFixedCharge) {
    this.totalFixedCharge = totalFixedCharge;
    return this;
  }

  /**
   * Get totalFixedCharge
   * @return totalFixedCharge
   */
  @Valid 
  @Schema(name = "totalFixedCharge", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalFixedCharge")
  public TotalFixedCharge getTotalFixedCharge() {
    return totalFixedCharge;
  }

  public void setTotalFixedCharge(TotalFixedCharge totalFixedCharge) {
    this.totalFixedCharge = totalFixedCharge;
  }

  public RevenuesAndBalances totalPayment(TotalPayment totalPayment) {
    this.totalPayment = totalPayment;
    return this;
  }

  /**
   * Get totalPayment
   * @return totalPayment
   */
  @Valid 
  @Schema(name = "totalPayment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalPayment")
  public TotalPayment getTotalPayment() {
    return totalPayment;
  }

  public void setTotalPayment(TotalPayment totalPayment) {
    this.totalPayment = totalPayment;
  }

  public RevenuesAndBalances totalRevenue(TotalRevenue totalRevenue) {
    this.totalRevenue = totalRevenue;
    return this;
  }

  /**
   * Get totalRevenue
   * @return totalRevenue
   */
  @Valid 
  @Schema(name = "totalRevenue", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("totalRevenue")
  public TotalRevenue getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(TotalRevenue totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RevenuesAndBalances revenuesAndBalances = (RevenuesAndBalances) o;
    return Objects.equals(this.balance, revenuesAndBalances.balance) &&
        Objects.equals(this.compBalance, revenuesAndBalances.compBalance) &&
        Objects.equals(this.foodAndBevRevenue, revenuesAndBalances.foodAndBevRevenue) &&
        Objects.equals(this.nonRevenue, revenuesAndBalances.nonRevenue) &&
        Objects.equals(this.otherRevenue, revenuesAndBalances.otherRevenue) &&
        Objects.equals(this.roomRevenue, revenuesAndBalances.roomRevenue) &&
        Objects.equals(this.totalFixedCharge, revenuesAndBalances.totalFixedCharge) &&
        Objects.equals(this.totalPayment, revenuesAndBalances.totalPayment) &&
        Objects.equals(this.totalRevenue, revenuesAndBalances.totalRevenue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balance, compBalance, foodAndBevRevenue, nonRevenue, otherRevenue, roomRevenue, totalFixedCharge, totalPayment, totalRevenue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RevenuesAndBalances {\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
    sb.append("    compBalance: ").append(toIndentedString(compBalance)).append("\n");
    sb.append("    foodAndBevRevenue: ").append(toIndentedString(foodAndBevRevenue)).append("\n");
    sb.append("    nonRevenue: ").append(toIndentedString(nonRevenue)).append("\n");
    sb.append("    otherRevenue: ").append(toIndentedString(otherRevenue)).append("\n");
    sb.append("    roomRevenue: ").append(toIndentedString(roomRevenue)).append("\n");
    sb.append("    totalFixedCharge: ").append(toIndentedString(totalFixedCharge)).append("\n");
    sb.append("    totalPayment: ").append(toIndentedString(totalPayment)).append("\n");
    sb.append("    totalRevenue: ").append(toIndentedString(totalRevenue)).append("\n");
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

