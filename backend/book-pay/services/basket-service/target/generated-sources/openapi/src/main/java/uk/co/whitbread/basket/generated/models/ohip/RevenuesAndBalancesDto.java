package uk.co.whitbread.basket.generated.models.ohip;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.ohip.BalanceDto;
import uk.co.whitbread.basket.generated.models.ohip.CompBalanceDto;
import uk.co.whitbread.basket.generated.models.ohip.FoodAndBevRevenueDto;
import uk.co.whitbread.basket.generated.models.ohip.NonRevenueDto;
import uk.co.whitbread.basket.generated.models.ohip.OtherRevenueDto;
import uk.co.whitbread.basket.generated.models.ohip.RoomRevenueDto;
import uk.co.whitbread.basket.generated.models.ohip.TotalFixedChargeDto;
import uk.co.whitbread.basket.generated.models.ohip.TotalPaymentDto;
import uk.co.whitbread.basket.generated.models.ohip.TotalRevenueDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * RevenuesAndBalancesDto
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:10:10.951524+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class RevenuesAndBalancesDto {

  private @Nullable BalanceDto balance;

  private @Nullable CompBalanceDto compBalance;

  private @Nullable FoodAndBevRevenueDto foodAndBevRevenue;

  private @Nullable NonRevenueDto nonRevenue;

  private @Nullable OtherRevenueDto otherRevenue;

  private @Nullable RoomRevenueDto roomRevenue;

  private @Nullable TotalFixedChargeDto totalFixedCharge;

  private @Nullable TotalPaymentDto totalPayment;

  private @Nullable TotalRevenueDto totalRevenue;

  public RevenuesAndBalancesDto balance(BalanceDto balance) {
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
  public BalanceDto getBalance() {
    return balance;
  }

  public void setBalance(BalanceDto balance) {
    this.balance = balance;
  }

  public RevenuesAndBalancesDto compBalance(CompBalanceDto compBalance) {
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
  public CompBalanceDto getCompBalance() {
    return compBalance;
  }

  public void setCompBalance(CompBalanceDto compBalance) {
    this.compBalance = compBalance;
  }

  public RevenuesAndBalancesDto foodAndBevRevenue(FoodAndBevRevenueDto foodAndBevRevenue) {
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
  public FoodAndBevRevenueDto getFoodAndBevRevenue() {
    return foodAndBevRevenue;
  }

  public void setFoodAndBevRevenue(FoodAndBevRevenueDto foodAndBevRevenue) {
    this.foodAndBevRevenue = foodAndBevRevenue;
  }

  public RevenuesAndBalancesDto nonRevenue(NonRevenueDto nonRevenue) {
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
  public NonRevenueDto getNonRevenue() {
    return nonRevenue;
  }

  public void setNonRevenue(NonRevenueDto nonRevenue) {
    this.nonRevenue = nonRevenue;
  }

  public RevenuesAndBalancesDto otherRevenue(OtherRevenueDto otherRevenue) {
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
  public OtherRevenueDto getOtherRevenue() {
    return otherRevenue;
  }

  public void setOtherRevenue(OtherRevenueDto otherRevenue) {
    this.otherRevenue = otherRevenue;
  }

  public RevenuesAndBalancesDto roomRevenue(RoomRevenueDto roomRevenue) {
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
  public RoomRevenueDto getRoomRevenue() {
    return roomRevenue;
  }

  public void setRoomRevenue(RoomRevenueDto roomRevenue) {
    this.roomRevenue = roomRevenue;
  }

  public RevenuesAndBalancesDto totalFixedCharge(TotalFixedChargeDto totalFixedCharge) {
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
  public TotalFixedChargeDto getTotalFixedCharge() {
    return totalFixedCharge;
  }

  public void setTotalFixedCharge(TotalFixedChargeDto totalFixedCharge) {
    this.totalFixedCharge = totalFixedCharge;
  }

  public RevenuesAndBalancesDto totalPayment(TotalPaymentDto totalPayment) {
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
  public TotalPaymentDto getTotalPayment() {
    return totalPayment;
  }

  public void setTotalPayment(TotalPaymentDto totalPayment) {
    this.totalPayment = totalPayment;
  }

  public RevenuesAndBalancesDto totalRevenue(TotalRevenueDto totalRevenue) {
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
  public TotalRevenueDto getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(TotalRevenueDto totalRevenue) {
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
    RevenuesAndBalancesDto revenuesAndBalancesDto = (RevenuesAndBalancesDto) o;
    return Objects.equals(this.balance, revenuesAndBalancesDto.balance) &&
        Objects.equals(this.compBalance, revenuesAndBalancesDto.compBalance) &&
        Objects.equals(this.foodAndBevRevenue, revenuesAndBalancesDto.foodAndBevRevenue) &&
        Objects.equals(this.nonRevenue, revenuesAndBalancesDto.nonRevenue) &&
        Objects.equals(this.otherRevenue, revenuesAndBalancesDto.otherRevenue) &&
        Objects.equals(this.roomRevenue, revenuesAndBalancesDto.roomRevenue) &&
        Objects.equals(this.totalFixedCharge, revenuesAndBalancesDto.totalFixedCharge) &&
        Objects.equals(this.totalPayment, revenuesAndBalancesDto.totalPayment) &&
        Objects.equals(this.totalRevenue, revenuesAndBalancesDto.totalRevenue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balance, compBalance, foodAndBevRevenue, nonRevenue, otherRevenue, roomRevenue, totalFixedCharge, totalPayment, totalRevenue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RevenuesAndBalancesDto {\n");
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

