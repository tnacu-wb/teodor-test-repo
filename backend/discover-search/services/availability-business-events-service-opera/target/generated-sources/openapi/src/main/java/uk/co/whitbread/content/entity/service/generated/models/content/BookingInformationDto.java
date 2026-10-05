package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.BookingFlowStepDto;
import uk.co.whitbread.content.entity.service.generated.models.content.BookingSpinnerConfigDto;
import uk.co.whitbread.content.entity.service.generated.models.content.DonationDto;
import uk.co.whitbread.content.entity.service.generated.models.content.InfoMessageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ItemDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PaymentInfoMessageDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PrivacyPolicyDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PromotionPanelDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TermsAndConditionsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookingInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:46.057591+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class BookingInformationDto {

  @Valid
  private List<@Valid BookingFlowStepDto> bookingFlowSteps = new ArrayList<>();

  @Valid
  private List<@Valid BookingSpinnerConfigDto> bookingSpinnerConfig = new ArrayList<>();

  private @Nullable String brand;

  private @Nullable DonationDto donation;

  @Valid
  private List<@Valid InfoMessageDto> infoMessages = new ArrayList<>();

  private @Nullable String mealsNotAvailableMessage;

  private @Nullable String mealsNotAvailableTitle;

  @Valid
  private List<@Valid PaymentInfoMessageDto> paymentInfoMessages = new ArrayList<>();

  private @Nullable PrivacyPolicyDto privacyPolicy;

  @Valid
  private List<@Valid PromotionPanelDto> promotionPanels = new ArrayList<>();

  private @Nullable String restaurantClosedMessage;

  private @Nullable String restaurantClosedTitle;

  @Valid
  private List<@Valid TermsAndConditionsDto> termsAndConditions = new ArrayList<>();

  @Valid
  private List<@Valid ItemDto> upsellItems = new ArrayList<>();

  public BookingInformationDto bookingFlowSteps(List<@Valid BookingFlowStepDto> bookingFlowSteps) {
    this.bookingFlowSteps = bookingFlowSteps;
    return this;
  }

  public BookingInformationDto addBookingFlowStepsItem(BookingFlowStepDto bookingFlowStepsItem) {
    if (this.bookingFlowSteps == null) {
      this.bookingFlowSteps = new ArrayList<>();
    }
    this.bookingFlowSteps.add(bookingFlowStepsItem);
    return this;
  }

  /**
   * Get bookingFlowSteps
   * @return bookingFlowSteps
   */
  @Valid 
  @Schema(name = "bookingFlowSteps", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingFlowSteps")
  public List<@Valid BookingFlowStepDto> getBookingFlowSteps() {
    return bookingFlowSteps;
  }

  public void setBookingFlowSteps(List<@Valid BookingFlowStepDto> bookingFlowSteps) {
    this.bookingFlowSteps = bookingFlowSteps;
  }

  public BookingInformationDto bookingSpinnerConfig(List<@Valid BookingSpinnerConfigDto> bookingSpinnerConfig) {
    this.bookingSpinnerConfig = bookingSpinnerConfig;
    return this;
  }

  public BookingInformationDto addBookingSpinnerConfigItem(BookingSpinnerConfigDto bookingSpinnerConfigItem) {
    if (this.bookingSpinnerConfig == null) {
      this.bookingSpinnerConfig = new ArrayList<>();
    }
    this.bookingSpinnerConfig.add(bookingSpinnerConfigItem);
    return this;
  }

  /**
   * Get bookingSpinnerConfig
   * @return bookingSpinnerConfig
   */
  @Valid 
  @Schema(name = "bookingSpinnerConfig", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookingSpinnerConfig")
  public List<@Valid BookingSpinnerConfigDto> getBookingSpinnerConfig() {
    return bookingSpinnerConfig;
  }

  public void setBookingSpinnerConfig(List<@Valid BookingSpinnerConfigDto> bookingSpinnerConfig) {
    this.bookingSpinnerConfig = bookingSpinnerConfig;
  }

  public BookingInformationDto brand(String brand) {
    this.brand = brand;
    return this;
  }

  /**
   * Get brand
   * @return brand
   */
  
  @Schema(name = "brand", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("brand")
  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public BookingInformationDto donation(DonationDto donation) {
    this.donation = donation;
    return this;
  }

  /**
   * Get donation
   * @return donation
   */
  @Valid 
  @Schema(name = "donation", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("donation")
  public DonationDto getDonation() {
    return donation;
  }

  public void setDonation(DonationDto donation) {
    this.donation = donation;
  }

  public BookingInformationDto infoMessages(List<@Valid InfoMessageDto> infoMessages) {
    this.infoMessages = infoMessages;
    return this;
  }

  public BookingInformationDto addInfoMessagesItem(InfoMessageDto infoMessagesItem) {
    if (this.infoMessages == null) {
      this.infoMessages = new ArrayList<>();
    }
    this.infoMessages.add(infoMessagesItem);
    return this;
  }

  /**
   * Get infoMessages
   * @return infoMessages
   */
  @Valid 
  @Schema(name = "infoMessages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("infoMessages")
  public List<@Valid InfoMessageDto> getInfoMessages() {
    return infoMessages;
  }

  public void setInfoMessages(List<@Valid InfoMessageDto> infoMessages) {
    this.infoMessages = infoMessages;
  }

  public BookingInformationDto mealsNotAvailableMessage(String mealsNotAvailableMessage) {
    this.mealsNotAvailableMessage = mealsNotAvailableMessage;
    return this;
  }

  /**
   * Get mealsNotAvailableMessage
   * @return mealsNotAvailableMessage
   */
  
  @Schema(name = "mealsNotAvailableMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealsNotAvailableMessage")
  public String getMealsNotAvailableMessage() {
    return mealsNotAvailableMessage;
  }

  public void setMealsNotAvailableMessage(String mealsNotAvailableMessage) {
    this.mealsNotAvailableMessage = mealsNotAvailableMessage;
  }

  public BookingInformationDto mealsNotAvailableTitle(String mealsNotAvailableTitle) {
    this.mealsNotAvailableTitle = mealsNotAvailableTitle;
    return this;
  }

  /**
   * Get mealsNotAvailableTitle
   * @return mealsNotAvailableTitle
   */
  
  @Schema(name = "mealsNotAvailableTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mealsNotAvailableTitle")
  public String getMealsNotAvailableTitle() {
    return mealsNotAvailableTitle;
  }

  public void setMealsNotAvailableTitle(String mealsNotAvailableTitle) {
    this.mealsNotAvailableTitle = mealsNotAvailableTitle;
  }

  public BookingInformationDto paymentInfoMessages(List<@Valid PaymentInfoMessageDto> paymentInfoMessages) {
    this.paymentInfoMessages = paymentInfoMessages;
    return this;
  }

  public BookingInformationDto addPaymentInfoMessagesItem(PaymentInfoMessageDto paymentInfoMessagesItem) {
    if (this.paymentInfoMessages == null) {
      this.paymentInfoMessages = new ArrayList<>();
    }
    this.paymentInfoMessages.add(paymentInfoMessagesItem);
    return this;
  }

  /**
   * Get paymentInfoMessages
   * @return paymentInfoMessages
   */
  @Valid 
  @Schema(name = "paymentInfoMessages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentInfoMessages")
  public List<@Valid PaymentInfoMessageDto> getPaymentInfoMessages() {
    return paymentInfoMessages;
  }

  public void setPaymentInfoMessages(List<@Valid PaymentInfoMessageDto> paymentInfoMessages) {
    this.paymentInfoMessages = paymentInfoMessages;
  }

  public BookingInformationDto privacyPolicy(PrivacyPolicyDto privacyPolicy) {
    this.privacyPolicy = privacyPolicy;
    return this;
  }

  /**
   * Get privacyPolicy
   * @return privacyPolicy
   */
  @Valid 
  @Schema(name = "privacyPolicy", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("privacyPolicy")
  public PrivacyPolicyDto getPrivacyPolicy() {
    return privacyPolicy;
  }

  public void setPrivacyPolicy(PrivacyPolicyDto privacyPolicy) {
    this.privacyPolicy = privacyPolicy;
  }

  public BookingInformationDto promotionPanels(List<@Valid PromotionPanelDto> promotionPanels) {
    this.promotionPanels = promotionPanels;
    return this;
  }

  public BookingInformationDto addPromotionPanelsItem(PromotionPanelDto promotionPanelsItem) {
    if (this.promotionPanels == null) {
      this.promotionPanels = new ArrayList<>();
    }
    this.promotionPanels.add(promotionPanelsItem);
    return this;
  }

  /**
   * Get promotionPanels
   * @return promotionPanels
   */
  @Valid 
  @Schema(name = "promotionPanels", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("promotionPanels")
  public List<@Valid PromotionPanelDto> getPromotionPanels() {
    return promotionPanels;
  }

  public void setPromotionPanels(List<@Valid PromotionPanelDto> promotionPanels) {
    this.promotionPanels = promotionPanels;
  }

  public BookingInformationDto restaurantClosedMessage(String restaurantClosedMessage) {
    this.restaurantClosedMessage = restaurantClosedMessage;
    return this;
  }

  /**
   * Get restaurantClosedMessage
   * @return restaurantClosedMessage
   */
  
  @Schema(name = "restaurantClosedMessage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurantClosedMessage")
  public String getRestaurantClosedMessage() {
    return restaurantClosedMessage;
  }

  public void setRestaurantClosedMessage(String restaurantClosedMessage) {
    this.restaurantClosedMessage = restaurantClosedMessage;
  }

  public BookingInformationDto restaurantClosedTitle(String restaurantClosedTitle) {
    this.restaurantClosedTitle = restaurantClosedTitle;
    return this;
  }

  /**
   * Get restaurantClosedTitle
   * @return restaurantClosedTitle
   */
  
  @Schema(name = "restaurantClosedTitle", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("restaurantClosedTitle")
  public String getRestaurantClosedTitle() {
    return restaurantClosedTitle;
  }

  public void setRestaurantClosedTitle(String restaurantClosedTitle) {
    this.restaurantClosedTitle = restaurantClosedTitle;
  }

  public BookingInformationDto termsAndConditions(List<@Valid TermsAndConditionsDto> termsAndConditions) {
    this.termsAndConditions = termsAndConditions;
    return this;
  }

  public BookingInformationDto addTermsAndConditionsItem(TermsAndConditionsDto termsAndConditionsItem) {
    if (this.termsAndConditions == null) {
      this.termsAndConditions = new ArrayList<>();
    }
    this.termsAndConditions.add(termsAndConditionsItem);
    return this;
  }

  /**
   * Get termsAndConditions
   * @return termsAndConditions
   */
  @Valid 
  @Schema(name = "termsAndConditions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("termsAndConditions")
  public List<@Valid TermsAndConditionsDto> getTermsAndConditions() {
    return termsAndConditions;
  }

  public void setTermsAndConditions(List<@Valid TermsAndConditionsDto> termsAndConditions) {
    this.termsAndConditions = termsAndConditions;
  }

  public BookingInformationDto upsellItems(List<@Valid ItemDto> upsellItems) {
    this.upsellItems = upsellItems;
    return this;
  }

  public BookingInformationDto addUpsellItemsItem(ItemDto upsellItemsItem) {
    if (this.upsellItems == null) {
      this.upsellItems = new ArrayList<>();
    }
    this.upsellItems.add(upsellItemsItem);
    return this;
  }

  /**
   * Get upsellItems
   * @return upsellItems
   */
  @Valid 
  @Schema(name = "upsellItems", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upsellItems")
  public List<@Valid ItemDto> getUpsellItems() {
    return upsellItems;
  }

  public void setUpsellItems(List<@Valid ItemDto> upsellItems) {
    this.upsellItems = upsellItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookingInformationDto bookingInformationDto = (BookingInformationDto) o;
    return Objects.equals(this.bookingFlowSteps, bookingInformationDto.bookingFlowSteps) &&
        Objects.equals(this.bookingSpinnerConfig, bookingInformationDto.bookingSpinnerConfig) &&
        Objects.equals(this.brand, bookingInformationDto.brand) &&
        Objects.equals(this.donation, bookingInformationDto.donation) &&
        Objects.equals(this.infoMessages, bookingInformationDto.infoMessages) &&
        Objects.equals(this.mealsNotAvailableMessage, bookingInformationDto.mealsNotAvailableMessage) &&
        Objects.equals(this.mealsNotAvailableTitle, bookingInformationDto.mealsNotAvailableTitle) &&
        Objects.equals(this.paymentInfoMessages, bookingInformationDto.paymentInfoMessages) &&
        Objects.equals(this.privacyPolicy, bookingInformationDto.privacyPolicy) &&
        Objects.equals(this.promotionPanels, bookingInformationDto.promotionPanels) &&
        Objects.equals(this.restaurantClosedMessage, bookingInformationDto.restaurantClosedMessage) &&
        Objects.equals(this.restaurantClosedTitle, bookingInformationDto.restaurantClosedTitle) &&
        Objects.equals(this.termsAndConditions, bookingInformationDto.termsAndConditions) &&
        Objects.equals(this.upsellItems, bookingInformationDto.upsellItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookingFlowSteps, bookingSpinnerConfig, brand, donation, infoMessages, mealsNotAvailableMessage, mealsNotAvailableTitle, paymentInfoMessages, privacyPolicy, promotionPanels, restaurantClosedMessage, restaurantClosedTitle, termsAndConditions, upsellItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookingInformationDto {\n");
    sb.append("    bookingFlowSteps: ").append(toIndentedString(bookingFlowSteps)).append("\n");
    sb.append("    bookingSpinnerConfig: ").append(toIndentedString(bookingSpinnerConfig)).append("\n");
    sb.append("    brand: ").append(toIndentedString(brand)).append("\n");
    sb.append("    donation: ").append(toIndentedString(donation)).append("\n");
    sb.append("    infoMessages: ").append(toIndentedString(infoMessages)).append("\n");
    sb.append("    mealsNotAvailableMessage: ").append(toIndentedString(mealsNotAvailableMessage)).append("\n");
    sb.append("    mealsNotAvailableTitle: ").append(toIndentedString(mealsNotAvailableTitle)).append("\n");
    sb.append("    paymentInfoMessages: ").append(toIndentedString(paymentInfoMessages)).append("\n");
    sb.append("    privacyPolicy: ").append(toIndentedString(privacyPolicy)).append("\n");
    sb.append("    promotionPanels: ").append(toIndentedString(promotionPanels)).append("\n");
    sb.append("    restaurantClosedMessage: ").append(toIndentedString(restaurantClosedMessage)).append("\n");
    sb.append("    restaurantClosedTitle: ").append(toIndentedString(restaurantClosedTitle)).append("\n");
    sb.append("    termsAndConditions: ").append(toIndentedString(termsAndConditions)).append("\n");
    sb.append("    upsellItems: ").append(toIndentedString(upsellItems)).append("\n");
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

