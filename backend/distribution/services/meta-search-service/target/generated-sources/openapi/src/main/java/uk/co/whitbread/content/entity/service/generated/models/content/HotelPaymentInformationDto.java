package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.AcceptedCreditCardDto;
import uk.co.whitbread.content.entity.service.generated.models.content.AddressDto;
import uk.co.whitbread.content.entity.service.generated.models.content.PaymentProviderDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * HotelPaymentInformationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:51:27.565654+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class HotelPaymentInformationDto {

  @Valid
  private List<@Valid AcceptedCreditCardDto> acceptedCreditCards = new ArrayList<>();

  private @Nullable AddressDto address;

  @Valid
  private Map<String, String> paymentMethodsOpera = new HashMap<>();

  @Valid
  private List<@Valid PaymentProviderDto> paymentProviders = new ArrayList<>();

  public HotelPaymentInformationDto acceptedCreditCards(List<@Valid AcceptedCreditCardDto> acceptedCreditCards) {
    this.acceptedCreditCards = acceptedCreditCards;
    return this;
  }

  public HotelPaymentInformationDto addAcceptedCreditCardsItem(AcceptedCreditCardDto acceptedCreditCardsItem) {
    if (this.acceptedCreditCards == null) {
      this.acceptedCreditCards = new ArrayList<>();
    }
    this.acceptedCreditCards.add(acceptedCreditCardsItem);
    return this;
  }

  /**
   * Get acceptedCreditCards
   * @return acceptedCreditCards
   */
  @Valid 
  @Schema(name = "acceptedCreditCards", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("acceptedCreditCards")
  public List<@Valid AcceptedCreditCardDto> getAcceptedCreditCards() {
    return acceptedCreditCards;
  }

  public void setAcceptedCreditCards(List<@Valid AcceptedCreditCardDto> acceptedCreditCards) {
    this.acceptedCreditCards = acceptedCreditCards;
  }

  public HotelPaymentInformationDto address(AddressDto address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @Valid 
  @Schema(name = "address", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("address")
  public AddressDto getAddress() {
    return address;
  }

  public void setAddress(AddressDto address) {
    this.address = address;
  }

  public HotelPaymentInformationDto paymentMethodsOpera(Map<String, String> paymentMethodsOpera) {
    this.paymentMethodsOpera = paymentMethodsOpera;
    return this;
  }

  public HotelPaymentInformationDto putPaymentMethodsOperaItem(String key, String paymentMethodsOperaItem) {
    if (this.paymentMethodsOpera == null) {
      this.paymentMethodsOpera = new HashMap<>();
    }
    this.paymentMethodsOpera.put(key, paymentMethodsOperaItem);
    return this;
  }

  /**
   * Get paymentMethodsOpera
   * @return paymentMethodsOpera
   */
  
  @Schema(name = "paymentMethodsOpera", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentMethodsOpera")
  public Map<String, String> getPaymentMethodsOpera() {
    return paymentMethodsOpera;
  }

  public void setPaymentMethodsOpera(Map<String, String> paymentMethodsOpera) {
    this.paymentMethodsOpera = paymentMethodsOpera;
  }

  public HotelPaymentInformationDto paymentProviders(List<@Valid PaymentProviderDto> paymentProviders) {
    this.paymentProviders = paymentProviders;
    return this;
  }

  public HotelPaymentInformationDto addPaymentProvidersItem(PaymentProviderDto paymentProvidersItem) {
    if (this.paymentProviders == null) {
      this.paymentProviders = new ArrayList<>();
    }
    this.paymentProviders.add(paymentProvidersItem);
    return this;
  }

  /**
   * Get paymentProviders
   * @return paymentProviders
   */
  @Valid 
  @Schema(name = "paymentProviders", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("paymentProviders")
  public List<@Valid PaymentProviderDto> getPaymentProviders() {
    return paymentProviders;
  }

  public void setPaymentProviders(List<@Valid PaymentProviderDto> paymentProviders) {
    this.paymentProviders = paymentProviders;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HotelPaymentInformationDto hotelPaymentInformationDto = (HotelPaymentInformationDto) o;
    return Objects.equals(this.acceptedCreditCards, hotelPaymentInformationDto.acceptedCreditCards) &&
        Objects.equals(this.address, hotelPaymentInformationDto.address) &&
        Objects.equals(this.paymentMethodsOpera, hotelPaymentInformationDto.paymentMethodsOpera) &&
        Objects.equals(this.paymentProviders, hotelPaymentInformationDto.paymentProviders);
  }

  @Override
  public int hashCode() {
    return Objects.hash(acceptedCreditCards, address, paymentMethodsOpera, paymentProviders);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HotelPaymentInformationDto {\n");
    sb.append("    acceptedCreditCards: ").append(toIndentedString(acceptedCreditCards)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    paymentMethodsOpera: ").append(toIndentedString(paymentMethodsOpera)).append("\n");
    sb.append("    paymentProviders: ").append(toIndentedString(paymentProviders)).append("\n");
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

